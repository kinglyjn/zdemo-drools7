package test02.rules;

/**
 * 用户账户
 * @author zhangqingli
 *
 */
public class Account {
	private int balance; 	//余额
	
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
	
	@Override
	public String toString() {
		return "Account [balance=" + balance + "]";
	}
}
