package com.variance.mimiprotect.contacts.managers;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.Pair;
import com.variance.mimiprotect.util.Settings;

public class ContactDetailsViewManager {
	private static final String NAME_ID = "Name";
	// to be used recursively in order
	public static final String PHONE_ID = "Phone ";
	public static final String EMAIL_ID = "Email ";
	public static final String WEBSITE_ID = "Website";
	public static final String ADDRESS_ID = "Address";
	public static final String COUNTRY_ID = "Country";
	public static final String GROUP_ID = "group";
	public static final String TITLE_ID = "Title";
	public static final String ORGANIZATION_ID = "Organization";
	public static final String COMPANY_ID = "Company";

	private Contact contact;
	private Activity context;
	private ContactDetailsListAdapter adapter;
	private ProgressDialog progressDialog;

	public ContactDetailsViewManager(Contact contact, Activity context) {
		super();
		this.contact = contact;
		this.context = context;
	}

	public ContactDetailsViewManager(Contact contact) {
		super();
		this.contact = contact;
	}

	private void showProgress(String message) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(true);
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}

	private void endProgress() {
		progressDialog.dismiss();
	}

	public void initializeAndShow() {
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.usercontact_contactdetailsview);
		dialog.setTitle("Contact Details");
		View titleView = dialog.getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView
						.setBackgroundResource(R.drawable.mimi_connect_background);
			}
		}
		// add components
		adapter = new ContactDetailsListAdapter(context,
				R.layout.usercontact_singlecontactdetailview, getDetails(),
				contact.isCorporateContact());
		ListView listView = (ListView) dialog
				.findViewById(R.id.contactDetailsView);
		listView.setAdapter(adapter);
		Button btnUpdate = (Button) dialog.findViewById(R.id.updateContactView);
		if (contact.isCorporateContact()) {
			btnUpdate.setEnabled(false);
			btnUpdate.setVisibility(View.GONE);
		} else {
			btnUpdate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					new AsyncTask<String, Void, String>() {

						@Override
						protected void onPostExecute(String result) {
							endProgress();
							Toast.makeText(context, result, Toast.LENGTH_LONG)
									.show();
							if (!contact.isCorporateContact()) {
								MimiProtectGeneralManager
										.updateContact(contact);
							}
						}

						@Override
						protected void onPreExecute() {
							showProgress("Saving...");
						}

						@Override
						protected String doInBackground(String... params) {
							String name = adapter.getValue(NAME_ID);
							int j = 0;
							for (; j < contact.getPhones().length; j++) {
								String phone = adapter.getValue(PHONE_ID + j);
								if (phone != null) {
									contact.setPhone(j, phone);
								}
							}
							// do we have an extra phone?
							String extraPhone = adapter.getValue(PHONE_ID + j);
							if (extraPhone != null
									&& !"".equals(extraPhone.trim())) {
								contact.setPhone(j, extraPhone);
							}
							int i = 0;
							for (; i < contact.getEmails().length; i++) {
								String email = adapter.getValue(EMAIL_ID + i);
								if (email != null) {
									contact.setEmail(i, email);
								}
							}
							// do we have an extra email?
							String extraEmail = adapter.getValue(EMAIL_ID + i);
							if (extraEmail != null
									&& !"".equals(extraEmail.trim())) {
								contact.setEmail(i, extraEmail);
							}
							String website = adapter.getValue(WEBSITE_ID);
							String address = adapter.getValue(ADDRESS_ID);
							String group = adapter.getValue(GROUP_ID);
							String organization = adapter
									.getValue(ORGANIZATION_ID);
							String title = adapter.getValue(TITLE_ID);
							if (address != null)
								contact.setAddress(address);
							if (name != null) {
								contact.setName(name);
							}
							if (website != null) {
								contact.setWebsite(website);
							}
							if (group != null) {
								contact.setGroup(group);
							}
							// title?
							if (title != null) {
								contact.setTitle(title);
							}
							if (organization != null) {
								contact.setOrganization(organization);
							}
							Log.i("Contact:", contact.toString());
							// we need to send this data to server
							String result = HttpRequestManager.doRequest(
									Settings.getContactManagerUrl(),
									Settings.makeContactUpdateParameters(contact));
							return result;
						}
					}.execute(new String[] {});
				}
			});
		}
		dialog.show();
	}

	private ArrayList<Pair<String, String>> getDetails() {
		ArrayList<Pair<String, String>> details = new ArrayList<Pair<String, String>>();
		details.add(new Pair<String, String>(NAME_ID, contact.getName()));
		if (!contact.isCorporateContact()) {
			// add title and organization by default
			// to appear after the name straight away
			details.add(new Pair<String, String>(ORGANIZATION_ID, contact
					.getOrganization()));
			details.add(new Pair<String, String>(TITLE_ID, contact.getTitle()));
		}
		if (contact.isCorporateContact()) {
			details.add(new Pair<String, String>(COMPANY_ID, contact
					.getCompanyName()));
		}
		int i = 0;
		for (; contact.getPhones() != null && i < contact.getPhones().length; i++) {
			details.add(new Pair<String, String>(PHONE_ID + i, contact
					.getPhones()[i]));
		}
		if (!contact.isCorporateContact()) {
			// add an extra id for new phone number
			details.add(new Pair<String, String>(PHONE_ID + i, ""));
		}
		i = 0;
		for (; contact.getEmails() != null && i < contact.getEmails().length; i++) {
			details.add(new Pair<String, String>(EMAIL_ID + i, contact
					.getEmails()[i]));
		}
		if (!contact.isCorporateContact()) {
			// add an extra email id for adding
			details.add(new Pair<String, String>(EMAIL_ID + i, ""));
		}
		if (contact.getWebsite() != null) {
			details.add(new Pair<String, String>(WEBSITE_ID, contact
					.getWebsite()));
		}
		if (contact.getAddress() != null) {
			details.add(new Pair<String, String>(ADDRESS_ID, contact
					.getAddress()));
		}
		if (contact.getCountry() != null) {
			details.add(new Pair<String, String>(COUNTRY_ID, contact
					.getCountry()));
		}
		if (!contact.isCorporateContact()) {
			// add group editing by default
			details.add(new Pair<String, String>(GROUP_ID, contact.getGroup()));
		}
		return details;
	}
}
