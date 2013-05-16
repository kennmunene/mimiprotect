package com.variance.mimiprotect.contacts.task;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.ActionParameterValue;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.Settings;

public class BackupTask extends AsyncTask<Void, Void, List<String>> {

	private Activity activity;
	int uploadedCount = 0;
	Cursor cur = null;

	public BackupTask(Activity activity) {
		this.activity = activity;

	}

	private void show(String msg) {
		Log.e(BackupTask.class.getName(), msg);
	}

	private void readContacts() {
		show("starting to read contacts");
		List<String> list = new ArrayList<String>();
		int maxContacts = 30;
		int currCount = 0;
		while (cur.moveToNext() && !super.isCancelled()) {
			// show("cursor: ");
			currCount++;
			String lookupKey = cur.getString(cur
					.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			Uri uri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
			AssetFileDescriptor fd;
			try {
				fd = activity.getContentResolver().openAssetFileDescriptor(uri,
						"r");
				FileInputStream fis = fd.createInputStream();
				byte[] vCard = new byte[(int) fd.getDeclaredLength()];
				fis.read(vCard);
				fis.close();
				list.add(new String(vCard));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (currCount >= maxContacts) {
				currCount = 0; // reset counter
				// flush list
				uploadContacts(list);
				list.clear();
			}
		}
		uploadContacts(list);
	}

	@Override
	protected void onCancelled() {
		HttpRequestManager.doRequest(Settings.getBackupURL(), Settings
				.makeRequestParameter(
						ActionParameterValue.PERSONAL_CONTACT_BACKUP_COMPLETE,
						null));
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		List<String> list = new ArrayList<String>();
		try {
			readContacts();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private void uploadContacts(final List<String> r) {
		show("flushing contacts");
		List<String> result = r;
		final Iterator<String> contacts = result.iterator();
		while (contacts.hasNext() && !super.isCancelled()) {
			try {
				// update progress
				PersonalPhonebookActivity.updateProgressBar(uploadedCount++);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String contact = contacts.next();
				HttpRequestManager.doRequest(Settings.getBackupURL(),
						Settings.makeBackupAESParameters(contact));
			} catch (Exception e) {
				Log.e("Error:",
						e != null && e.getMessage() != null ? e.getMessage()
								: "Unknow Error");
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPreExecute() {
		// We are starting back up.
		HttpRequestManager.doRequest(Settings.getBackupURL(), Settings
				.makeRequestParameter(
						ActionParameterValue.PERSONAL_CONTACT_BACKUP_STARTED,
						null));
		ContentResolver cr = activity.getContentResolver();
		cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
				null);
		int max = cur.getCount();
		PersonalPhonebookActivity.showProgress("Backing up, Please wait...",
				activity, this, true, max, true,
				"Are you sure you want to stop Backing up?");

	}

	@Override
	protected void onPostExecute(final List<String> result) {
		PersonalPhonebookActivity.endProgress();
		HttpRequestManager.doRequest(Settings.getBackupURL(), Settings
				.makeRequestParameter(
						ActionParameterValue.PERSONAL_CONTACT_BACKUP_COMPLETE,
						null));
		cacheAfterBackup();
	}

	private void cacheAfterBackup() {
		HttpRequestTaskListener<Void, Void> listener = new HttpRequestTaskListener<Void, Void>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(Void result) {
			}

			public Void doTask(Void... params) {
				// load cache if necessary
				MimiProtectGeneralManager.syncCacheIfNecessary();
				return null;
			}
		};
		new HttpRequestTask<Void, Void, Void>(listener, null, this.activity)
				.executeInBackground();
	}
}