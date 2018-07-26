package test03.decision_table;

import java.io.IOException;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

/**
 * 测试 决策表的编译和执行
 * @author zhangqingli
 *
 */
public class Test01_DecisionTable_Compile_Execute {
	
	public KieSession getStatefulKieSession(String kieSessionName) {
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		KieServices kieServices = KieServices.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		return kieContainer.newKieSession(kieSessionName);
	}
	public void focusStatefulKieSession(KieSession kieSession, String agendaGroup) {
		kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
	}
	
	/**
	 * 测试 决策表的编译
	 * 
	 */
	@Test
	public void test01() throws IOException {
		SpreadsheetCompiler compiler = new SpreadsheetCompiler();
		String rules = compiler.compile(ResourceFactory.newClassPathResource("rules/test03/decision_table_01.xls"), InputType.XLS);
		System.out.println(rules);
	}
	
	
	/**
	 * 测试 决策表的执行
	 * 决策表规则的触发执行API和普通DRL文件的基本一致
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getStatefulKieSession("test03-rules-ksession");
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}
