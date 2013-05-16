package com.variance.mimiprotect.contacts;

import info.ineighborhood.cardme.vcard.VCard;
import info.ineighborhood.cardme.vcard.features.AddressFeature;
import info.ineighborhood.cardme.vcard.features.DisplayableNameFeature;
import info.ineighborhood.cardme.vcard.features.EmailFeature;
import info.ineighborhood.cardme.vcard.features.FormattedNameFeature;
import info.ineighborhood.cardme.vcard.features.GeographicPositionFeature;
import info.ineighborhood.cardme.vcard.features.NameFeature;
import info.ineighborhood.cardme.vcard.features.NicknameFeature;
import info.ineighborhood.cardme.vcard.features.OrganizationFeature;
import info.ineighborhood.cardme.vcard.features.TelephoneFeature;
import info.ineighborhood.cardme.vcard.features.TitleFeature;
import info.ineighborhood.cardme.vcard.features.URLFeature;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;

public class VCardAndroidParser {

	public ArrayList<ContentProviderOperation> getContentProviderOperations(
			VCard vcard) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		String displayName = null;
		String firstName = null;
		String surName = null;
		String nickName = null;
		String middleName = null;
		String MobileNumber = null;
		String HomeNumber = null;
		String WorkNumber = null;
		String emailID = null;
		String company = null;
		String jobTitle = null;
		String website = null;
		// String fax = null;
		SimpleAddress address = null;
		// String error = "";
		try {
			DisplayableNameFeature dnf = vcard.getDisplayableNameFeature();
			if (dnf != null) {
				displayName = dnf.getName();
			}
		} catch (Exception e) {
			// error += "disp name, ";
		}

		try {
			NameFeature nf = vcard.getName();
			if (nf != null && nf.hasFamilyName()) {
				surName = nf.getFamilyName();
			}
			if (nf != null && nf.hasGivenName()) {
				firstName = nf.getGivenName();
			}
			if (nf != null && nf.hasAdditionalNames()) {
				Iterator<String> names = nf.getAdditionalNames();
				middleName = "";
				while (names.hasNext()) {
					middleName = middleName.concat(names.next()).concat(" ");
				}
			}

			FormattedNameFeature fnf = vcard.getFormattedName();
			if (fnf != null) {
				if (displayName == null || displayName.equalsIgnoreCase(""))
					displayName = fnf.getFormattedName();
				else if (surName == null || surName.equalsIgnoreCase("")) {
					surName = fnf.getFormattedName();
				} else if (middleName == null
						|| middleName.equalsIgnoreCase("")) {
					middleName = fnf.getFormattedName();
				}
			}
			if (surName == null && displayName != null) {
				surName = displayName;
			}

			NicknameFeature nnf = vcard.getNicknames();
			if (nnf != null && nnf.hasNicknames()) {
				nickName = "";
				Iterator<String> nicknames = nnf.getNicknames();
				while (nicknames.hasNext()) {
					nickName = nickName.concat(nicknames.next()).concat(" ");
				}
			}

		} catch (Exception e) {
			// error += "f,l name, ";
		}

		// ---------------------------- Address
		try {
			Iterator<AddressFeature> afs = vcard.getAddresses();
			AddressFeature af = null;
			if (afs != null) {
				address = new SimpleAddress();
			}
			while (afs.hasNext()) {
				af = afs.next();
				address.setPostCode(af.getPostalCode());
				address.setCountryName(af.getCountryName());
				address.setLocality(af.getLocality());
				address.setPostBox(af.getPostOfficeBox());
				address.setRegion(af.getRegion());
				address.setStreetAddress(af.getStreetAddress());
			}
		} catch (Exception e) {
		}

		try {
			OrganizationFeature org = vcard.getOrganizations();
			if (org != null && org.hasOrganizations()) {
				company = "";
				Iterator<String> orgs = org.getOrganizations();
				while (orgs.hasNext()) {
					company = company.concat(orgs.next()).concat(" ");
				}
			}
		} catch (Exception e) {
			// error += "compa, ";
		}

		try {
			TitleFeature tf = vcard.getTitle();
			if (tf != null && tf.hasTitle()) {
				jobTitle = tf.getTitle();
			}
		} catch (Exception e) {
			// error += "title, ";
		}

		try {
			Iterator<TelephoneFeature> tfs = vcard.getTelephoneNumbers();
			TelephoneFeature tf;
			while (tfs.hasNext()) {
				tf = tfs.next();
				String phone = tf.getTelephone();
				if (MobileNumber == null) {
					MobileNumber = phone;
				} else if (HomeNumber == null) {
					HomeNumber = phone;
				} else if (WorkNumber == null) {
					WorkNumber = phone;
				}
			}
			// error += " tel = " + count;

		} catch (Exception e) {
			// error += "phones, ";
		}

		try {
			Iterator<EmailFeature> efs = vcard.getEmails();
			EmailFeature ef = null;
			while (efs.hasNext()) {
				ef = efs.next();
				String email = ef.getEmail();
				if (email != null && !email.equalsIgnoreCase("")) {
					emailID = email;
				}
			}
			// error += " email=" ;
		} catch (Exception e) {
			// error += "email ";
		}

		try {
			GeographicPositionFeature gpf = vcard.getGeographicPosition();
			if (vcard.hasGeographicPosition()) {
				address.getLocation().setLat(gpf.getLatitude());
				address.getLocation().setLongi(gpf.getLongitude());

			}
		} catch (Exception e) {
		}

		try {
			Iterator<URLFeature> urls = vcard.getURLs();
			URLFeature url = null;
			if (vcard.hasURLs()) {
				while (urls.hasNext()) {
					url = urls.next();
					website = url.getURL().toString();
				}
			}

		} catch (Exception e) {
		}

		if (displayName == null || displayName.equalsIgnoreCase("")) {
			displayName = "";
			if (surName != null)
				displayName = displayName.concat(surName).concat(" ");
		}
		if (firstName != null) {
			displayName = displayName.concat(firstName);
		}

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------ Names
		if (firstName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
							firstName).build());
		}

		if (surName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
							surName).build());
		}

		if (nickName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Nickname.NAME,
							nickName).build());
		}

		// ------------------------------------------------------ Mobile Number
		if (MobileNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							MobileNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build());
		}

		// ------------------------------------------------------ Home Numbers
		if (HomeNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							HomeNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
					.build());
		}

		// ------------------------------------------------------ Work Numbers
		if (WorkNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							WorkNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
					.build());
		}

		// ------------------------------------------------------ Email
		if (emailID != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							emailID)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.build());
		}

		if (website != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Website.DATA,
							website)
					.withValue(ContactsContract.CommonDataKinds.Website.TYPE,
							ContactsContract.CommonDataKinds.Website.TYPE_WORK)
					.build());
		}
		/*
		 * if (fax != null) { ops.add(ContentProviderOperation
		 * .newInsert(ContactsContract.Data.CONTENT_URI)
		 * .withValueBackReference( ContactsContract.Data.RAW_CONTACT_ID, 0)
		 * .withValue( ContactsContract.Data.MIMETYPE,
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		 * .withValue(ContactsContract.CommonDataKinds.Phone.DATA, fax)
		 * .withValue( ContactsContract.CommonDataKinds.Phone.TYPE,
		 * ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK) .build()); }
		 */
		// ------------------------------------------------------ Organization
		if (company != null && !company.equals("") && !jobTitle.equals("")) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.COMPANY,
							company)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.TYPE,
							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.TITLE,
							jobTitle)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.TYPE,
							ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
					.build());
		}

		// ---------------------------------------------------Address

		if (address != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.CITY,
							address.getCity())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
							address.getCountryName())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.REGION,
							address.getRegion())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.STREET,
							address.getStreetAddress())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
							address.getPostBox())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
							address.getPostCode())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
					.build());
		}

		return ops;
	}

	public class SimpleAddress {
		private String streetAddress = "";
		private String postBox = "";
		private String locality = "";
		private String countryName = "";
		private String region = "";
		private String city = "";
		private String postCode = "";

		private GeoPosition location = new GeoPosition();

		public SimpleAddress() {

		}

		public String getPostCode() {
			return postCode;
		}

		public void setPostCode(String postCode) {
			this.postCode = postCode;
		}

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}

		public String getCountryName() {
			return countryName;
		}

		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getStreetAddress() {
			return streetAddress;
		}

		public void setStreetAddress(String streetAddress) {
			this.streetAddress = streetAddress;
		}

		public String getPostBox() {
			return postBox;
		}

		public void setPostBox(String postBox) {
			this.postBox = postBox;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public GeoPosition getLocation() {
			return location;
		}

		public void setLocation(GeoPosition location) {
			this.location = location;
		}

		public class GeoPosition {
			private double lat;
			private double longi;

			public double getLongi() {
				return longi;
			}

			public void setLongi(double longi) {
				this.longi = longi;
			}

			public double getLat() {
				return lat;
			}

			public void setLat(double lat) {
				this.lat = lat;
			}

		}
	}

}
