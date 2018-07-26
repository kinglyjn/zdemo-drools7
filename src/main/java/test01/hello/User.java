package test01.hello;

/**
 * 用户类型
 * @author zhangqingli
 *
 */
public class User {
	private String name;
	private String type; //账户类型 VIP|COMMON
	
	public User(String name) {
		this.name = name;
	}
	public User() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", type=" + type + "]";
	}
}
