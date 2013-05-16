package com.variance.mimiprotect.contacts.task;

import info.ineighborhood.cardme.engine.VCardEngine;
import info.ineighborhood.cardme.vcard.VCard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.contacts.VCardAndroidParser;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.Settings;

/**
 * TODO (marembo) This class needs a lot of refactoring.
 * 
 * @author marembo
 * 
 */
public class RestoreTask extends AsyncTask<String, Void, String> {

	private static String result = "";
	private InputStream is = null;
	private Activity activity;
	public static Map<String, String> map;
	private String vcard;
	private int totalContacts = 0;
	private boolean cancelled = false;

	public RestoreTask(Activity act) {
		this.activity = act;
	}

	private void show(String msg) {
		Log.e(RestoreTask.class.getName(), msg);
	}

	private void retrieveContacts() {
		Log.e(RestoreTask.class.getName(), "retrieving contacts");
		String contacts = "";
		int page = 0;
		do {
			Log.e(RestoreTask.class.getName(), "retrieval started page= "
					+ page);

			try {
				HttpResponse response = HttpRequestManager
						.doRequestWithResponse(Settings.getBackupURL(),
								Settings.makeRestoreAESParameters(page++));
				contacts = retrieveContactString(response);
				if (contacts.length() > 0) {
					saveContactToPhone(contacts);
				}
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}
			show("retrieved page " + page);

		} while (contacts.length() > 0 && !cancelled);

	}

	private void saveContactToPhone(String vcards) {
		VCardEngine ve = new VCardEngine();
		StringReader reader = new StringReader(vcards);
		BufferedReader bReader = new BufferedReader(reader);
		String line = null;
		String vcard = "";
		try {
			while ((line = bReader.readLine()) != null && !cancelled) {
				if (line.trim().startsWith("BEGIN:VCARD")) {
					vcard = line + "\n";
				} else if (line.trim().startsWith("END:VCARD")) {
					vcard += line;
					try {
						VCard vc = ve.parse(vcard);
						saveContactToPhone(vc);
						PersonalPhonebookActivity
								.updateProgressBar(totalContacts++);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					vcard += line + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveContactToPhone(VCard vcard) {
		VCardAndroidParser vcardAndroidParser = new VCardAndroidParser();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		// Asking the Contact provider to create a new contact
		try {
			ops = vcardAndroidParser.getContentProviderOperations(vcard);
			activity.getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			Log.e("writevcf", "Contact Saved To Phone");
		} catch (Exception e) {
			// e.printStackTrace();
			Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
			Log.e("", "Exception: " + e.getMessage());
		}
	}

	@Override
	protected void onCancelled() {
		cancelled = true;
	}

	@Override
	protected void onPreExecute() {
		int count = PersonalPhonebookActivity.maxAESPersonalRecords();
		PersonalPhonebookActivity.showProgress("Restoring Contacts", activity,
				this, true, count, true,
				"Are you sure you want to stop restoring!");

	}

	private String retrieveContactString(HttpResponse response)
			throws Exception {
		HttpEntity entity = response.getEntity();
		is = entity.getContent();
		BufferedReader bin = new BufferedReader(new InputStreamReader(is));
		String newLine = "";
		vcard = "";
		while ((newLine = bin.readLine()) != null) {
			if (newLine.startsWith("BEGIN:VCARD")) {
				vcard += newLine + "\n";
				while ((newLine = bin.readLine()) != null
						&& newLine.startsWith("END:VCARD") == false) {
					vcard += newLine + "\n";
				}
				if (newLine.startsWith("END:VCARD")) {
					vcard += newLine + "\n";
				}
			}
		}
		if (vcard.equals("")) {
			return "";
		}
		InputStream in = new ByteArrayInputStream(vcard.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		result = sb.toString();
		// Log.e("VCard: ", result);
		return result;
	}

	@Override
	protected String doInBackground(String... url) {
		String taskResult = "";
		try {
			retrieveContacts();
		} catch (Exception e) {
			result = "";
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		result = "";
		return taskResult;
	}

	@Override
	protected void onPostExecute(String result) {
		PersonalPhonebookActivity.endProgress();
	}
}