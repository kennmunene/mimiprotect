/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.mimiprotect.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.variance.mimiprotect.call.CallInformation;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.contacts.UserInfo;
import com.variance.mimiprotect.contacts.Users;
import com.variance.mimiprotect.contacts.Contact.ContactType;
import com.variance.mimiprotect.contacts.business.BusinessInformation;
import com.variance.mimiprotect.livelink.util.LiveLinkRequest;
import com.variance.mimiprotect.livelink.util.LivelinkRequests;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.xml.VDocument;

/**
 * 
 * @author kenn
 */
@SuppressWarnings("unused")
public class DataParser {

	private static final String HTTP_DATA = "data";
	private static final String HTTP_DATA_UNIT = "unit";

	private static final String HTTP_RESPONSE = "response";
	private static final String HTTP_RESPONSE_STATUS = "status";
	private static final String HTTP_RESPONSE_STATUS_VALUE = "enum-value";
	private static final String HTTP_RESPONSE_MESSAGE = "message";
	private static final String HTTP_RESPONSE_EXTRAS = "extras";
	private static final String HTTP_RESPONSE_EXTRAS_ENTRY = "map-entry";
	private static final String HTTP_RESPONSE_EXTRAS_ENTRY_ID = "id";
	private static final String HTTP_RESPONSE_EXTRAS_ENTRY_VALUE = "value";

	private static final String UPLOADED_CONTENT = "ucontent";
	private static final String UPLOADED_CONTENT_NAME = "name";
	private static final String UPLOADED_CONTENT_ID = "id";
	private static final String UPLOADED_CONTENT_PATH = "path";
	private static final String UPLOADED_CONTENT_EXT = "ext";

	// Corporate contact data for call by name
	public static final String CORPORATE_CONTACT_EMAIL = "email";
	public static final String CORPORATE_CONTACT_PHONE_NUMBERS = "phones";
	public static final String CORPORATE_CONTACT_ID = "id";
	public static final String CORPORATE_CONTACT_PHONE_NUMBER = "phone";
	public static final String CORPORATE_CONTACT = "entry";
	public static final String CORPORATE_CONTACT_WEBSITE = "web";
	public static final String CORPORATE_CONTACT_LOCATION = "location";
	public static final String CORPORATE_CONTACT_NAME = "name";
	public static final String CORPORATE_CONTACT_MESSAGE = "message";
	public static final String CORPORATE_CONTACT_COMPANY_NAME = "companyName";
	public static final String CORPORATE_CONTACT_ADDRESS = "b-addr";
	public static final String CORPORATE_CONTACT_COUNTRY = "country";

	// technically business contact information for the organization associated
	// with current user.
	private static final String BUSINESS_CONTACT_INFORMATION = "business";
	private static final String BUSINESS_CONTACT_INFORMATION_NAME = "name";
	private static final String BUSINESS_CONTACT_INFORMATION_COUNTRY = "country";
	private static final String BUSINESS_CONTACT_INFORMATION_ADDRESS = "address";
	private static final String BUSINESS_CONTACT_INFORMATION_POSTCODE = "postcode";
	private static final String BUSINESS_CONTACT_INFORMATION_WEBSITE = "website";
	private static final String BUSINESS_CONTACT_INFORMATION_PHONES = "phones";
	private static final String BUSINESS_CONTACT_INFORMATION_EMAILS = "emails";
	private static final String BUSINESS_CONTACT_INFORMATION_PHONE = "email";
	private static final String BUSINESS_CONTACT_INFORMATION_EMAIL = "email";
	// business contacts listed for current organization
	private static final String BUSINESS_ID = "businessId";
	private static final String BUSINESS_CONTACTS = "businessContacts";
	private static final String BUSINESS_CONTACT = "contact";
	private static final String BUSINESS_CONTACT_NAME = "name";
	private static final String BUSINESS_CONTACT_EMAIL = "email";
	private static final String BUSINESS_CONTACT_PHONE = "phone";
	private static final String BUSINESS_CONTACT_OTHERNAMES = "otherNames";
	private static final String BUSINESS_CONTACT_USERS = "elements";
	private static final String BUSINESS_CONTACT_USER = "unit";
	private static final String BUSINESS_CONTACT_USER_NAME = "userName";
	private static final String BUSINESS_CONTACT_USER_ID = "id";
	private static final String BUSINESS_CONTACT_ORGANIZATION = "organization";
	private static final String BUSINESS_CONTACT_TITLE = "title";

	// Basic xml markers for
	public static final String PERSONAL_CONTACT = "contact";
	public static final String PERSONAL_CONTACT_NAME = "name";
	public static final String PERSONAL_CONTACT_PHONE = "phone";
	public static final String PERSONAL_CONTACT_PHONES = "phones";
	public static final String PERSONAL_CONTACT_EMAIL = "email";
	public static final String PERSONAL_CONTACT_EMAILS = "emails";
	public static final String PERSONAL_CONTACT_ID = "id";
	public static final String PERSONAL_CONTACT_ORGANIZATION = "organization";
	public static final String PERSONAL_CONTACT_TITLE = "title";
	public static final String PERSONAL_CONTACT_ADDRESS = "contact-addr";
	public static final String PERSONAL_CONTACT_LIVELINKED = "livelinked";
	public static final String PERSONAL_CONTACT_CONTACTID = "contactId";
	public static final String PERSONAL_CONTACT_CONTACTNAME = "contactName";
	public static final String PERSONAL_CONTACT_GROUP = "group";

	private static final String USER = "UserCN";
	private static final String USER_ID = "id";
	private static final String USER_NAME = "userName";
	private static final String USER_MSISDN = "msisdn";
	private static final String USER_EMAIL = "email";
	private static final String USER_PASSWORD = "password";
	private static final String USER_CONTACT_INFORMATION = "userContactInformation";
	private static final String USER_CONTACT_SURNAME = "surname";
	private static final String USER_CONTACT_OTHERNAMES = "othernames";
	private static final String USER_CONTACT_PHONE_NUMBERS = "phoneNumbers";
	private static final String USER_CONTACT_PHONE = "phone";
	private static final String USER_CONTACT_EMAIL_ADDRESSES = "emailAddresses";
	private static final String USER_CONTACT_EMAIL = "email";
	private static final String USER_CONTACT_ORGANIZATION = "organization";
	private static final String USER_CONTACT_TITLE = "title";

	private static final String USER_INFO = "UserInfo";
	private static final String USER_INFO_USER_NAME = "userName";
	private static final String USER_INFO_ACTUAL_NAME = "userActualName";

	private static final String LIVELINK_REQUEST_ID = "id";
	private static final String LIVELINK_REQUEST = "LivelinkRequest";
	private static final String LIVELINK_REQUEST_USER = "fromUser";
	private static final String LIVELINK_REQUEST_USER_ID = "id";
	private static final String LIVELINK_REQUEST_USER_NAME = "name";
	private static final String LIVELINK_REQUEST_MESSAGE = "livelinkMessage";

	private static final String UNLINK_REQUEST = "unlink";
	private static final String UNLINK_REQUEST_CONTACT_ID = "id";

	private static final String CALL_INFORMATION = "call";
	private static final String CALL_INFORMATION_MSISDN = "msisdn";
	private static final String CALL_INFORMATION_IMEI = "imei";
	private static final String CALL_INFORMATION_DIALED_NUMBER = "dialled-number";
	private static final String CALL_INFORMATION_DIALED_DATE = "dialled-date";
	private static final String CALL_INFORMATION_DISCONNECTED_DATE = "disconnected-date";
	private static final String CALL_INFORMATION_CELL_LOCATION_CELL_ID = "cell-id";
	private static final String CALL_INFORMATION_CELL_LOCATION_AREA_CODE = "area-code";
	private static final String CALL_INFORMATION_CELL_LOCATION_UMST_PSC = "psc";
	private static final String CALL_INFORMATION_LATITUDE = "lat";
	private static final String CALL_INFORMATION_LONGITUDE = "lon";

	private static String getStart(String elem) {
		return "<" + elem + ">";
	}

	private static String getEnd(String elem) {
		return "</" + elem + ">";
	}

	private static String get(String str, String elem) {
		String s = getStart(elem), e = getEnd(elem);
		int off = str.indexOf(s), end = str.indexOf(e);
		if (off >= 0 && end > off) {
			return str.substring(off + s.length(), end);
		}
		return null;
	}

	private static String put(String data, String markup) {
		StringBuilder sb = new StringBuilder();
		sb.append(getStart(markup));
		sb.append(data);
		sb.append(getEnd(markup));
		return sb.toString();
	}

	private static String put(Object data, String markup) {
		StringBuilder sb = new StringBuilder();
		sb.append(getStart(markup));
		sb.append(data);
		sb.append(getEnd(markup));
		return sb.toString();
	}

	/**
	 * Splits the string contact into separate contacts
	 * 
	 * @param str
	 * @param elem
	 * @return
	 */
	private static Vector<String> getContact(String str, String elem) {
		String s = getStart(elem), e = getEnd(elem);
		int off = str.indexOf(s), end = str.indexOf(e, off);
		Vector<String> v = new Vector<String>();
		while (off >= 0 && end > off) {
			String val = str.substring(off, end + e.length()); // we need the
																// entire
																// contact
																// element
			v.add(val);
			off = str.indexOf(s, end);
			end = str.indexOf(e, off);
		}
		return v;
	}

	private static Contact getBusinessDetail(String details) {
		// remove entry marker
		String ds = get(details, CORPORATE_CONTACT);
		// start the data off
		String name = get(ds, CORPORATE_CONTACT_NAME);
		String email = get(ds, CORPORATE_CONTACT_EMAIL);
		String country = get(ds, CORPORATE_CONTACT_COUNTRY);
		String id = get(ds, CORPORATE_CONTACT_ID);
		String address = get(ds, CORPORATE_CONTACT_LOCATION);
		String website = get(ds, CORPORATE_CONTACT_WEBSITE);
		String phones = get(ds, CORPORATE_CONTACT_PHONE_NUMBERS);
		String ticker = get(ds, CORPORATE_CONTACT_MESSAGE);
		String companyName = get(ds, CORPORATE_CONTACT_COMPANY_NAME);
		Log.i("Business Ticker", ticker == null ? "ticker null" : ticker);
		String[] phoneList = null;
		Vector<String> v = new Vector<String>();
		if (phones != null) {
			int isPhone = phones
					.indexOf(getEnd(CORPORATE_CONTACT_PHONE_NUMBER));
			while (isPhone > 0) {
				v.add(get(phones, CORPORATE_CONTACT_PHONE_NUMBER));
				phones = phones.substring(isPhone
						+ getEnd(CORPORATE_CONTACT_PHONE_NUMBER).length());
				if (phones != null) {
					isPhone = phones
							.indexOf(getEnd(CORPORATE_CONTACT_PHONE_NUMBER));
				} else {
					break;
				}
			}
		}
		if (!v.isEmpty()) {
			int i = 0;
			phoneList = new String[v.size()];
			for (String s : v) {
				phoneList[i++] = s;
			}
		}
		Contact c = new Contact(id, name, phoneList, new String[] { email },
				website, address, country, null, true);
		if (ticker != null && !"null".equals(ticker.trim())
				&& !"".equals(ticker.trim())) {
			c.setMessage(ticker);
		}
		if (companyName != null && !"null".equals(companyName)) {
			c.setCompanyName(companyName);
		}
		return c;
	}

	public static Contact getBusinessContactDetail(String details) {
		// remove business marker
		String entry = get(details, CORPORATE_CONTACT_ADDRESS);
		return getBusinessDetail(entry);
	}

	private static String[] getArrayDetails(String details, String delim) {
		String[] list = null;
		Vector<String> v = new Vector<String>();
		if (details != null) {
			int isDetail = details.indexOf(getEnd(delim));
			while (isDetail > 0) {
				v.add(get(details, delim));
				details = details.substring(isDetail + getEnd(delim).length());
				if (details != null) {
					isDetail = details.indexOf(getEnd(delim));
				} else {
					break;
				}
			}
		}
		list = new String[v.size()];
		int i = 0;
		for (String s : v) {
			list[i++] = s;
		}
		return list;
	}

	private static List<String> getListDetails(String details, String delim) {
		List<String> list = new ArrayList<String>();

		if (details != null) {
			int isDetail = details.indexOf(getEnd(delim));
			while (isDetail > 0) {
				list.add(get(details, delim));
				details = details.substring(isDetail + getEnd(delim).length());
				if (details != null) {
					isDetail = details.indexOf(getEnd(delim));
				} else {
					break;
				}
			}
		}
		return list;
	}

	private static Contact getPersonalDetails(String details) {
		// remove entry marker
		String ds = get(details, PERSONAL_CONTACT);
		Log.i("Contact Details:", ds + "");
		// start the data off
		String name = get(ds, PERSONAL_CONTACT_NAME);
		String emails = get(ds, PERSONAL_CONTACT_EMAILS);
		String livelink = get(ds, PERSONAL_CONTACT_LIVELINKED);
		String[] emailList = getArrayDetails(emails, PERSONAL_CONTACT_EMAIL);
		String id = get(ds, PERSONAL_CONTACT_ID);
		String address = get(ds, PERSONAL_CONTACT_ADDRESS);
		String phones = get(ds, PERSONAL_CONTACT_PHONES);
		String group = get(ds, PERSONAL_CONTACT_GROUP);
		String title = get(ds, PERSONAL_CONTACT_TITLE);
		String contactId = get(ds, PERSONAL_CONTACT_CONTACTID);
		String contactName = get(ds, PERSONAL_CONTACT_CONTACTNAME);
		String organization = get(ds, PERSONAL_CONTACT_ORGANIZATION);
		String[] phoneList = getArrayDetails(phones, PERSONAL_CONTACT_PHONE);
		Contact c = new Contact(id, name, phoneList, emailList, null, address,
				livelink, false, organization, title,
				!Utils.isNullStringOrEmpty(contactName), contactId, contactName);
		if (group != null && !"".equals(group.trim())
				&& !"null".equals(group.trim())) {
			c.setGroup(group);
		}
		return c;
	}

	public static Contact getPersonalContactDetail(String details) {
		// remove personal marker
		String entry = get(details, PERSONAL_CONTACT_ADDRESS);
		return getPersonalDetails(entry);
	}

	private static ArrayList<Contact> getContacts(String details,
			String addressDelim, String contactDelim, boolean businessDetails) {
		ArrayList<Contact> cs = new ArrayList<Contact>();
		try {
			if (details.contains(contactDelim)
					&& details.contains(addressDelim)) {
				String entry = get(details, addressDelim);
				Vector<String> contactDetails = getContact(entry, contactDelim);
				for (String s : contactDetails) {
					Contact c = businessDetails ? getBusinessDetail(s)
							: getPersonalDetails(s);
					Log.i("Contact", c.getContactId() + "");
					if (c.getName() == null || c.getName().equals("")) {
						if (c.getEmails() != null && c.getEmails().length > 0) {
							c.setName(c.getEmails()[0]);
						} else if (c.getPhones() != null
								&& c.getPhones().length > 0) {
							c.setName(c.getPhones()[0]);
						}
					}
					if (c.isValidContact()) {
						c.setCorporateContact(businessDetails);
						cs.add(c);
					}
				}
			}
			if (cs.isEmpty()) {
				// we add a dummy contact for display
				Contact c = new Contact("-1", "No contacts found", null, null);
				cs.add(c);
				c.setNoContactFound(true);
				if (businessDetails) {
					c.setName("No business listings");
				}
			}
			if (!businessDetails) {
				Collections.sort(cs);
			}
		} catch (Exception e) {
			Log.e("DataParser: ", e.toString());
		}
		return cs;
	}

	public static ArrayList<Contact> getCorporateContacts(String details) {
		return getContacts(details, CORPORATE_CONTACT_ADDRESS,
				CORPORATE_CONTACT, true);
	}

	public static ArrayList<Contact> getPersonalContacts(String details) {
		return getContacts(details, PERSONAL_CONTACT_ADDRESS, PERSONAL_CONTACT,
				false);
	}

	public static ArrayList<Contact> getContacts(String details,
			ContactType contactType) {
		switch (contactType) {
		case PERSONAL:
			return getPersonalContacts(details);
		case BUSINESS:
			return getBusinessContacts(details);
		case CORPORATE:
			return getCorporateContacts(details);
		default:
			return new ArrayList<Contact>();
		}
	}

	public static ArrayList<Contact> getBusinessContacts(String details) {
		ArrayList<Contact> contacts = getContacts(details, BUSINESS_CONTACTS,
				BUSINESS_CONTACT, false);
		return contacts;
	}

	public static ArrayList<Contact> getBusinessContactsUsers(String details) {
		String users = get(details, BUSINESS_CONTACT_USERS);
		if (users != null) {
			String[] userList = getArrayDetails(users, BUSINESS_CONTACT_USER);
			if (userList != null) {
				ArrayList<Contact> cs = new ArrayList<Contact>();
				for (String user : userList) {
					Contact c = new Contact();
					c.setName(user);
					cs.add(c);
				}
				return cs;
			}
		}
		return new ArrayList<Contact>();
	}

	public static ArrayList<UserInfo> getUserInformation(String details) {
		String users = get(details, HTTP_DATA);
		if (users != null) {
			String[] userList = getArrayDetails(users, HTTP_DATA_UNIT);
			if (userList != null) {
				ArrayList<UserInfo> cs = new ArrayList<UserInfo>();
				for (String userInfo : userList) {
					String userName = get(userInfo, USER_INFO_USER_NAME);
					String actualName = get(userInfo, USER_INFO_ACTUAL_NAME);
					cs.add(new UserInfo(userName, actualName));
				}
				return cs;
			}
		}
		return new ArrayList<UserInfo>();
	}

	public static String getPersonalContactString(Contact c) {
		VObjectMarshaller<Contact> m = new VObjectMarshaller<Contact>(
				Contact.class);
		return m.doMarshall(c);
		// StringBuilder sb = new StringBuilder();
		// sb.append(put(c.getId(), PERSONAL_CONTACT_ID));
		// // sb.append(put(c.getAddress(), PERSONAL_CONTACT_ADDRESS)); NOT YET
		// // DEFINED FOR PERSONAL CONTACTS
		// sb.append(put(c.getLivelinkId(), PERSONAL_CONTACT_LIVELINKED));
		// sb.append(put(c.getName(), PERSONAL_CONTACT_NAME));
		// StringBuilder sbPhones = new StringBuilder();
		// for (String p : c.getPhones()) {
		// sbPhones.append(put(p, PERSONAL_CONTACT_PHONE));
		// }
		// sb.append(put(sbPhones, PERSONAL_CONTACT_PHONES));
		// StringBuilder sbEmails = new StringBuilder();
		// for (String e : c.getEmails()) {
		// sbEmails.append(put(e, PERSONAL_CONTACT_EMAIL));
		// }
		// sb.append(put(sbEmails, PERSONAL_CONTACT_EMAILS));
		// sb.append(put(c.getGroup(), PERSONAL_CONTACT_GROUP));
		// sb.append(put(c.getOrganization(), PERSONAL_CONTACT_ORGANIZATION));
		// sb.append(put(c.getTitle(), PERSONAL_CONTACT_TITLE));
		// return put(put(sb, PERSONAL_CONTACT), PERSONAL_CONTACT_ADDRESS);
	}

	public static User getUserFrom(String data) {
		User u = null;
		try {
			Log.i("getUserFrom", data + "");
			VDocument doc = VDocument.parseDocumentFromString(data);
			u = new VObjectMarshaller<User>(User.class).unmarshall(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	public static String getDataFrom(User user) {
		StringBuilder sb = new StringBuilder();
		sb.append(put(user.getId(), USER_ID));
		sb.append(put(user.getUserName(), USER_NAME));
		sb.append(put(user.getPassword(), USER_PASSWORD));
		sb.append(put(user.getMsisdn(), USER_MSISDN));
		sb.append(put(user.getEmail(), USER_EMAIL));
		StringBuilder sbContact = new StringBuilder();
		StringBuilder sbPhones = new StringBuilder();
		for (String p : user.getPhones()) {
			sbPhones.append(put(p, USER_CONTACT_PHONE));
		}
		sbContact.append(put(sbPhones, USER_CONTACT_PHONE_NUMBERS));
		StringBuilder sbEmails = new StringBuilder();
		for (String e : user.getEmails()) {
			sbEmails.append(put(e, USER_CONTACT_EMAIL));
		}
		sbContact.append(put(sbEmails, USER_CONTACT_EMAIL_ADDRESSES));
		sbContact
				.append(put(user.getOrganization(), USER_CONTACT_ORGANIZATION));
		sbContact.append(put(user.getTitle(), USER_CONTACT_TITLE));
		sbContact.append(put(user.getSurname(), USER_CONTACT_SURNAME));
		sbContact.append(put(user.getOtherNames(), USER_CONTACT_OTHERNAMES));
		sb.append(put(sbContact, USER_CONTACT_INFORMATION));
		return put(sb.toString(), USER);
	}

	public static List<LiveLinkRequest> getLivelinkRequestFrom(
			String livelinkStr) {
		Log.e("LivelinkRequests", livelinkStr);
		List<LiveLinkRequest> lrs = new ArrayList<LiveLinkRequest>();
		try {
			LivelinkRequests lr = new VObjectMarshaller<LivelinkRequests>(
					LivelinkRequests.class).unmarshall(VDocument
					.parseDocumentFromString(livelinkStr));
			return lr.getLivelinkRequests();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lrs;
	}

	public static String getUnlinkDataFrom(Contact contact) {
		String data = put(put(contact.getId(), UNLINK_REQUEST_CONTACT_ID),
				UNLINK_REQUEST);
		return data;
	}

	public static String getCallInformation(CallInformation ci) {
		StringBuilder sb = new StringBuilder();
		sb.append(put(ci.getCaller_msisdn(), CALL_INFORMATION_MSISDN));
		sb.append(put(ci.getPhoneDialed(), CALL_INFORMATION_DIALED_NUMBER));
		sb.append(put(Utils.toIsoString(ci.getDialedDate()),
				CALL_INFORMATION_DIALED_DATE));
		sb.append(put(Utils.toIsoString(ci.getEndDate()),
				CALL_INFORMATION_DISCONNECTED_DATE));
		if (ci.getCellLocation() instanceof GsmCellLocation) {
			GsmCellLocation cl = (GsmCellLocation) ci.getCellLocation();
			sb.append(put(cl.getCid(), CALL_INFORMATION_CELL_LOCATION_CELL_ID));
			sb.append(put(cl.getLac(), CALL_INFORMATION_CELL_LOCATION_AREA_CODE));
			sb.append(put(-1, CALL_INFORMATION_CELL_LOCATION_UMST_PSC));
		} else {
			sb.append(put(-1, CALL_INFORMATION_CELL_LOCATION_CELL_ID));
			sb.append(put(-1, CALL_INFORMATION_CELL_LOCATION_AREA_CODE));
			sb.append(put(-1, CALL_INFORMATION_CELL_LOCATION_UMST_PSC));
		}
		sb.append(put(ci.getImei(), CALL_INFORMATION_IMEI));
		sb.append(put(ci.getLatitude(), CALL_INFORMATION_LATITUDE));
		sb.append(put(ci.getLongitude(), CALL_INFORMATION_LONGITUDE));
		String callInfo = put(sb, CALL_INFORMATION);
		Log.i("CallInformation: ", callInfo);
		return callInfo;
	}

	public static List<UploadedContent> getUploadedContentsFrom(
			String contentsXml) {
		List<UploadedContent> list = new ArrayList<UploadedContent>();
		String[] contents = getArrayDetails(contentsXml, UPLOADED_CONTENT);
		for (String cs : contents) {
			UploadedContent uc = new UploadedContent();
			String fileId = get(cs, UPLOADED_CONTENT_ID);
			if (fileId != null && !"".equals(fileId)) {
				int id = Integer.parseInt(fileId);
				uc.setFileID(id);
			}
			uc.setFileExt(get(cs, UPLOADED_CONTENT_EXT));
			uc.setFileName(get(cs, UPLOADED_CONTENT_NAME));
			uc.setFilePath(get(cs, UPLOADED_CONTENT_PATH));
			list.add(uc);
		}
		return list;
	}

	public static String marshallBusinessInformation(BusinessInformation bInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append(put(bInfo.getBusinessName(),
				BUSINESS_CONTACT_INFORMATION_NAME));
		sb.append(put(bInfo.getAddress(), BUSINESS_CONTACT_INFORMATION_ADDRESS));
		sb.append(put(bInfo.getCountry(), BUSINESS_CONTACT_INFORMATION_COUNTRY));
		sb.append(put(bInfo.getPostcode(),
				BUSINESS_CONTACT_INFORMATION_POSTCODE));
		sb.append(put(bInfo.getWebsite(), BUSINESS_CONTACT_INFORMATION_WEBSITE));
		StringBuilder phones = new StringBuilder();
		for (String p : bInfo.getPhones()) {
			phones.append(put(p, BUSINESS_CONTACT_INFORMATION_PHONE));
		}
		sb.append(put(phones, BUSINESS_CONTACT_INFORMATION_PHONES));
		StringBuilder emails = new StringBuilder();
		for (String e : bInfo.getEmails()) {
			phones.append(put(e, BUSINESS_CONTACT_INFORMATION_EMAIL));
		}
		sb.append(put(emails, BUSINESS_CONTACT_INFORMATION_EMAILS));
		String val = put(sb, BUSINESS_CONTACT_INFORMATION);
		Log.i("Marshaller", val);
		return val;
	}

	public static HttpResponseData getHttpResponseData(String response) {
		String responseStr = get(response, HTTP_RESPONSE);
		if (responseStr == null) {
			return null;
		}
		String status = get(responseStr, HTTP_RESPONSE_STATUS);
		String statusValue = get(status, HTTP_RESPONSE_STATUS_VALUE);
		String message = get(responseStr, HTTP_RESPONSE_MESSAGE);
		// do we have any extras
		String extras = get(responseStr, HTTP_RESPONSE_EXTRAS);
		HttpResponseStatus responseStatus = HttpResponseStatus
				.valueOf(statusValue);
		HttpResponseData data = new HttpResponseData(responseStatus, message);
		if (extras != null) {
			String[] extrasEntry = getArrayDetails(responseStr,
					HTTP_RESPONSE_EXTRAS_ENTRY);
			if (extrasEntry != null && extrasEntry.length > 0) {
				for (String extra : extrasEntry) {
					String id = get(extra, HTTP_RESPONSE_EXTRAS_ENTRY_ID);
					String value = get(extra, HTTP_RESPONSE_EXTRAS_ENTRY_VALUE);
					if (id != null && value != null) {
						data.putExtra(id, value);
					}
				}
			}
		}
		return data;
	}

	/**
	 * Marshals the list with only the user name information
	 * 
	 * @param userInfos
	 * @return
	 */
	public static String getUserNameInformation(List<UserInfo> userInfos) {
		StringBuilder sb = new StringBuilder();
		for (UserInfo ui : userInfos) {
			sb.append(put(ui.getUserName(), USER_NAME));
		}
		String info = put(sb, USER);
		Log.i("User Name Information", info);
		return info;
	}

	/**
	 * Marshalls the list with only the id information
	 * 
	 * @param cs
	 * @return
	 */
	public static String getPersonalContactIdInformation(List<Contact> cs) {
		StringBuilder sb = new StringBuilder();
		for (Contact c : cs) {
			sb.append(put(c.getId(), PERSONAL_CONTACT_ID));
		}
		return put(sb, PERSONAL_CONTACT);
	}

	public static String getNewBusinessContactData(String name,
			String otherNames, String phone, String email) {
		StringBuilder sb = new StringBuilder();
		sb.append(put(name, BUSINESS_CONTACT_NAME));
		sb.append(put(otherNames, BUSINESS_CONTACT_OTHERNAMES));
		sb.append(put(phone, BUSINESS_CONTACT_PHONE));
		sb.append(put(email, BUSINESS_CONTACT_EMAIL));
		return put(sb, BUSINESS_CONTACT);
	}

	public static Users getUsersFrom(String data) {
		try {
			Log.i("getUsersFrom", data + "");
			VDocument doc = VDocument.parseDocumentFromString(data);
			return new VObjectMarshaller<Users>(Users.class).unmarshall(doc);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getNewBusinessContactData(String name,
			String otherNames, String phone, String email, String company,
			String title) {
		StringBuilder sb = new StringBuilder();
		sb.append(put(name, BUSINESS_CONTACT_NAME));
		sb.append(put(otherNames, BUSINESS_CONTACT_OTHERNAMES));
		sb.append(put(phone, BUSINESS_CONTACT_PHONE));
		sb.append(put(email, BUSINESS_CONTACT_EMAIL));
		sb.append(put(company, BUSINESS_CONTACT_ORGANIZATION));
		sb.append(put(title, BUSINESS_CONTACT_TITLE));
		return put(sb, BUSINESS_CONTACT);
	}

	public static UserSetting parseUserSettings(String loadUserSettingStr) {
		UserSetting userSetting = new UserSetting();
		userSetting.setAllowLocalCache(Boolean.valueOf(get(loadUserSettingStr,
				UserSetting.USERSETTING_allowLocalCache)));
		String value = get(loadUserSettingStr,
				UserSetting.USERSETTING_cacheUpdatePeriod);
		if (value != null) {
			userSetting.setCacheUpdatePeriod(Integer.parseInt(value));
		}
		value = get(loadUserSettingStr,
				UserSetting.USERSETTING_enableAutomaticLogin);
		if (value != null) {
			userSetting.setEnableAutomaticLogin(Boolean.valueOf(value));
		}
		value = get(loadUserSettingStr, UserSetting.USERSETTING_imei);
		if (value != null) {
			userSetting.setImei(value);
		}
		value = get(loadUserSettingStr, UserSetting.USERSETTING_lastCacheUpdate);
		if (value != null) {
			userSetting.setLastCacheUpdate(Utils.parseISODate(value));
		}
		value = get(loadUserSettingStr,
				UserSetting.USERSETTING_lockMimiProtectToCurrentPhone);
		if (value != null) {
			userSetting
					.setLockMimiProtectToCurrentPhone(Boolean.valueOf(value));
		}
		value = get(loadUserSettingStr, UserSetting.USERSETTING_password);
		if (value != null) {
			userSetting.setPassword(value);
		}
		value = get(loadUserSettingStr, UserSetting.USERSETTING_showDashboard);
		if (value != null) {
			userSetting.setShowDashboard(Boolean.valueOf(value));
		}
		value = get(loadUserSettingStr,
				UserSetting.USERSETTING_showSplashScreen);
		if (value != null) {
			userSetting.setShowSplashScreen(Boolean.valueOf(value));
		}
		value = get(loadUserSettingStr, UserSetting.USERSETTING_username);
		if (value != null) {
			userSetting.setUsername(value);
		}
		value = get(loadUserSettingStr,
				UserSetting.USERSETTING_businessPhonebookUser);
		if (value != null) {
			userSetting.setBusinessPhonebookUser(Boolean.valueOf(value));
		}
		value = get(loadUserSettingStr,
				UserSetting.USERSETTING_businessPhonebookName);
		if (value != null) {
			userSetting.setBusinessPhonebookName(value);
		}
		return userSetting;
	}
}
