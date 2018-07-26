package test02.rules;

import java.util.Date;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 * 测试 在规则文件中声明 POJO
 * @author zhangqingli
 *
 */
public class Test10_DeclareClassInDrl {
	
	public KieSession getStatefulKieSession(String kieSessionName) {
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		KieServices kieServices = KieServices.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		return kieContainer.newKieSession(kieSessionName);
	}
	public void focusStatefulKieSession(KieSession kieSession, String agendaGroup) {
		kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
	}
	public KieContainer getKieContainer() { //KieContainer是单例的
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		KieServices kieServices = KieServices.get();
		return kieServices.getKieClasspathContainer();
	}
	public KieBase getKieBaseByName(String kieBaseName) { //KieBase也是单例的
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		return KieServices.get().getKieClasspathContainer().getKieBase(kieBaseName);
	}
	
	
	/**
	 * 测试 在规则文件中声明简单的POJO
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-declare-class-in-drl");
		
		kieSession.fireAllRules();
		
		QueryResults queryResults = kieSession.getQueryResults("getAddress");
		for (QueryResultsRow queryResultsRow : queryResults) {
			//此处Address类是在规则文件中定义的，动态生成的，类型名称为 ${rule_package}.Address
			Object address = queryResultsRow.get("$address");
			System.out.println(address.getClass());
		}
		
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 JAVA API 操作drl声明的类型和对象
	 * 
	 */
	@Test
	public void test02() throws InstantiationException, IllegalAccessException {
		KieContainer kieContainer = getKieContainer();
		KieBase kieBase = kieContainer.getKieBase("test02-rules-kbase");
		
		// 通过反射，遍历，拿到对应的city枚举值
		Object targetCity = null;
		FactType cityFactType = kieBase.getFactType("rules.test02", "City");
		Class<?> cityClass = cityFactType.getFactClass(); //rules.test02.City
		Object[] enumConstants = cityClass.getEnumConstants();
		for (Object city : enumConstants) {
			if (city.toString().equals("GUANGZHOU")) {
				targetCity = city;
				break;
			}
		}
		
		// 初始化目标对象
		// init address: Address( countryName=China, city=GUANGZHOU, street=HuaYuanLu, time=Fri Jul 20 14:30:45 CST 2018 )
		FactType addressFactType = kieBase.getFactType("rules.test02", "Address");
		Object address = addressFactType.newInstance();
		addressFactType.set(address, "countryName", "China");
		addressFactType.set(address, "city", targetCity);
		addressFactType.set(address, "street", "HuaYuanLu");
		addressFactType.set(address, "time", new Date());
		System.out.println("init address: " + address + "\n");
		
		// 将初始化的目标对象insert到工作内存中
		KieSession kieSession = kieContainer.newKieSession("test02-rules-ksession");
		kieSession.getAgenda().getAgendaGroup("test02-declare-class-in-drl").setFocus();
		kieSession.insert(address);
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}
