/*
* EE422C Final Project submission by
* Hamza Shaikh
* hms2659
* 17835
* Spring 2021
* Slip Days Used: 0
*/

public class Item {
	private String name;
	private String description;
	private String price;
	private String maxPrice;
	private String bidder;
	private boolean isSold;
	
	public Item(String[] data) {
		name = data[0];
		description = data[1];
		price = data[2];
		maxPrice = data[3];
		bidder = null;
		isSold = false;
	}
	
	public Item(Item data) {
		this.name = data.getName();
		this.description = data.getDescription();
		this.price = data.getPrice();
		this.maxPrice = data.getMaxPrice();
		this.bidder = data.getBidder();
		this.isSold = data.isSold();
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(((Item)obj).getName());
	}

	@Override
	public String toString() {
		return name + ": " + description + " Bidded Price: $" + price + " Bidder: " + bidder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public boolean isSold() {
		return isSold;
	}

	public void setSold(boolean isSold) {
		this.isSold = isSold;
	}

	public String getBidder() {
		return bidder;
	}

	public void setBidder(String bidder) {
		this.bidder = bidder;
	}
	
}
