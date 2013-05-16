package com.variance.mimiprotect.contacts.business.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.Utils;

public class BusinessContactListAdapter extends ArrayAdapter<String> {
	private ArrayList<Contact> contacts;
	private String[] strContacts;
	private Context context;

	public BusinessContactListAdapter(Context context, int textViewResourceId,
			ArrayList<Contact> contacts) {
		super(context, textViewResourceId, Utils.contactsToString(contacts));
		this.contacts = contacts;
		strContacts = Utils.contactsToString(contacts);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		rowView = inflater.inflate(R.layout.businesscontact_singlecontactview,
				parent, false);
		TextView textView = (TextView) rowView
				.findViewById(R.id.viewBusinessContact);
		textView.setText(strContacts[position]);
		addOnClickListener(textView, position);
		return rowView;
	}

	private void addOnClickListener(TextView view, final int position) {
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Contact c = contacts.get(position);
				if (c != null && !c.isBusinessContactHeaderStart()
						&& !c.isPersonalContactHeaderStart() && context != null
						&& context instanceof PersonalPhonebookActivity) {
					// PersonalPhonebookActivity.manageContactOptions(c,
					// (BusinessContactActivity) context);
				}
			}
		});
	}
}
