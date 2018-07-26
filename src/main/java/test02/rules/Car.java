package test02.rules;

public class Car {
	private Customer owner;
	private Boolean freeParking;
	
	public Customer getOwner() {
		return owner;
	}
	public void setOwner(Customer owner) {
		this.owner = owner;
	}
	public Boolean getFreeParking() {
		return freeParking;
	}
	public void setFreeParking(Boolean freeParking) {
		this.freeParking = freeParking;
	}
	@Override
	public String toString() {
		return "Car [owner=" + owner + ", freeParking=" + freeParking + "]";
	}
}