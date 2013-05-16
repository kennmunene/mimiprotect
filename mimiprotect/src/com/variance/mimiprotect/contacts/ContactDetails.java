/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.mimiprotect.contacts;

/**
 * 
 * @author kenn
 */
public class ContactDetails {

	private String name;
	private String phoneNumbers[];
	private String website;
	private String address;
	private String email;

	public ContactDetails() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String phoneName) {
		this.name = phoneName;
	}

	public String[] getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(String[] phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDefaultPhoneNumber() {
		String[] numbers = getPhoneNumbers();
		return numbers[0];
	}
}
