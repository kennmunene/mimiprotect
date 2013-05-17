package com.variance.mimiprotect.ad;

public class AdRequestInfo {
	private double latitude;
	private double longitude;
	private int requestIndex;

	public AdRequestInfo() {
		super();
	}

	public AdRequestInfo(double latitude, double longitude, int requestIndex) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.requestIndex = requestIndex;
	}

	public int getRequestIndex() {
		return requestIndex;
	}

	public void setRequestIndex(int requestIndex) {
		this.requestIndex = requestIndex;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
