package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * 测试 使用quartz日期实现类的日历
 * @author zhangqingli
 *
 */
public class Test05RHS_Update_Modify {
	
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
	 * 测试 RHS update操作
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-update");
		
		Account account = new Account(100);
		kieSession.insert(account);
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 KieSession API update操作
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		
		Account account = new Account(100);
		FactHandle accountFactHandle = kieSession.insert(account);
		focusStatefulKieSession(kieSession, "test02-kiesession-update");
		kieSession.fireAllRules();
		
		account = (Account) kieSession.getObject(accountFactHandle);
		account.setBalance(200);
		kieSession.update(accountFactHandle, account);
		focusStatefulKieSession(kieSession, "test02-kiesession-update");
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 RHS modify操作
	 * 
	 */
	@Test
	public void test03() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-modify");
		
		Account account = new Account(100);
		kieSession.insert(account);
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
}









