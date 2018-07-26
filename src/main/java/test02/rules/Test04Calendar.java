package test02.rules;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.Calendar;
import org.quartz.impl.calendar.WeeklyCalendar;

/**
 * 测试 使用quartz日期实现类的日历
 * @author zhangqingli
 *
 */
public class Test04Calendar {
	
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
	 * 使用quartz日期实现类的包装对象
	 * @author zhangqingli
	 *
	 */
	static class CalendarWrapper implements Calendar {
		private WeeklyCalendar calenar;
		public CalendarWrapper(WeeklyCalendar calenar) {
			this.calenar = calenar;
		}
		@Override
		public boolean isTimeIncluded(long timestamp) {
			return calenar.isTimeIncluded(timestamp);
		}
	}
	
	/**
	 * 测试 使用日历定义规则
	 * 下面的示例中，当时间处于每周四的时候，符合条件的规则也不会被触发
	 * 
	 */
	@Test
	public void test01() {
		KieSession kieSession = getStatefulKieSession("test02-rules-ksession");
		focusStatefulKieSession(kieSession, "test02-calendar");
		
		// 给kieSession设置日期
		WeeklyCalendar weeklyCalendar = new WeeklyCalendar();
		weeklyCalendar.setDayExcluded(java.util.Calendar.THURSDAY, true); //设置为不包含周四那一天
		kieSession.getCalendars().set("weekday", new CalendarWrapper(weeklyCalendar));
		
		kieSession.fireAllRules();
		kieSession.dispose();
	}
}









