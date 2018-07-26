package test02.rules;

/**
 * 全局服务：邮件
 * @author zhangqingli
 *
 */
public class SendEmailService {
	
	public static void send(String message) {
		System.out.println(message);
	}
	
	public void send2(String message) {
		System.out.println(message);
	}
}
