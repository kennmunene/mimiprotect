package com.variance.mimiprotect.contacts.task;

import info.ineighborhood.cardme.engine.VCardEngine;
import info.ineighborhood.cardme.vcard.VCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.contacts.VCardAndroidParser;
import com.variance.mimiprotect.util.Settings;

public class WriteVCFTask extends AsyncTask<String, Void, String> {
	private VCardAndroidParser vcardAndroidParser;
	Activity activity;
	ProgressDialog dialog;

	public WriteVCFTask(Activity act, ProgressDialog dialog) {
		this.activity = act;
		this.dialog = dialog;
		vcardAndroidParser = new VCardAndroidParser();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Settings.DIRECT_VCF_IN_PROGRESS = true;
		writeContactsToPhoneFromVCF();
		return "";
	}

	private void writeContactsToPhoneFromVCF() {
		Settings.DIRECT_VCF_REQUESTED = false;
		File root = null;
		File vCardfile = null;
		vcardAndroidParser = new VCardAndroidParser();
		StringBuilder sb = new StringBuilder();
		VCardEngine vengine = null;
		VCard vcard = null;
		try {
			vengine = new VCardEngine();
			if (root == null) {
				root = Environment.getExternalStorageDirectory();
			}
			if (root.canRead()) {
				vCardfile = new File(root, "contacts-temp.vcf");
				if (vCardfile == null || !vCardfile.exists()) {
					Toast.makeText(activity, "iwrite task: no file",
							Toast.LENGTH_SHORT).show();
					return;
				}
				vCardfile.deleteOnExit();
				FileReader vcfreader = new FileReader(vCardfile);
				BufferedReader reader = new BufferedReader(vcfreader);
				String vstr = "";
				while (vengine != null && (vstr = reader.readLine()) != null) {
					if (vstr.startsWith("BEGIN:VCARD")) {
						sb = new StringBuilder();
					}
					sb.append(vstr);
					sb.append('\n');
					if (vstr.startsWith("END:VCARD")) {
						// Log.e("", sb.toString());
						try {
							// Log.e("vcard", sb.toString());
							vcard = vengine.parse(sb.toString());
							saveContactToPhone(vcard);
						} catch (Exception e) {
							Log.e("writevcftask", e.toString());
						}
					}
				}

				if (vCardfile != null) {
					vCardfile.delete();
				}
			}
		} catch (IOException e) {
			// Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT);
			Log.e("RestoreTask", "Could not write file " + e.getMessage());
		}
	}

	private void saveContactToPhone(VCard vcard) {
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
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (dialog != null)
			dialog.dismiss();
	}

}
