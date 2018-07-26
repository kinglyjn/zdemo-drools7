package test01.hello;

import java.util.Arrays;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;

public class Test01 {
	
	/**
	 * 获取一个kieSession
	 * 此处匹配的规则满足 agenda-group=xxx 或 agenda-group=null（drl文件中rule没有设置agenda-group属性）
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends CommandExecutor> T getKieSession(String kieSessionName, String sessionState) { //sessionState = statefull|stateless
		KieServices kieServices = KieServices.Factory.get(); 
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		if ("statefull".equals(sessionState)) {
			return (T) kieContainer.newKieSession(kieSessionName);
		} else if ("stateless".equals(sessionState)) {
			return (T) kieContainer.newStatelessKieSession(kieSessionName);
		} else {
			throw new IllegalArgumentException("参数sessionState的值不是 statefull或stateless其中之一！");
		}
	}
	
	/**
	 * 获取一个聚焦到某个规则组的有状态kieSession
	 * 此处匹配的规则满足 agenda-group=xxx
	 * <br>
	 * Agenda Group是用来在Agenda基础上对规则进行再次分组，可通过为规则添加agenda-group属性来实现。
	 * agenda-group属性的值是一个字符串，通过这个字符串，可以将规则分为若干个Agenda Group。引擎在
	 * 调用设置了agenda-group属性的规则时需要显示的指定某个Agenda Group得到Focus（焦点），否则将
	 * 不执行该Agenda Group当中的规则。
	 * 
	 */
	public KieSession getFocusedStatefulKieSession(String kieSessionName, String agendaGroup) {
		KieSession kieSession = (KieSession) getKieSession(kieSessionName, "statefull");
		kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
		return kieSession;
	}
	
	
	/**
	 * 测试 drools基本使用
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getFocusedStatefulKieSession("all-rules-ksession", "test01.hello");
		
		User user1 = new User("张三");
		Account account1 = new Account(9000);
		account1.setUser(user1);
		
		User user2 = new User("小娟");
		Account account2 = new Account(11000);
		account2.setUser(user2);
		
		kieSession.insert(account1);
		kieSession.insert(account2);
		
		int count = kieSession.fireAllRules();
		kieSession.dispose();
		
		System.out.println("Fired " + count + " rules.");
		System.out.println("account1=" + account1);
		System.out.println("account2=" + account2);
	}
	
	
	/**
	 * 测试 FactHandle
	 * 
	 */
	@Test
	public void test02() {
		KieSession kieSession = getFocusedStatefulKieSession("all-rules-ksession", "test01.hello");
		
		User user = new User("张三");
		Account account = new Account(9000);
		account.setUser(user);
		
		FactHandle accountFactHandler = kieSession.insert(account); //org.drools.core.common.DefaultFactHandle
		int count = kieSession.fireAllRules();
		System.out.println("Fired " + count + " rules.");
		System.out.println("account=" + account);
		
		//format_version:id:identity:hashcode:recency（0:1:488600086:488600086:1:DEFAULT:NON_TRAIT:test01.hello.Account）
		String externalForm = accountFactHandler.toExternalForm();
		System.out.println(externalForm);
		
		// 重新获取Account对象
		Account account2 = (Account) kieSession.getObject(accountFactHandler);
		System.out.println(account2==account); //true
		account2.setBalance(11000);
		kieSession.update(accountFactHandler, account2);
		
		// KieSession重新聚焦执行匹配规则
		kieSession.getAgenda().getAgendaGroup("test01.hello").setFocus();
		count = kieSession.fireAllRules();
		System.out.println("Fired " + count + " rules.");
		System.out.println("account=" + account);
		
		// 关闭kieSession
		kieSession.dispose();
	}
	
	
	/**
	 * 测试 无状态kieSession
	 * kmodule中的ksession默认的定义是有状态的（type=stateful，StatefulKnowledgeSession），
	 * 有状态的session可以利用Working Memory执行多次，而无状态的session（type=stateless，StatelessKieSession）则只能执行一次。
	 * StatelessKieSession是对KieSession的封装，不需要再调用dispose方法进行session的关闭。它隔离了每次规则与引擎的交互，不会再去
	 * 维护会话的状态，同时也不再提供fileAllRules方法。
	 * 
	 * [注意]
	 * StatelessKieSession只能用在没有标识agenda-group属性的那些规则上！
	 * 
	 * 无状态session使用的场景：
	 * 1.数据校验
	 * 2.运算
	 * 3.数据过滤
	 * 4.消息路由
	 * 5.任何能被描述成函数公式的规则
	 * 
	 */
	@Test
	public void test03() {
		StatelessKieSession statelessKieSession = (StatelessKieSession) getKieSession("test01-rules-stateless-ksession", "stateless");
		System.out.println(statelessKieSession.getClass()); //StatelessKnowledgeSessionImpl
		
		User user1 = new User("张三");
		Account account1 = new Account(9000);
		account1.setUser(user1);
		
		User user2 = new User("小娟");
		Account account2 = new Account(11000);
		account2.setUser(user2);
		
		//statelessKieSession.execute(account1);
		//KieCommands commands = KieServices.get().getCommands();
		//Command<Account> command = commands.newInsertElements(Arrays.asList(new Account[] {account1,account2}));
		//statelessKieSession.execute(command);
		statelessKieSession.execute(Arrays.asList(account1, account2));
	}
	
}
