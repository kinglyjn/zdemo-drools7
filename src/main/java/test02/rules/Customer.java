package test02.rules;

public class Customer {
	private String name;
	private int age;
	private String type;
	private float discount;
	
	public Customer(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Customer [name=" + name + ", age=" + age + ", type=" + type + ", discount=" + discount + "]";
	}
}
