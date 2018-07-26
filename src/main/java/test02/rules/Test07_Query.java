package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 * 测试 查询
 * @author zhangqingli
 *
 */
public class Test07_Query {
	
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
	 * 测试 无参查询 和 有参查询
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-query");
		
		Account account1 = new Account(100);
		Account account2 = new Account(200);
		kieSession.insert(account1);
		kieSession.insert(account2);
		kieSession.fireAllRules();
		
		// 无参查询
		System.out.println("---------------");
		QueryResults queryResults = kieSession.getQueryResults("query-account-with-balance-equals-100");
		for (QueryResultsRow queryResultsRow : queryResults) {
			System.out.println(queryResultsRow.get("$account")); //此处的参数 identifier就是query中定义的要查询对象的句柄名称
		}

		//有参查询
		System.out.println("---------------");
		QueryResults queryResults2 = kieSession.getQueryResults("query-account-by-balance-greater-than", 100);
		for (QueryResultsRow queryResultsRow : queryResults2) {
			System.out.println(queryResultsRow.get("$account")); 
		}
		
		kieSession.dispose();
	}
}
