package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 测试 全局变量
 * @author zhangqingli
 *
 */
public class Test09_Global {
	
	public KieSession getStatefulKieSession(String kieSessionName) {
		KieServices kieServices = KieServices.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		return kieContainer.newKieSession(kieSessionName);
	}
	public void focusStatefulKieSession(KieSession kieSession, String agendaGroup) {
		kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
	}
	
	
	/**
	 * 测试 调用变量的静态方法
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-global-01");
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 调用变量的普通方法
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-global-02");
		
		//通过kieSession设置全局变量
		kieSession.setGlobal("emailService2", new SendEmailService());
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}
