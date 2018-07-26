package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 测试 函数
 * @author zhangqingli
 *
 */
public class Test08_Function {
	
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
	 * 测试 函数
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-function");
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}
