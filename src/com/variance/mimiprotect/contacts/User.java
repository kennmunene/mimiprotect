package com.variance.mimiprotect.contacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.variance.mimiprotect.util.Utils;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.android.annotations.CollectionElement;
import com.variance.vjax.android.annotations.GenericCollectionType;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1051210119470629419L;
	private String userName;
	private String password;
	private String surname;
	private String otherNames;
	private String msisdn;
	private String email;
	@CollectionElement(elementMarkup = "phone")
	@GenericCollectionType(String.class)
	private List<String> phones;
	@CollectionElement(elementMarkup = "email")
	@GenericCollectionType(String.class)
	private List<String> emails;
	private String id;
	private String organization;
	private String title;
	/**
	 * The user contact id, on which livelink is performed.
	 */
	private String userContactId;

	public User() {
	}

	public User(String userName, String password, String surname,
			String otherNames, String msisdn, String email,
			List<String> phones, List<String> emails, String id,
			String organization, String title, String userContactId) {
		super();
		this.userName = userName;
		this.password = password;
		this.surname = surname;
		this.otherNames = otherNames;
		this.msisdn = msisdn;
		this.email = email;
		this.phones = phones;
		this.emails = emails;
		this.id = id;
		this.organization = organization;
		this.title = title;
		this.userContactId = userContactId;
	}

	public User(String userName, String password, String surname,
			String otherNames, String msisdn, String email,
			List<String> phones, List<String> emails, String id,
			String organization, String title) {
		super();
		this.userName = userName;
		this.password = password;
		this.surname = surname;
		this.otherNames = otherNames;
		this.msisdn = msisdn;
		this.email = email;
		this.phones = phones;
		this.emails = emails;
		this.id = id;
		this.organization = organization;
		this.title = title;
	}

	public User(String userName, String password, String surname,
			String otherNames, List<String> phones, List<String> emails,
			String id) {
		super();
		this.userName = userName;
		this.password = password;
		this.surname = surname;
		this.otherNames = otherNames;
		this.phones = phones;
		this.emails = emails;
		this.id = id;
	}

	public User(String userName, String password, List<String> phones,
			List<String> emails, String id) {
		super();
		this.userName = userName;
		this.password = password;
		this.phones = phones;
		this.emails = emails;
		this.id = id;
	}

	public String getUserContactId() {
		return userContactId;
	}

	public void setUserContactId(String userContactId) {
		this.userContactId = userContactId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getOtherNames() {
		return otherNames;
	}

	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getPhones() {
		if (phones == null) {
			phones = new ArrayList<String>();
		}
		return phones;
	}

	public void addPhone(String phone) {
		if (!getPhones().contains(phone)) {
			getPhones().add(0, phone);
		}
	}

	public void addPhone(String phone, int index) {
		if (!getPhones().contains(phone)) {
			getPhones().set(index, phone);
		}
	}

	public void addEmail(String email) {
		if (!getEmails().contains(email)) {
			getEmails().add(email);
		}
	}

	public void addEmail(String email, int index) {
		if (!getEmails().contains(email)) {
			getEmails().set(index, email);
		}
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public List<String> getEmails() {
		if (emails == null) {
			emails = new ArrayList<String>();
		}
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstEmail() {
		if (email != null && !"".equals(email)) {
			return email;
		}
		return emails.isEmpty() ? null : emails.get(0);
	}

	public String getFirstPhone() {
		if (Utils.isNullStringOrEmpty(msisdn)) {
			for (String phone : phones) {
				if (!Utils.isNullStringOrEmpty(phone)) {
					return phone;
				}
			}
		}
		return msisdn;
	}

	public String serialize() {
		return new VObjectMarshaller<User>(User.class).doMarshall(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (msisdn == null) {
			if (other.msisdn != null)
				return false;
		} else if (!msisdn.equals(other.msisdn))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
