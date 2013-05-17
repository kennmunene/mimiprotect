package com.variance.mimiprotect.contacts.business;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class NewBusinessActivity extends MimiProtectActivity {

	private BusinessInformation businessInformation;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscontact_newbusiness);
		businessInformation = new BusinessInformation();
		init();
	}

	private void init() {
		Button btnAddPhone = (Button) findViewById(R.id.btnAddBusinessContactPhoneNumber);
		btnAddPhone.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String phone = ((EditText) findViewById(R.id.businessContactPhoneNumber))
						.getText().toString();
				((EditText) findViewById(R.id.businessContactPhoneNumber))
						.setText("");
				businessInformation.addPhone(phone);
			}
		});
		Button btnAddEmail = (Button) findViewById(R.id.btnAddBusinessContactEmail);
		btnAddEmail.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String email = ((EditText) findViewById(R.id.businessContactEmail))
						.getText().toString();
				((EditText) findViewById(R.id.businessContactEmail))
						.setText("");
				businessInformation.addEmail(email);
			}
		});
	}

	public void handleCancelNewBusiness(View view) {
		finish();
	}

	private boolean isNotNullOrEmpty(String... params) {
		for (String str : params) {
			if (Utils.isNullStringOrEmpty(str))
				return false;
		}
		return true;
	}

	public void addOfficePhonebook(View view) {
		String name = ((EditText) findViewById(R.id.businessContactName))
				.getText().toString();
		String country = ((EditText) findViewById(R.id.businessContactCountry))
				.getText().toString();
		String postcode = ((EditText) findViewById(R.id.businessContactPostcode))
				.getText().toString();
		String website = ((EditText) findViewById(R.id.businessContactWebsite))
				.getText().toString();
		String address = ((EditText) findViewById(R.id.businessContactAddress))
				.getText().toString();
		String phone = ((EditText) findViewById(R.id.businessContactPhoneNumber))
				.getText().toString();
		String email = ((EditText) findViewById(R.id.businessContactEmail))
				.getText().toString();
		businessInformation.setBusinessName(name);
		businessInformation.setAddress(address);
		businessInformation.setCountry(country);
		businessInformation.setPostcode(postcode);
		businessInformation.setWebsite(website);
		businessInformation.addEmail(email);
		businessInformation.addPhone(phone);
		if (!isNotNullOrEmpty(name, country, address)
				|| businessInformation.getPhones().isEmpty()
				|| businessInformation.getEmails().isEmpty()) {
			Toast.makeText(this,
					"You must specify all fields marked with asterisk",
					Toast.LENGTH_LONG).show();
			return;
		}
		new AsyncTask<String, Void, HttpResponseData>() {
			Activity activity = NewBusinessActivity.this;

			@Override
			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(activity, result.getMessage(),
							Toast.LENGTH_SHORT).show();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						finish();
						Intent intent = new Intent(activity,
								GeneralTabActivity.class);
						Intent callingintent = activity.getIntent();
						if (callingintent != null) {
							intent.putExtras(callingintent);
						}
						intent.putExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
								BusinessContactActivity.class.getName());

						intent.putExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
								businessInformation.getBusinessName());
						activity.startActivity(intent);
						// at the same time, we initialize the office phonebook.
						DashBoardActivity.DASH_BOARD_ACTIVITY
								.onOfficePhonebookCreated();
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Saving...",
						NewBusinessActivity.this);
			}

			@Override
			protected HttpResponseData doInBackground(String... params) {
				return HttpRequestManager
						.doRequestWithResponseData(
								Settings.getBusinessContactUrl(),
								Settings.makeBusinessContactNewBusinessParameter(businessInformation));
			}
		}.execute(new String[] {});
	}

	public void cancel(View view) {
		finish();
	}
}
