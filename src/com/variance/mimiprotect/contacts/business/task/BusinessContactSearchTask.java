package com.variance.mimiprotect.contacts.business.task;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.BusinessContactActivity;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.contact.ContactOptionsActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class BusinessContactSearchTask extends
		AsyncTask<SearchParameter, Void, String> {
	private BusinessContactActivity businessContactActivity;
	private boolean executeOnBackground;

	public BusinessContactSearchTask(
			BusinessContactActivity businessContactActivity) {
		super();
		this.businessContactActivity = businessContactActivity;
	}

	public BusinessContactSearchTask(
			BusinessContactActivity businessContactActivity,
			boolean executeOnBackground) {
		super();
		this.businessContactActivity = businessContactActivity;
		this.executeOnBackground = executeOnBackground;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!executeOnBackground) {
			PersonalPhonebookActivity.endProgress();
		}
		loadCustomView(result);
	}

	@Override
	protected void onPreExecute() {
		if (!executeOnBackground) {
			PersonalPhonebookActivity.showProgress(
					"Searching Business Contacts", businessContactActivity,
					this);
		}
	}

	@Override
	protected String doInBackground(SearchParameter... arg0) {
		if (arg0 != null && arg0.length > 0) {
			SearchParameter search = arg0[0];
			return HttpRequestManager.doRequest(
					Settings.getBusinessContactUrl(),
					Settings.makeLoadBusinessContactParameters(search));
		}
		return null;
	}

	public void loadViewFromCache(List<Contact> contacts) {
		loadCustomView(contacts);
	}

	private void sieveContacts(List<Contact> contacts) {
		for (ListIterator<Contact> it = contacts.listIterator(); it.hasNext();) {
			if (!it.next().isValidContact()) {
				it.remove();
			}
		}
	}

	private void loadCustomView(String result) {
		final ArrayList<Contact> contacts = DataParser
				.getBusinessContacts(result);
		sieveContacts(contacts);
		ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(
				businessContactActivity,
				R.layout.businesscontact_singlecontactview, contacts) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(
						R.layout.businesscontact_singlecontactview, parent,
						false);
				TextView textView = (TextView) rowView
						.findViewById(R.id.viewBusinessContact);
				textView.setText(contacts.get(position).getName());
				addOnClickListener(textView, position);
				return rowView;
			}

			private void addOnClickListener(TextView view, final int position) {
				view.setOnClickListener(new OnClickListener() {
					Context context = businessContactActivity;

					public void onClick(View v) {
						Contact c = contacts.get(position);
						if (c != null && !c.isBusinessContactHeaderStart()
								&& !c.isPersonalContactHeaderStart()
								&& getContext() != null) {
							Intent intent = new Intent(context,
									GeneralTabActivity.class);
							Intent callingintent = ((Activity) context)
									.getIntent();
							if (callingintent != null) {
								intent.putExtras(callingintent);
							}
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
									ContactOptionsActivity.class.getName());
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
									"mimi");
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT,
									c);
							context.startActivity(intent);
						}
					}
				});
			}
		};
		ListView contactView = (ListView) businessContactActivity
				.findViewById(R.id.businessContactView);
		contactView.setAdapter(adapter);
	}

	private void loadCustomView(final List<Contact> contacts) {
		sieveContacts(contacts);
		ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(
				businessContactActivity,
				R.layout.businesscontact_singlecontactview, contacts) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(
						R.layout.businesscontact_singlecontactview, parent,
						false);
				TextView textView = (TextView) rowView
						.findViewById(R.id.viewBusinessContact);
				textView.setText(contacts.get(position).getName());
				addOnClickListener(textView, position);
				return rowView;
			}

			private void addOnClickListener(TextView view, final int position) {
				view.setOnClickListener(new OnClickListener() {
					Context context = businessContactActivity;

					public void onClick(View v) {
						Contact c = contacts.get(position);
						if (c != null && !c.isBusinessContactHeaderStart()
								&& !c.isPersonalContactHeaderStart()
								&& getContext() != null) {
							Intent intent = new Intent(context,
									GeneralTabActivity.class);
							Intent callingintent = ((Activity) context)
									.getIntent();
							if (callingintent != null) {
								intent.putExtras(callingintent);
							}
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
									ContactOptionsActivity.class.getName());
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
									"mimi");
							intent.putExtra(
									MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT,
									c);
							context.startActivity(intent);
						}
					}
				});
			}
		};
		ListView contactView = (ListView) businessContactActivity
				.findViewById(R.id.businessContactView);
		contactView.setAdapter(adapter);
	}
}
