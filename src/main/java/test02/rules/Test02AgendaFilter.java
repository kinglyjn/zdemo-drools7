package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * 测试 AgendaFilter
 * @author zhangqingli
 *
 */
public class Test02AgendaFilter {
	
	static class MyAgendaFilter implements AgendaFilter {
		private String ruleName;
		public MyAgendaFilter(String ruleName) {
	        this.ruleName = ruleName;
	    }
		public String getRuleName() {
			return ruleName;
		}
		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}

		/**
		 * 对于规则的执行的控制，还可以使用org.kie.api.runtime.rule. AgendaFilter
		 * 来实现。用户可以实现该接口的accept方法，通过规则当中的属性值来控制是否执行规则。 
		 * 
		 */
		@Override
		public boolean accept(Match match) {
			System.out.println("rule name: " + match.getRule().getName());
			return match.getRule().getName().equals(ruleName);
		}
	}
	
	
	
	public KieSession getStatefulKieSession(String kieSessionName) {
		KieServices kieServices = KieServices.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		return kieContainer.newKieSession(kieSessionName);
	}
	
	
	/**
	 * 只有符合过滤条件的规则才能被执行
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		kieSession.getAgenda().getAgendaGroup("test02-agenda-filter").setFocus();
		
		AgendaFilter agendaFilter = new MyAgendaFilter("test02-agenda-filter-01");
		kieSession.fireAllRules(agendaFilter);
		
		kieSession.dispose();
	}
}
