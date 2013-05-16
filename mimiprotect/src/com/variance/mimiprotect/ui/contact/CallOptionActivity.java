package com.variance.mimiprotect.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;

public class CallOptionActivity extends MimiProtectActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_callortextoptions);
		showCallOptions();
	}

	private void showCallOptions() {
		String[] phones = (String[]) getIntent().getSerializableExtra(
				"phones_53267784282824");
		TextView defaultView = (TextView) findViewById(R.id.default_call_number_view);
		defaultView.setText(phones[0]);
		addOnCallListener(defaultView, phones[0]);
		LinearLayout morePhoneNumbersView = (LinearLayout) findViewById(R.id.viewMorePhoneNumbersPanel);
		morePhoneNumbersView.removeAllViews();
		if (phones.length > 1) {
			for (int i = 1; i < phones.length; i++) {
				LinearLayout option = getView(morePhoneNumbersView);
				TextView view = (TextView) option
						.findViewById(R.id.call_number_view);
				view.setText(phones[i]);
				addOnCallListener(view, phones[i]);
				morePhoneNumbersView.addView(option);
			}
			final Button btnShowMorePhoneNumbers = (Button) findViewById(R.id.btnShowMorePhoneNumbers);
			btnShowMorePhoneNumbers.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					viewMorePhoneNumbers(btnShowMorePhoneNumbers);
				}
			});
		}
	}

	private LinearLayout getView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.usercontact_calloption, parent, true);
		return layout;
	}

	public void viewMorePhoneNumbers(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.viewMorePhoneNumbersPanel);
		int state = layout.getVisibility();
		if (state == View.GONE) {
			layout.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.mimi_connect_less);
		} else {
			layout.setVisibility(View.GONE);
			view.setBackgroundResource(R.drawable.mimi_connect_more);
		}
	}

	private void addOnCallListener(TextView view, final String phone) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = phone;
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + phone));
				startActivity(callIntent);
			}
		});
	}

}
