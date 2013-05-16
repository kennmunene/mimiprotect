package com.variance.mimiprotect.contacts.selection;

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

public class ContactGeneralMultiSelectionAdapter extends ArrayAdapter<Contact> {

	private List<Contact> contacts;
	private Map<CheckBox, View> currentRadioButtons;
	private LayoutInflater inflater;
	private ContactGeneralSelectionManager selectionManager;

	public ContactGeneralMultiSelectionAdapter(Context context,
			int textViewResourceId, List<Contact> contacts,
			ContactGeneralSelectionManager selectionManager) {
		super(context, textViewResourceId, contacts);
		this.contacts = contacts;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		currentRadioButtons = new HashMap<CheckBox, View>();
		this.selectionManager = selectionManager;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView txtView = null;
		CheckBox selectBtn = null;
		ViewHolder vh = null;
		convertView = inflater.inflate(
				R.layout.usercontact_singleselectionview, parent, false);
		txtView = (TextView) convertView
				.findViewById(R.id.usercontact_contactTextView);
		selectBtn = (CheckBox) convertView
				.findViewById(R.id.usercontact_selectBtn);
		return getView(position, convertView, txtView, selectBtn, vh);
	}

	public View getView(final int position, final View convertView,
			TextView txtView, CheckBox selectBtn, ViewHolder vh) {
		final Contact c = this.contacts.get(position);
		txtView.setText(c.getName());
		currentRadioButtons.put(selectBtn, convertView);
		selectBtn.setChecked(getSelectedContacts().contains(c));
		selectBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!getSelectedContacts().contains(c)) {
						getSelectedContacts().add(c);
					}
				} else {
					getSelectedContacts().remove(c);
					if (selectionManager.isSelectAllContacts()) {
						selectionManager.setSelectAllContacts(false);
					}
				}
			}
		});
		addOnClickListener(convertView, selectBtn, position);
		return convertView;
	}

	private void addOnClickListener(final View view, final CheckBox checkBox,
			final int position) {
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Contact c = contacts.get(position);
				boolean alreadyAdded = getSelectedContacts().contains(c);
				if (!alreadyAdded) {
					getSelectedContacts().add(c);
				} else {
					getSelectedContacts().remove(c);
				}
				checkBox.setChecked(!alreadyAdded);
			}
		});
	}

	public List<Contact> getSelectedContacts() {
		return selectionManager.getSelectedContacts();
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void selectAllContacts() {
		getSelectedContacts().addAll(this.contacts);
		for (CheckBox b : currentRadioButtons.keySet()) {
			b.setChecked(true);
		}
		selectionManager.setSelectAllContacts(true);
	}

	public void deselectAllContacts() {
		getSelectedContacts().clear();
		for (CheckBox b : currentRadioButtons.keySet()) {
			b.setChecked(false);
		}
		selectionManager.setSelectAllContacts(false);
	}

	static class ViewHolder {
		public CheckBox getCb() {
			return cb;
		}

		public void setCb(CheckBox cb) {
			this.cb = cb;
		}

		public TextView getTextView() {
			return textView;
		}

		public void setTextView(TextView textView) {
			this.textView = textView;
		}

		TextView textView;
		CheckBox cb;
	}
}
