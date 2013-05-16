package com.variance.mimiprotect.contacts.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.Pair;

public class ContactDetailsListAdapter extends ArrayAdapter<String> {
	private Context context;
	private ArrayList<Pair<String, String>> contactdetails;
	private Map<String, String> contactData; // for editing purposes.
	private boolean businessContact;

	public ContactDetailsListAdapter(Context context, int textViewResourceId,
			ArrayList<Pair<String, String>> contactdetails,
			boolean businessContact) {
		super(context, textViewResourceId, getDetails(contactdetails));
		this.context = context;
		this.contactdetails = contactdetails;
		contactData = new HashMap<String, String>();
		this.businessContact = businessContact;
	}

	public ContactDetailsListAdapter(Context context, int textViewResourceId,
			ArrayList<Pair<String, String>> contactdetails) {
		super(context, textViewResourceId, getDetails(contactdetails));
		this.context = context;
		this.contactdetails = contactdetails;
		contactData = new HashMap<String, String>();
	}

	private static String[] getDetails(
			ArrayList<Pair<String, String>> contactdetails) {
		String[] details = new String[contactdetails.size()];
		int i = 0;
		for (Pair<String, String> p : contactdetails) {
			details[i++] = p.getSecond();
		}
		return details;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.usercontact_singlecontactdetailview, parent, false);
		TextView detailNameTxt = (TextView) rowView
				.findViewById(R.id.contactDetailName);
		detailNameTxt.setText(contactdetails.get(position).getFirst());
		TextView detailValueTxt_business = (TextView) rowView
				.findViewById(R.id.contactDetailValue_BusinessContact);
		EditText detailValueTxt_personal = (EditText) rowView
				.findViewById(R.id.contactDetailValue_PersonalContact);
		if (!businessContact) {
			detailValueTxt_business.setVisibility(View.GONE);
			detailValueTxt_personal.setText(contactdetails.get(position)
					.getSecond());
			detailValueTxt_personal.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					contactData.put(contactdetails.get(position).getFirst(),
							s.toString());
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					contactData.put(contactdetails.get(position).getFirst(),
							s.toString());
				}
			});
			if (contactdetails.get(position).getFirst()
					.startsWith(ContactDetailsViewManager.PHONE_ID)) {
				detailValueTxt_personal
						.setInputType(InputType.TYPE_CLASS_PHONE);
			}
		} else {
			detailValueTxt_personal.setVisibility(View.GONE);
			detailValueTxt_business.setText(contactdetails.get(position)
					.getSecond());
		}
		return rowView;
	}

	public String getValue(String id) {
		return contactData.get(id);
	}
}
