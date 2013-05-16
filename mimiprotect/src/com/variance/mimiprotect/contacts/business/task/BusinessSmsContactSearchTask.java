package com.variance.mimiprotect.contacts.business.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.BusinessSmsMessageActivity;
import com.variance.mimiprotect.contacts.business.task.BusinessContacSelectFromPersonalContacttAdapter.ViewHolder;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class BusinessSmsContactSearchTask extends
		AsyncTask<SearchParameter, Void, String> {
	private BusinessSmsMessageActivity smsMessenger;
	private boolean executeOnBackground;
	private List<Contact> selectedContacts;

	public BusinessSmsContactSearchTask(BusinessSmsMessageActivity smsMessenger) {
		super();
		this.smsMessenger = smsMessenger;
		this.selectedContacts = new ArrayList<Contact>();
	}

	public BusinessSmsContactSearchTask(
			BusinessSmsMessageActivity smsMessenger, boolean executeOnBackground) {
		super();
		this.smsMessenger = smsMessenger;
		this.executeOnBackground = executeOnBackground;
		this.selectedContacts = new ArrayList<Contact>();
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
					"Searching Business Contacts", smsMessenger, this);
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

	public List<Contact> getSelectedContacts() {
		return selectedContacts;
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

	private void loadListView(final List<Contact> contacts) {
		ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(smsMessenger,
				R.layout.businesscontact_singlecontactview, contacts) {

			private Map<CheckBox, View> currentRadioButtons;
			private LayoutInflater inflater;
			{
				inflater = (LayoutInflater) smsMessenger
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				currentRadioButtons = new HashMap<CheckBox, View>();
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				TextView txtView = null;
				CheckBox selectBtn = null;
				ViewHolder vh = null;
				convertView = inflater
						.inflate(R.layout.usercontact_singleselectionview,
								parent, false);
				txtView = (TextView) convertView
						.findViewById(R.id.usercontact_contactTextView);
				selectBtn = (CheckBox) convertView
						.findViewById(R.id.usercontact_selectBtn);
				return getView(position, convertView, txtView, selectBtn, vh);
			}

			public View getView(final int position, final View convertView,
					TextView txtView, CheckBox selectBtn, ViewHolder vh) {
				final Contact c = contacts.get(position);
				txtView.setText(c.getName());
				currentRadioButtons.put(selectBtn, convertView);
				selectBtn.setChecked(getSelectedContacts().contains(c));
				selectBtn
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									if (!getSelectedContacts().contains(c)) {
										getSelectedContacts().add(c);
									}
								} else {
									getSelectedContacts().remove(c);
								}
							}
						});
				addOnClickListener(convertView, selectBtn, position);
				return convertView;
			}

			private void addOnClickListener(final View view,
					final CheckBox checkBox, final int position) {
				view.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Contact c = contacts.get(position);
						boolean alreadyAdded = getSelectedContacts()
								.contains(c);
						if (!alreadyAdded) {
							getSelectedContacts().add(c);
						} else {
							getSelectedContacts().remove(c);
						}
						checkBox.setChecked(!alreadyAdded);
					}
				});
			}
		};
		ListView contactView = (ListView) smsMessenger
				.findViewById(R.id.selectBusinessContacts);
		contactView.setAdapter(adapter);
	}

	private void loadCustomView(String result) {
		final ArrayList<Contact> contacts = DataParser
				.getBusinessContacts(result);
		sieveContacts(contacts);
		loadListView(contacts);
	}

	private void loadCustomView(final List<Contact> contacts) {
		sieveContacts(contacts);
		loadListView(contacts);
	}
}
