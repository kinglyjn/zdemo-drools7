package test04.springboot_integration;

/**
 * 实体类
 * @author zhangqingli
 *
 */
public class Address {
	private String state;
    private String street;
    private String postcode;
	
    public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
}
