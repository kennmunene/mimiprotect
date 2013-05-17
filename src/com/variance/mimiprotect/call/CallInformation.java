package com.variance.mimiprotect.call;

import java.util.Calendar;

import android.telephony.CellLocation;

public class CallInformation {
	private String clientId;
	private String phoneDialed;
	private Calendar dialedDate;
	private Calendar endDate;
	private CellLocation cellLocation;
	private String caller_msisdn;
	private double latitude;
	private double longitude;
	private String imei; // gsm imei cdma meid

	public CallInformation() {
		super();
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation) {
		this();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn, String imei) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
		this.imei = imei;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn, double latitude, double longitude, String imei) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imei = imei;
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCaller_msisdn() {
		return caller_msisdn;
	}

	public void setCaller_msisdn(String caller_msisdn) {
		this.caller_msisdn = caller_msisdn;
	}

	public CellLocation getCellLocation() {
		return cellLocation;
	}

	public void setCellLocation(CellLocation cellLocation) {
		this.cellLocation = cellLocation;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPhoneDialed() {
		return phoneDialed;
	}

	public void setPhoneDialed(String phoneDialed) {
		this.phoneDialed = phoneDialed;
	}

	public Calendar getDialedDate() {
		return dialedDate;
	}

	public void setDialedDate(Calendar dialedDate) {
		this.dialedDate = dialedDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

}
