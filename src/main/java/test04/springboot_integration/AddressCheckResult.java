package test04.springboot_integration;

/**
 * 规则返回结果类
 * @author zhangqingli
 *
 */
public class AddressCheckResult {
	private boolean postCodeResult = false; // true:通过校验；false：未通过校验

	public boolean isPostCodeResult() {
		return postCodeResult;
	}
	public void setPostCodeResult(boolean postCodeResult) {
		this.postCodeResult = postCodeResult;
	}
}
