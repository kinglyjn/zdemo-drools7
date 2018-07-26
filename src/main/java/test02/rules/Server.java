package test02.rules;

public class Server {
	private int warnCount = 0;
	private String message;

	public int getWarnCount() {
		return warnCount;
	}
	public void setWarnCount(int warnCount) {
		this.warnCount = warnCount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Server [warnCount=" + warnCount + ", message=" + message + "]";
	}
}
