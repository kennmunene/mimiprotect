package com.variance.mimiprotect.contacts;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Log;

import com.variance.mimiprotect.chat.ChatStatus;
import com.variance.vjax.android.annotations.ArrayParented;
import com.variance.vjax.android.annotations.Transient;

public class Contact implements Serializable, Comparable<Contact> {
	public static enum ContactType {
		PERSONAL, BUSINESS, CORPORATE;
	}

	public static String hash(String vcard) {
		try {
			byte[] bytes = vcard.getBytes("UTF-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(bytes);
			return new String(digest);
		} catch (Exception ex) {
			Logger.getLogger(Contact.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1368047403499385522L;
	private String id;
	private String name;
	@ArrayParented(componentMarkup = "phone")
	private String[] phones;
	@ArrayParented(componentMarkup = "email")
	private String[] emails;
	private String website;
	private String address;
	@Transient
	private String livelinkId;
	private String country;
	private String group;
	@Transient
	private String message; // only for business listing
	@Transient
	private String companyName; // only for business listing
	private String organization; // only for personal contacts
	private String title; // only for personal contacts
	// the contact id for which this contact was discovered from, if this is a
	// first degree connection
	@Transient
	private String contactId;
	// the contact name for which this contact was discovered from, if this is a
	// first degree connection. Used to show the current user, from whom this
	// contact was discovered from.
	@Transient
	private String contactName;
	@Transient
	private boolean corporateContact;
	@Transient
	private boolean businessContact;
	@Transient
	private boolean businessContactHeaderStart;
	@Transient
	private boolean personalContactHeaderStart;
	@Transient
	private boolean personalContactGroupHeaderStart;
	@Transient
	private boolean noContactFound;
	/**
	 * If true, then this contact is a first degree connection to current users.
	 */
	@Transient
	private boolean firstDegreeConnection;

	@Transient
	private int autoid;
	@Transient
	private int contactType;
	/**
	 * Important only if we are on chat.
	 */
	@Transient
	private int port;
	@Transient
	private String chatId;
	@Transient
	private ChatStatus chatStatus;

	public ChatStatus getChatStatus() {
		return chatStatus;
	}

	public void setChatStatus(ChatStatus chatStatus) {
		this.chatStatus = chatStatus;
	}

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public int getContactType() {
		return contactType;
	}

	public void setContactType(int contactType) {
		this.contactType = contactType;
	}

	public Contact() {
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String livelinkId, String country,
			String group, boolean corporateContact, boolean businessContact,
			boolean businessContactHeaderStart,
			boolean personalContactHeaderStart, boolean firstDegreeConnection,
			String contactId, String contactName) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.livelinkId = livelinkId;
		this.country = country;
		this.group = group;
		this.corporateContact = corporateContact;
		this.businessContact = businessContact;
		this.businessContactHeaderStart = businessContactHeaderStart;
		this.personalContactHeaderStart = personalContactHeaderStart;
		this.firstDegreeConnection = firstDegreeConnection;
		this.contactId = contactId;
		this.contactName = contactName;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String livelinkId, String country,
			String group, boolean corporateContact, boolean businessContact,
			boolean businessContactHeaderStart,
			boolean personalContactHeaderStart) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.livelinkId = livelinkId;
		this.country = country;
		this.group = group;
		this.corporateContact = corporateContact;
		this.businessContact = businessContact;
		this.businessContactHeaderStart = businessContactHeaderStart;
		this.personalContactHeaderStart = personalContactHeaderStart;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String livelinked,
			boolean corporateContact, String organization, String title,
			boolean firstDegreeConnection, String contactId, String contactName) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.livelinkId = livelinked;
		this.corporateContact = corporateContact;
		this.organization = organization;
		this.title = title;
		this.firstDegreeConnection = firstDegreeConnection;
		this.contactId = contactId;
		this.contactName = contactName;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String country, String livelinkId,
			boolean corporateContact) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.country = country;
		this.corporateContact = corporateContact;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String country, String livelinkId,
			boolean corporateContact, boolean businessContact) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.country = country;
		this.corporateContact = corporateContact;
		this.businessContact = businessContact;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, boolean businessContact) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.corporateContact = businessContact;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address, String livelinkId,
			boolean businessContact, boolean businessContactHeaderStart) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
		this.livelinkId = livelinkId;
		this.corporateContact = businessContact;
		this.businessContactHeaderStart = businessContactHeaderStart;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website, String address) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
		this.address = address;
	}

	public Contact(String id, String name, String[] phones, String[] emails,
			String website) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.website = website;
	}

	public Contact(String id, String name, String[] phones, String[] emails) {
		super();
		this.id = id;
		this.name = name;
		this.phones = phones;
		this.emails = emails;
	}

	public boolean isFirstDegreeConnection() {
		return firstDegreeConnection;
	}

	public void setFirstDegreeConnection(boolean firstDegreeConnection) {
		this.firstDegreeConnection = firstDegreeConnection;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	private boolean isNotNullOrEmpty(String value) {
		return value != null && !value.trim().equals("");
	}

	private boolean isNotNullOrEmpty(String[] values) {
		if (values == null || values.length == 0)
			return false;
		for (String s : values) {
			if (isNotNullOrEmpty(s))
				return true;
		}
		return false;
	}

	public boolean isValidContact() {
		return isNotNullOrEmpty(name) || isNotNullOrEmpty(this.companyName)
				|| isNotNullOrEmpty(this.organization)
				|| isNotNullOrEmpty(this.title)
				|| isNotNullOrEmpty(this.website)
				|| isNotNullOrEmpty(this.emails)
				|| isNotNullOrEmpty(this.phones);
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

	public boolean isNoContactFound() {
		return noContactFound;
	}

	public void setNoContactFound(boolean noContactFound) {
		this.noContactFound = noContactFound;
	}

	public boolean isBusinessContact() {
		return businessContact;
	}

	public String getGroup() {
		return group;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setCorporateContact(boolean corporateContact) {
		this.corporateContact = corporateContact;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLivelinkId() {
		return livelinkId;
	}

	public void setLivelinkId(String livelinkId) {
		this.livelinkId = livelinkId;
	}

	public boolean isCorporateContact() {
		return corporateContact;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isPersonalContactGroupHeaderStart() {
		return personalContactGroupHeaderStart;
	}

	public void setPersonalContactGroupHeaderStart(
			boolean personalContactGroupHeaderStart) {
		this.personalContactGroupHeaderStart = personalContactGroupHeaderStart;
	}

	public void setBusinessContact(boolean businessContact) {
		this.businessContact = businessContact;
	}

	public String getAddress() {
		return address;
	}

	public boolean isDummyContac() {
		return this.id == null || "-1".equals(id);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(int index, String email) {
		if (emails != null) {
			if (emails.length > index) {
				emails[index] = email;
			} else {
				String myemails[] = new String[emails.length + 1];
				System.arraycopy(emails, 0, myemails, 0, emails.length);
				emails = myemails;
				emails[index] = email;
			}
		}
	}

	public void setPhone(int index, String phone) {
		if (phones != null) {
			if (phones.length > index) {
				phones[index] = phone;
			} else {
				String myphones[] = new String[phones.length + 1];
				System.arraycopy(phones, 0, myphones, 0, phones.length);
				phones = myphones;
				phones[index] = phone;
			}
		} else if (phone != null && !"".equals(phone.trim())) {
			phones = new String[1];
			phones[0] = phone;
		}
	}

	public String[] getPhones() {
		if (phones == null) {
			phones = new String[0];
		}
		return phones;
	}

	public void setPhones(String[] phones) {
		this.phones = phones;
	}

	public String[] getEmails() {
		if (emails == null) {
			emails = new String[0];
		}
		return emails;
	}

	public void setEmails(String[] emails) {
		this.emails = emails;
	}

	public boolean isBusinessEndContact() {
		return businessContactHeaderStart;
	}

	public void setBusinessEndContact(boolean businessMarker) {
		this.businessContactHeaderStart = businessMarker;
	}

	public boolean isBusinessContactHeaderStart() {
		return businessContactHeaderStart;
	}

	public void setBusinessContactHeaderStart(boolean businessContactHeaderStart) {
		this.businessContactHeaderStart = businessContactHeaderStart;
	}

	public boolean isPersonalContactHeaderStart() {
		return personalContactHeaderStart;
	}

	public void setPersonalContactHeaderStart(boolean personalContactHeaderStart) {
		this.personalContactHeaderStart = personalContactHeaderStart;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", name=" + name + ", phones="
				+ Arrays.toString(phones) + ", emails="
				+ Arrays.toString(emails) + ", website=" + website
				+ ", address=" + address + ", livelinkId=" + livelinkId
				+ ", country=" + country + ", group=" + group + ", message="
				+ message + ", companyName=" + companyName + ", organization="
				+ organization + ", title=" + title + ", corporateContact="
				+ corporateContact + ", businessContact=" + businessContact
				+ ", businessContactHeaderStart=" + businessContactHeaderStart
				+ ", personalContactHeaderStart=" + personalContactHeaderStart
				+ ", personalContactGroupHeaderStart="
				+ personalContactGroupHeaderStart + ", noContactFound="
				+ noContactFound + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Contact) {
			Contact c = (Contact) o;
			return (id != null) ? id.equals(c.id) : c.id == null;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 67;
		hash += (id != null) ? id.hashCode() : 0;
		return hash;
	}

	public String getDefaultEmail() {
		return getEmails() != null && getEmails().length > 0 ? getEmails()[0]
				: null;
	}

	private int compareName(Contact another) {
		if (another != null) {
			String cmp1 = name != null ? name : getDefaultEmail();
			String cmp2 = another.name != null ? another.name : another
					.getDefaultEmail();
			return (cmp1 != null && cmp2 != null) ? cmp1
					.compareToIgnoreCase(cmp2) : -1;
		}
		return 1;
	}

	public int compareTo(Contact another) {
		try {
			int cmp = 0;
			if (group == null && another.group == null) {
				cmp = compareName(another);
			} else if (group == null) {
				cmp = 1;
			} else if (another.group == null) {
				cmp = -1;
			} else {
				cmp = group.compareToIgnoreCase(another.group);
				if (cmp == 0) {
					cmp = compareName(another);
				}
			}
			return cmp;
		} catch (Exception e) {
			Log.e("Contact.CompareTo", e.toString());
			return -1;
		}
	}
}
