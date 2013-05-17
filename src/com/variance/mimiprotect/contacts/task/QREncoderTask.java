package com.variance.mimiprotect.contacts.task;

import info.ineighborhood.cardme.io.VCardWriter;
import info.ineighborhood.cardme.vcard.VCard;
import info.ineighborhood.cardme.vcard.VCardImpl;
import info.ineighborhood.cardme.vcard.features.BeginFeature;
import info.ineighborhood.cardme.vcard.features.EmailFeature;
import info.ineighborhood.cardme.vcard.features.EndFeature;
import info.ineighborhood.cardme.vcard.features.FormattedNameFeature;
import info.ineighborhood.cardme.vcard.features.TelephoneFeature;
import info.ineighborhood.cardme.vcard.types.BeginType;
import info.ineighborhood.cardme.vcard.types.EmailType;
import info.ineighborhood.cardme.vcard.types.EndType;
import info.ineighborhood.cardme.vcard.types.FormattedNameType;
import info.ineighborhood.cardme.vcard.types.NameType;
import info.ineighborhood.cardme.vcard.types.TelephoneType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.ImageViwerActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;

public class QREncoderTask extends AsyncTask<String, Void, String> {
	private Activity activity;
	private String qrEncodeURL = Settings.getQREncoderURL();
	private File vcardFile = null;

	public QREncoderTask(Activity activity, ProgressDialog pd) {
		this.activity = activity;
	}

	private String getBusinessCardAsVCard() {
		String vcardStr = null;
		String data = HttpRequestManager.doRequest(
				Settings.getProfileRequestUrl(),
				Settings.getProfileRequestLoadParameter());
		User user = DataParser.getUserFrom(data);
		VCard vcard = new VCardImpl();
		FormattedNameFeature fn = null;

		NameType name = null;

		String surname = user.getSurname();
		if (surname != null && surname.length() > 0) {
			name = new NameType();
			name.setFamilyName(surname);
			fn = new FormattedNameType();
			fn.setFormattedName(surname);
		}
		String givenName = user.getOtherNames();
		if (givenName != null && givenName.length() > 0) {
			if (name == null)
				name = new NameType();
			name.setGivenName(givenName);
			if (fn == null) {
				fn = new FormattedNameType();
				fn.setFormattedName(givenName);
			} else {
				fn.setFormattedName(givenName + "  " + fn.getFormattedName());
			}
		}

		List<EmailFeature> emailTypes = null;
		List<String> emails = user.getEmails();
		if (emails != null && emails.size() > 0) {
			emailTypes = new ArrayList<EmailFeature>(emails.size());
		}
		for (String email : emails) {
			emailTypes.add(new EmailType(email));
		}

		List<TelephoneFeature> phoneTypes = null;

		List<String> phones = user.getPhones();
		if (phones.size() > 0) {
			phoneTypes = new ArrayList<TelephoneFeature>(phones.size());
		}
		for (String phone : phones) {
			phoneTypes.add(new TelephoneType(phone));
		}
		if (fn != null) {
			vcard = new VCardImpl();
			BeginFeature begin = new BeginType();
			vcard.setBegin(begin);
			vcard.setFormattedName(fn);
			if (name != null)
				vcard.setName(name);
			if (phoneTypes != null)
				vcard.addAllTelephoneNumber(phoneTypes);
			if (emailTypes != null)
				vcard.addAllEmails(emailTypes);
			EndFeature end = new EndType();
			vcard.setEnd(end);
			try {
				VCardWriter writer = new VCardWriter();
				writer.setVCard(vcard);
				vcardStr = writer.buildVCardString();
				// Log.e("vc", vcardStr);
			} catch (Exception e) {
				Log.e("vc", e.toString());
			}

			return vcardStr;
		}
		return null;
	}

	@SuppressLint("WorldReadableFiles")
	private void downloadVCard(String vcardStr) {
		HttpURLConnection connection;
		try {
			vcardStr = URLEncoder.encode(vcardStr);
			qrEncodeURL = qrEncodeURL
					.concat("?cht=qr&chs=150x150&choe=utf-8&chl=" + vcardStr);
			Log.e("download", qrEncodeURL);
			URL url = new URL(qrEncodeURL);
			if (url != null)
				Log.e("download", "url not null");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream is = connection.getInputStream();
			File root = null;
			if (root == null) {
				try {
					// root = Environment.getExternalStorageDirectory();
				} catch (Exception e) {
				}
			}
			// vcardFile = activity.getFileStreamPath(Settings.FILE_QR_PATH);
			// if (vcardFile != null)
			// // Log.e("download", "file not null");
			// if (!vcardFile.exists()) {
			// vcardFile.createNewFile();
			// }
			FileOutputStream fos = null;// new FileOutputStream(vcardFile);
			fos = activity.openFileOutput(Settings.FILE_QR_PATH,
					Context.MODE_WORLD_READABLE);
			vcardFile = activity.getFileStreamPath(Settings.FILE_QR_PATH);
			Settings.setQRFilepath(vcardFile.getPath());
			byte[] data = new byte[1024];
			int size = 0;
			int count = 0;
			while ((size = is.read(data)) > -1) {
				fos.write(data, 0, size);
				count += size;
			}
			is.close();
			fos.close();
			connection.disconnect();
			Log.e("QR Encoder", "finished " + count + " " + vcardFile.getPath());
			// start an activity to display photo
			// Toast.makeText(activity, "complete", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e("QR Encoder", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			// pd.show();
			Log.e("encoder task", "starting .. ");
			String vcardStr = getBusinessCardAsVCard();
			Log.e("encoder task", "len " + vcardStr.length());
			downloadVCard(vcardStr);
			// Intent intent = new Intent();
			// intent.setAction(android.content.Intent.ACTION_VIEW);
			// intent.setDataAndType(Uri.fromFile(vcardFile), "image/png");
			Intent intent = new Intent(activity, ImageViwerActivity.class);
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.e("encoder task", e.toString());
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Generating QR Code...",
				activity);
	}

	@Override
	protected void onPostExecute(String result) {
		PersonalPhonebookActivity.endProgress();
	}

}
