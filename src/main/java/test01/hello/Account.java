package test01.hello;

/**
 * 用户账户
 * @author zhangqingli
 *
 */
public class Account {
	private int balance; 	//余额
	private User user;		//用户
	
	public Account(int balance) {
		this.balance = balance;
	}
	public Account() {
		super();
	}

	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return "Account [balance=" + balance + ", user=" + user + "]";
	}
}
