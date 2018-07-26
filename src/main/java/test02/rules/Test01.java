package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class Test01 {

	
	public KieSession getStatefulKieSession(String kieSessionName) {
		KieServices kieServices = KieServices.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		return kieContainer.newKieSession(kieSessionName);
	}
	/**
	 *  注意：
	 *  1.如果想要触发某个分组的规则，首先需要ksession获取该分组的焦点
	 *  2.一个kiesession某分组的焦点只能被执行一次，即使该分组再次获取焦点，执行触发任务，该分组的规则也不会被执行
	 *  3.没有分组的规则，无论相应的kiesession有没有获取焦点，都会被触发
	 *  4.设置规则rule的分组时agenda-group和ruleflow-group的作用是相同的
	 *  
	 */
	public void focusStatefulKieSession(KieSession kieSession, String agendaGroup) {
		kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
	}
	
	/**
	 * 测试 默认循环执行（no-loop=false）
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-loop");
		
		Account account = new Account(80);
		kieSession.insert(account);
		
		int count = kieSession.fireAllRules();
		System.out.println("Fired " + count + " rules."); //按自定义规则执行20次
		System.out.println(account);
		
		kieSession.dispose();
	}
	
	/**
	 * 测试 非循环执行（no-loop=true）
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-noloop");
		
		Account account = new Account(80);
		kieSession.insert(account);
		
		int count = kieSession.fireAllRules();
		System.out.println("Fired " + count + " rules."); //按自定义规则执行1次
		System.out.println(account);
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 lock-on-active
	 * 
	 */
	@Test
	public void test03() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-lock-on-active");
		
		Account account = new Account(80);
		kieSession.insert(account);
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 静态和动态salience
	 * 
	 */
	@Test
	public void test04() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-salience");
		
		Account account = new Account(80);
		kieSession.insert(account);
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 actication-group
	 * 
	 */
	@Test
	public void test05() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-activation-group-demo");
		kieSession.fireAllRules();
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 auto-focus（自动获取焦点）
	 * 
	 */
	@Test
	public void test06() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		kieSession.fireAllRules();
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 date-effictive 和 date-expires
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void test07() throws InterruptedException {
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-date-effictive-expires");
		
		kieSession.fireAllRules();
		
		kieSession.dispose();
	}
}
