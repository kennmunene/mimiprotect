package com.variance.mimiprotect.contacts.business.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;

public class BusinessContacSelectFromPersonalContacttAdapter extends
		ArrayAdapter<Contact> {
	private List<Contact> selectedContacts;
	private List<Contact> contacts;
	private LayoutInflater inflater = null;
	private Map<View, CheckBox> rowViews;
	private CheckBox selectAllCheckBox;

	public BusinessContacSelectFromPersonalContacttAdapter(Context context,
			int resourceId, List<Contact> contacts,
			List<Contact> selectedContacts) {
		super(context, resourceId, contacts);
		this.contacts = contacts;
		this.selectedContacts = selectedContacts;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowViews = new HashMap<View, CheckBox>();
	}

	public BusinessContacSelectFromPersonalContacttAdapter(Context context,
			int resourceId, List<Contact> contacts,
			List<Contact> selectedContacts, CheckBox selectAllCheckBox) {
		super(context, resourceId, contacts);
		this.contacts = contacts;
		this.selectedContacts = selectedContacts;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowViews = new HashMap<View, CheckBox>();
		this.selectAllCheckBox = selectAllCheckBox;
		if (this.selectAllCheckBox != null) {
			this.selectAllCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								selectAllContacts();
							} else {
								deselectAllContacts();
							}
						}
					});
		}
	}

	@Override
	public View getView(int position, View convertView0, ViewGroup parent) {
		View convertView = inflater
				.inflate(
						R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
						parent, false);
		final CheckBox cb = (CheckBox) convertView
				.findViewById(R.id.cbSelectPersonalContact);
		TextView txtView = (TextView) convertView
				.findViewById(R.id.txtSelectPersonalContact);
		final Contact c = contacts.get(position);
		// make the check box checked, if the contact on view has been
		// selected previously
		for (Contact c0 : selectedContacts) {
			if (c0.getId().equals(c.getId())) {
				cb.setChecked(true);
				break;
			}
		}
		txtView.setText(c.getName());
		rowViews.put(convertView, cb);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!selectedContacts.contains(c)) {
						selectedContacts.add(c);
					}
				} else {
					selectedContacts.remove(c);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (selectedContacts.contains(c)) {
					selectedContacts.remove(c);
					cb.setChecked(false);
				} else {
					selectedContacts.add(c);
					cb.setChecked(true);
				}
			}
		});
		return convertView;
	}

	public void selectAllContacts() {
		this.selectedContacts.clear();
		this.selectedContacts.addAll(contacts);
		for (Map.Entry<View, CheckBox> e : rowViews.entrySet()) {
			e.getValue().setChecked(true);
		}
	}

	public void deselectAllContacts() {
		this.selectedContacts.clear();
		for (Map.Entry<View, CheckBox> e : rowViews.entrySet()) {
			e.getValue().setChecked(false);
		}
	}

	static class ViewHolder {
		CheckBox cb;
	}

}
