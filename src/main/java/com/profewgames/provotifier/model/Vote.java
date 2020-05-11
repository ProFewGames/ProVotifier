package com.profewgames.provotifier.model;

public class Vote {

	private String serviceName;
	private String username;
	private String address;
	private String timeStamp;

	@Override
	public String toString() {
		return "Vote (from:" + this.serviceName + " username:" + this.username + " address:" + this.address
				+ " timeStamp:" + this.timeStamp + ")";
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setUsername(final String username) {
		this.username = ((username.length() <= 16) ? username : username.substring(0, 16));
	}

	public String getUsername() {
		return username;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setTimeStamp(final String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
}