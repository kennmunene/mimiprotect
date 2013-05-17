package com.variance.mimiprotect.contacts.business;

import java.util.ArrayList;
import java.util.List;

public class BusinessInformation {
	private String businessName;
	private String country;
	private String address;
	private String postcode;
	private String website;
	private List<String> phones;
	private List<String> emails;

	public BusinessInformation() {
	}

	public BusinessInformation(String businessName, String country,
			String address, String postcode, String website) {
		super();
		this.businessName = businessName;
		this.country = country;
		this.address = address;
		this.postcode = postcode;
		this.website = website;
	}

	public void addPhone(String phone) {
		getPhones().add(phone);
	}

	public void addEmail(String email) {
		getEmails().add(email);
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<String> getPhones() {
		if (phones == null) {
			phones = new ArrayList<String>() {
				private static final long serialVersionUID = -2965811982086178718L;

				public boolean add(String e) {
					if (e != null && !"".equals(e) && !contains(e)) {
						super.add(e);
						return true;
					}
					return false;
				}
			};
		}
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public List<String> getEmails() {
		if (emails == null) {
			emails = new ArrayList<String>() {
				private static final long serialVersionUID = -9103550712865860584L;

				public boolean add(String e) {
					if (e != null && !"".equals(e) && !contains(e)) {
						super.add(e);
						return true;
					}
					return false;
				}
			};
		}
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

}
