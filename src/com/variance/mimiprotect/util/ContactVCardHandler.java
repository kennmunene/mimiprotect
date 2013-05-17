package com.variance.mimiprotect.util;

import info.ineighborhood.cardme.engine.VCardEngine;
import info.ineighborhood.cardme.vcard.VCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.contacts.VCardAndroidParser;
import com.variance.mimiprotect.request.HttpRequestManager;

public class ContactVCardHandler {

	private VCardAndroidParser vcardAndroidParser;
	private Activity context;

	public ContactVCardHandler(Activity context) {
		super();
		this.context = context;
		vcardAndroidParser = new VCardAndroidParser();
	}

	public String createContact(String vcardString, boolean saveOnline,
			boolean saveLocally) {
		VCardEngine vcardEngine = new VCardEngine();
		VCard vcard = null;
		String message = "";
		try {
			vcard = vcardEngine.parse(vcardString);
		} catch (Exception e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}
		if (vcard != null) {
			try {
				if (saveLocally) {
					message = saveContactToPhone(vcard);
				}
			} catch (Exception ex) {
			}
			try {
				if (saveOnline) {
					return message + "\n" + saveContactOnline(vcardString);
				}
			} catch (Exception ex) {
			}
		}
		return "failed to save contact";
	}

	public String saveContactToPhone(VCard vcard) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		// Asking the Contact provider to create a new contact
		try {
			ops = vcardAndroidParser.getContentProviderOperations(vcard);
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
			return "Contact Saved Locally.";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "contact not saved locally.";
	}

	private String saveContactOnline(final String vcardStr) {
		try {
			String contact = vcardStr;
			Map<String, String> data = new HashMap<String, String>();
			data.put("action", "backup");
			data.put("contact", contact);
			data.put("sessionID", Settings.getSessionID());
			String result = HttpRequestManager.doRequest(
					Settings.getBackupURL(), data);
			Log.e("Response:", result);
			return "Contact Saved Online";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "failed: Contact not saved online";
	}

}
