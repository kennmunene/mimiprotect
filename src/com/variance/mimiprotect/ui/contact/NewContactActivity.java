package com.variance.mimiprotect.ui.contact;

import info.ineighborhood.cardme.engine.VCardEngine;
import info.ineighborhood.cardme.vcard.VCard;
import info.ineighborhood.cardme.vcard.features.EmailFeature;
import info.ineighborhood.cardme.vcard.features.FormattedNameFeature;
import info.ineighborhood.cardme.vcard.features.NameFeature;
import info.ineighborhood.cardme.vcard.features.OrganizationFeature;
import info.ineighborhood.cardme.vcard.features.TelephoneFeature;
import info.ineighborhood.cardme.vcard.features.TitleFeature;

import java.util.Calendar;
import java.util.Iterator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.integration.IntentIntegrator;
import com.variance.mimiprotect.integration.IntentResult;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.util.ContactVCardHandler;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class NewContactActivity extends MimiProtectActivity {
	private boolean isDefaultBusinessContact;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_newcontact);
		Intent i = getIntent();
		if (i != null) {
			isDefaultBusinessContact = i
					.getBooleanExtra(
							MimiProtectIntentConstants.MIMI_PROTECT_IS_BUSINESS_CONTACT,
							false);
		}
		if (i != null
				&& i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_FROM_CREATE_CONTACT)) {
			// lets check if we have a value for contact.
			String phone = i.getStringExtra("phone");
			if (phone != null) {
				EditText txtPhone = (EditText) findViewById(R.id.txtProfileNumber);
				if (txtPhone != null) {
					txtPhone.setText(phone);
				}
			}
		}
		if (isDefaultBusinessContact) {
			((TextView) findViewById(R.id.newContact))
					.setText("New Business Contact");
		}
		if (i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_NEWCONTACT_FROM_QR)
				&& i.getBooleanExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_NEWCONTACT_FROM_QR,
						false)) {
			scanBusinessCard();
		}
		String dialingCode = Settings.getCurrentSIMCountryCode(this);
		// we can only set a hint for the code
		if (!Utils.isNullStringOrEmpty(dialingCode)) {
			EditText txtProfileNumber = (EditText) findViewById(R.id.txtProfileNumber);
			txtProfileNumber.setHint(dialingCode);
		}
	}

	private String getVcard() {
		// generate the basic card here
		String surname = ((EditText) findViewById(R.id.txtFirstName)).getText()
				.toString().trim();
		String otherNames = ((EditText) findViewById(R.id.txtLastName))
				.getText().toString().trim();
		String phoneNumber = ((EditText) findViewById(R.id.txtProfileNumber))
				.getText().toString().trim();
		String emailAddress = ((EditText) findViewById(R.id.profileEmail))
				.getText().toString().trim();
		String company = ((EditText) findViewById(R.id.profileCompany))
				.getText().toString().trim();
		String title = ((EditText) findViewById(R.id.profileTitle)).getText()
				.toString().trim();
		String vcard = "BEGIN:VCARD\n";
		vcard += "N:" + surname + ";" + otherNames + "\n";
		vcard += "FN:" + surname + " " + otherNames + "\n";
		vcard += "TEL;TYPE=WORK,VOICE:" + phoneNumber + "\n";
		vcard += "EMAIL;TYPE=PREF,INTERNET:" + emailAddress + "\n";
		vcard += "TITLE:" + title + "\n";
		vcard += "ORG:" + company + "\n";
		vcard += "REV:" + Utils.toIsoTString(Calendar.getInstance()) + "\n";
		vcard += "END:VCARD";
		return vcard;
	}

	@Override
	protected void onResume() {
		Intent i = getIntent();
		if (i != null
				&& i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_FROM_CREATE_CONTACT)) {
			// lets check if we have a value for contact.
			String phone = i.getStringExtra("phone");
			if (phone != null) {
				EditText txtPhone = (EditText) findViewById(R.id.txtProfileNumber);
				if (txtPhone != null) {
					txtPhone.setText(phone);
				}
			}
		}
		super.onResume();
	}

	private String getBusinessContactData() {
		// generate the basic card here
		PersonalPhonebookActivity.showProgress("Please wait...", this);
		String surname = ((EditText) findViewById(R.id.txtFirstName)).getText()
				.toString().trim();
		String otherNames = ((EditText) findViewById(R.id.txtLastName))
				.getText().toString().trim();
		String phoneNumber = ((EditText) findViewById(R.id.txtProfileNumber))
				.getText().toString().trim();
		String emailAddress = ((EditText) findViewById(R.id.profileEmail))
				.getText().toString().trim();
		String company = ((EditText) findViewById(R.id.profileCompany))
				.getText().toString().trim();
		String title = ((EditText) findViewById(R.id.profileTitle)).getText()
				.toString().trim();
		return DataParser.getNewBusinessContactData(surname, otherNames,
				phoneNumber, emailAddress, company, title);
	}

	private boolean isValidContact() {
		String surname = ((EditText) findViewById(R.id.txtFirstName)).getText()
				.toString().trim();
		String otherNames = ((EditText) findViewById(R.id.txtLastName))
				.getText().toString().trim();
		if ((surname == null || "".equals(surname.trim()))
				&& (otherNames == null || "".equals(otherNames.trim()))) {
			return false;
		}
		return true;
	}

	public void cancelAddNewContact(View view) {
		finish();
	}

	public void addContact(View view) {
		if (isDefaultBusinessContact) {
			addContactToBusinessPhonebook(view);
		} else {
			addContactToMyPhonebook(view);
		}
	}

	public void addContactToMyPhonebook(View view) {
		// generate the basic card here
		if (!isValidContact()) {
			Toast.makeText(this,
					"Please specify at least the name of the contact",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String vcard = getVcard();
		doAddContactToMyPhonebbok(vcard);
	}

	private void doAddContactToMyPhonebbok(final String vcard) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				PersonalPhonebookActivity.endProgress();
				Toast.makeText(NewContactActivity.this,
						"Completed adding contact: \n" + result,
						Toast.LENGTH_SHORT).show();
				MimiProtectGeneralManager
						.onNewContactAdded(PhonebookType.PRIVATE);
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						NewContactActivity.this);
			}

			@Override
			protected String doInBackground(Void... params) {
				return new ContactVCardHandler(NewContactActivity.this)
						.createContact(vcard, true, true);
			}

		}.execute();
	}

	public void addContactToBusinessPhonebook(View view) {
		if (!isValidContact()) {
			Toast.makeText(this,
					"Please specify at least the name of the contact",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String contactStr = getBusinessContactData();
		new AsyncTask<String, String, HttpResponseData>() {

			@Override
			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(
							NewContactActivity.this,
							result.getResponseStatus() + ": "
									+ result.getMessage(), Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(NewContactActivity.this,
							"Error, received no response from the server",
							Toast.LENGTH_LONG).show();
				}
				MimiProtectGeneralManager
						.onNewContactAdded(PhonebookType.OFFICE);
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Adding contacts...",
						NewContactActivity.this);
			}

			@Override
			protected HttpResponseData doInBackground(String... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessContactUrl(),
						Settings.makeAddBusinessContactNewContact(contactStr));
			}

		}.execute(new String[] {});
	}

	public void scanBusinessCard(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
	}

	public void scanBusinessCard() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		try {
			IntentResult result = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (result != null) {
				String format = result.getFormatName();
				if (format.contains("QR")) {
					String vcard = result.getContents();
					showQrScannedContact(vcard);
					if (isDefaultBusinessContact) {
						addContactToBusinessPhonebook(null);
					} else {
						doAddContactToMyPhonebbok(vcard);
					}
				}
			} else {
				Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Error:" + e.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showQrScannedContact(String vcardString) {
		VCardEngine engine = new VCardEngine();
		try {
			VCard vcard = engine.parse(vcardString);
			String surname = null;
			String otherNames = null;
			NameFeature nf = vcard.getName();
			if (nf != null) {
				surname = nf.getFamilyName();
				otherNames = nf.getGivenName();
			} else {
				FormattedNameFeature fnf = vcard.getFormattedName();
				if (fnf != null) {
					surname = fnf.getFormattedName();
					String[] names = surname.split(" ");
					if (names.length >= 2) {
						surname = names[0];
						otherNames = names[1];
					}
				}
			}
			String title = null;
			TitleFeature titleF = vcard.getTitle();
			if (titleF != null) {
				title = titleF.getTitle();
			}
			String company = null;
			OrganizationFeature org = vcard.getOrganizations();
			if (org != null && org.getOrganizations() != null) {
				for (Iterator<String> it = org.getOrganizations(); it.hasNext();) {
					company = it.next();
					if (!Utils.isNullOrEmpty(company)) {
						break;
					}
				}
			}
			String email = null;
			Iterator<EmailFeature> it = vcard.getEmails();
			if (it != null) {
				for (; it.hasNext();) {
					EmailFeature ef = it.next();
					email = ef.getEmail();
					if (!Utils.isNullOrEmpty(email)) {
						break;
					}
				}
			}
			String phone = null;
			Iterator<TelephoneFeature> phones = vcard.getTelephoneNumbers();
			if (phones != null) {
				for (; phones.hasNext();) {
					TelephoneFeature ef = phones.next();
					phone = ef.getTelephone();
					if (!Utils.isNullOrEmpty(phone)) {
						break;
					}
				}
			}
			show(surname, otherNames, phone, email, company, title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void show(String surname, String otherNames, String phoneNumber,
			String email, String company, String title) {
		((EditText) findViewById(R.id.txtFirstName)).setText(surname);
		((EditText) findViewById(R.id.txtLastName)).setText(otherNames);
		((EditText) findViewById(R.id.txtProfileNumber)).setText(phoneNumber);
		((EditText) findViewById(R.id.profileEmail)).setText(email);
		((EditText) findViewById(R.id.profileCompany)).setText(company);
		((EditText) findViewById(R.id.profileTitle)).setText(title);
	}
}
