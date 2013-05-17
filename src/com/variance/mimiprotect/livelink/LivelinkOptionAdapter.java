package com.variance.mimiprotect.livelink;

import java.util.ArrayList;
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

public class LivelinkOptionAdapter extends ArrayAdapter<Contact> {
	private List<Contact> contacts;
	private List<Contact> selectedContacts;
	private Map<CheckBox, View> currentRadioButtons;
	LayoutInflater inflater;

	public LivelinkOptionAdapter(Context context, int textViewResourceId,
			List<Contact> contacts) {
		super(context, textViewResourceId, contacts);
		this.contacts = contacts;
		this.selectedContacts = new ArrayList<Contact>();
		this.currentRadioButtons = new HashMap<CheckBox, View>();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView txtView;
		CheckBox selectBtn;
		convertView = inflater.inflate(
				R.layout.usercontact_livelinkoption_singleview, parent, false);
		txtView = (TextView) convertView
				.findViewById(R.id.usercontact_livelinkoption_contactTextView);
		selectBtn = (CheckBox) convertView
				.findViewById(R.id.usercontact_livelinkoption_selectBtn);
		final Contact c = this.contacts.get(position);
		txtView.setText(c.getName());
		currentRadioButtons.put(selectBtn, convertView);
		selectBtn.setChecked(selectedContacts.contains(c));
		if (selectedContacts.contains(c)) {
			convertView
					.setBackgroundResource(R.drawable.mimi_connect_two_color_gradient_background);
		} else {
			convertView
					.setBackgroundResource(R.drawable.mimi_connect_two_color_linear_gradient_no_corner);
		}
		selectBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!selectedContacts.contains(c)) {
						selectedContacts.add(c);
						// convertView.setBackgroundColor(context.getResources()
						// .getColor(R.color.grey0));
					}
				} else {
					selectedContacts.remove(c);
					// convertView.setBackgroundColor(context.getResources().getColor(
					// R.color.white));
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
				boolean alreadyAdded = selectedContacts.contains(c);
				if (!alreadyAdded) {
					selectedContacts.add(c);
					view.setBackgroundResource(R.drawable.mimi_connect_two_color_gradient_background);
				} else {
					selectedContacts.remove(c);
					view.setBackgroundResource(R.drawable.mimi_connect_two_color_linear_gradient_no_corner);
				}
				checkBox.setChecked(!alreadyAdded);
			}
		});
	}

	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}

	public void selectAllContacts() {
		this.selectedContacts.clear();
		this.selectedContacts.addAll(this.contacts);
		for (CheckBox b : currentRadioButtons.keySet()) {
			b.setChecked(true);
			currentRadioButtons.get(b).setBackgroundResource(
					R.drawable.mimi_connect_two_color_gradient_background);
		}
	}

	public void deselectAllContacts() {
		this.selectedContacts.clear();
		for (CheckBox b : currentRadioButtons.keySet()) {
			b.setChecked(false);
			currentRadioButtons
					.get(b)
					.setBackgroundResource(
							R.drawable.mimi_connect_two_color_linear_gradient_no_corner);
		}
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
