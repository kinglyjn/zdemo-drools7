package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 测试 extends关键字 和 do标记代码
 * @author zhangqingli
 *
 */
public class Test06_Extends_and_Do_Mark {
	
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
	 * 测试 条件的继承（extends）
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-extends");
		
		Customer customer = new Customer("张三", 70);
		Car car = new Car();
		car.setOwner(customer);
		kieSession.insert(customer);
		kieSession.insert(car);
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 do标记代码
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-do-mark");
		
		Customer customer = new Customer("张三", 70);
		customer.setType("Silver");
		Car car = new Car();
		car.setOwner(customer);
		kieSession.insert(customer);
		kieSession.insert(car);
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}









