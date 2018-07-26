package test04.springboot_integration;

import java.io.IOException;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


/**
 * springboot的初始化配置
 * @author zhangqingli
 *
 */
@Configuration
public class DroolsAutoConfiguration {
	
	private static final String RULES_PATH = "rules/test04/";
	
	
	private KieServices getKieServices() {
        return KieServices.Factory.get();
    }
	
	private Resource[] getRuleFiles() throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
	}
	
	@Bean
	@ConditionalOnMissingBean(KieFileSystem.class)
	public KieFileSystem keyFileSystem() throws IOException {
		KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        for (Resource file : getRuleFiles()) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
        }        
        return kieFileSystem;
	}
	
	@Bean
	@ConditionalOnMissingBean(KieContainer.class)
	public KieContainer kieContainer() throws IOException {
		final KieRepository kieRepository = getKieServices().getRepository();
		kieRepository.addKieModule(new KieModule() {
			@Override
			public ReleaseId getReleaseId() {
				return kieRepository.getDefaultReleaseId();
			}
		});
		KieBuilder kieBuilder = getKieServices().newKieBuilder(keyFileSystem());
		kieBuilder.buildAll();
		return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
	}
	
	@Bean
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }
	
	@Bean
    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSession() throws IOException {
        return kieContainer().newKieSession();
    }
	
	@Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }
}
