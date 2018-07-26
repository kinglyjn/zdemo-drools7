package test02.rules;

import java.util.ArrayList;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * 测试 定时器的使用
 * @author zhangqingli
 *
 */
public class Test03Timer {

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
	 * 模拟的系统报警器来示例一下Timer的使用。规则timer每隔一秒执行一次，
	 * 当满足触发规则返回结果至events对象中，业务系统拿到报警信息，并打印。
	 * 
	 */
	@Test
	public void test01() throws InterruptedException {
		final KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		
		//设置global变量events，用于存放每次规则触发自定义保存的信息
		kieSession.setGlobal("events", new ArrayList<String>());
		
		//开启一个新的线程用于定时触发任务
		new Thread(new Runnable() {
			@Override
			public void run() {
				kieSession.fireUntilHalt(); //Keeps firing Matches until a halt is called.
			}
		}).start();
		
		//更新 server warnCount，使其逐渐达到规则的处触发条件
		Server server = new Server();
		FactHandle serverFactHandle = kieSession.insert(server);
		for (int i = 0; i <= 5; i++) {
			Thread.sleep(1000);
			server.setWarnCount(i);
			kieSession.update(serverFactHandle, server);
		}
		
		//5秒钟后关闭规则的定时触发，并结束规则的定时触发
		Thread.sleep(5000);
		kieSession.halt();
		System.out.println("server: " + server);
		System.out.println("event: " + kieSession.getGlobal("events"));
		
		kieSession.dispose();
	}
	
}
