package com.example.AzureEventHub.SendData;

public class Container {
	private String containerId;
	private String location;
	private String expectedDelivery;
	private String containerSourcePort;
	private String containerDestinationPort;
	private String shipper;
	private String consignee;
	private String maerskcustomerCode;
	private String region;
	private String geoocationPresent;
	
	
	public String getContainerSourcePort() {
		return containerSourcePort;
	}
	public void setContainerSourcePort(String containerSourcePort) {
		this.containerSourcePort = containerSourcePort;
	}
	public String getContainerDestinationPort() {
		return containerDestinationPort;
	}
	public void setContainerDestinationPort(String containerDestinationPort) {
		this.containerDestinationPort = containerDestinationPort;
	}
	public String getShipper() {
		return shipper;
	}
	public void setShipper(String shipper) {
		this.shipper = shipper;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getExpectedDelivery() {
		return expectedDelivery;
	}
	public void setExpectedDelivery(String expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getContainerId() {
		return containerId;
	}
	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
	public String getMaerskcustomerCode() {
		return maerskcustomerCode;
	}
	public void setMaerskcustomerCode(String maerskcustomerCode) {
		this.maerskcustomerCode = maerskcustomerCode;
	}
	public String getGeoocationPresent() {
		return geoocationPresent;
	}
	public void setGeoocationPresent(String geoocationPresent) {
		this.geoocationPresent = geoocationPresent;
	}

}
