package com.variance.mimiprotect.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.contacts.selection.ContactGeneralSelectionManager;
import com.variance.mimiprotect.contacts.selection.OnContactSelectionComplete;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.contacts.task.QREncoderTask;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class ProfileActivity extends MimiProtectActivity implements
		HttpRequestTaskListener<Void, String> {
	private User currentUser;
	private String internationalDialingCode;
	private List<EditText> phoneNumbers;
	private List<EditText> emailAddresses;
	{
		phoneNumbers = new ArrayList<EditText>();
		emailAddresses = new ArrayList<EditText>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_profile);
		try {
			String dialingCode = getInternationalDialingCode();
			if (dialingCode != null) {
				((TextView) findViewById(R.id.profilePhoneNumber))
						.setHint(dialingCode);
			}
			currentUser = MimiProtectGeneralManager.getCurrentUser();
			load();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onTaskStarted() {
	}

	public void onTaskCompleted(String result) {
		currentUser = DataParser.getUserFrom(result);
		if (currentUser != null) {
			Log.i("organization:", currentUser.getOrganization() + "");
			Log.i("title:", currentUser.getTitle() + "");
			show();
		}
	}

	public String doTask(Void... params) {
		if (!Settings.isDebugging()) {
			internationalDialingCode = getInternationalDialingCode();
			return HttpRequestManager.doRequest(
					Settings.getProfileRequestUrl(),
					Settings.getProfileRequestLoadParameter());
		}
		return null;
	}

	private void load() {
		if (currentUser == null) {
			new HttpRequestTask<Void, Void, String>(this, "Loading profile...",
					this).execute();
		} else {
			show();
		}
	}

	private String getInternationalDialingCode() {
		return Settings.getCurrentSIMCountryCode(this);
	}

	private void show() {
		if (currentUser != null) {
			/**
			 * Set the user country code to phone number if it exists
			 */
			internationalDialingCode = getInternationalDialingCode();
			String phone = currentUser.getFirstPhone();
			if (!Utils.isNullStringOrEmpty(phone) && phone.length() <= 10
					&& !Utils.isNullStringOrEmpty(internationalDialingCode)) {
				if (phone.startsWith("0")) {
					phone = phone.substring(1);
				}
				phone = internationalDialingCode + phone;
			}
			if (!Utils.isNullStringOrEmpty(phone)) {
				((TextView) findViewById(R.id.profilePhoneNumber))
						.setText(phone);
			}
			// set surname and other names
			getEditText(R.id.txtFirstName).setText(currentUser.getSurname());
			getEditText(R.id.txtLastName).setText(currentUser.getOtherNames());
			// set organization and title
			getEditText(R.id.txtOrganization).setText(
					currentUser.getOrganization());
			getEditText(R.id.txtTitle).setText(currentUser.getTitle());
			if (currentUser.getEmails().size() > 0) {
				LinearLayout moreEmailAddresses = (LinearLayout) findViewById(R.id.moreEmailAddresses);
				moreEmailAddresses.removeAllViews();
				emailAddresses.clear();
				for (String email : currentUser.getEmails()) {
					LinearLayout layout = getView(moreEmailAddresses);
					EditText txt = (EditText) layout.findViewById(R.id.txtView);
					RadioButton rb = (RadioButton) layout
							.findViewById(R.id.rbDefaultOption);
					rb.setChecked(email.equals(currentUser.getEmail()));
					addEmailAddressRadioButtonListener(rb, currentUser
							.getEmails().indexOf(email));
					txt.setText(email);
					txt.setBackgroundDrawable(null);
					moreEmailAddresses.addView(layout);
					emailAddresses.add(txt);
					addEmailDeleteButton(layout, moreEmailAddresses);
				}
			}
			if (currentUser.getPhones().size() > 0) {
				LinearLayout morePhoneNumbers = (LinearLayout) findViewById(R.id.morePhoneNumbers);
				morePhoneNumbers.removeAllViews();
				phoneNumbers.clear();
				Log.i("first phone:", currentUser.getFirstPhone() + "");
				for (String phone_ : currentUser.getPhones()) {
					LinearLayout layout = getView(morePhoneNumbers);
					RadioButton rb = (RadioButton) layout
							.findViewById(R.id.rbDefaultOption);
					addRadioButtonListener(rb,
							currentUser.getPhones().indexOf(phone_));
					rb.setChecked(phone_.equals(currentUser.getFirstPhone()));
					if (!Utils.isNullStringOrEmpty(phone_)
							&& phone_.length() <= 10
							&& !Utils
									.isNullStringOrEmpty(internationalDialingCode)) {
						if (phone_.startsWith("0")) {
							phone_ = phone_.substring(1);
						}
						phone_ = internationalDialingCode + phone_;
					}
					EditText txt = (EditText) layout.findViewById(R.id.txtView);
					txt.setText(phone_);
					addEditTextListener(txt, rb);
					txt.setBackgroundDrawable(null);
					txt.setInputType(InputType.TYPE_CLASS_PHONE);
					morePhoneNumbers.addView(layout);
					phoneNumbers.add(txt);
					addPhoneDeleteButton(layout, morePhoneNumbers);
				}
			}
		}
	}

	private static final Set<RadioButton> radioButtons = new HashSet<RadioButton>();
	private static final Set<RadioButton> emailAddressRadioButtons = new HashSet<RadioButton>();

	private void addEditTextListener(final EditText txt, final RadioButton rb) {
		txt.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				// we simply add all of it
				if (rb.isChecked()) {
					// then we are working with the default button.
					String msisdn = s.toString();
					currentUser.setMsisdn(msisdn);
				}
			}
		});
	}

	private void addEmailAddressRadioButtonListener(final RadioButton rb,
			final int position) {
		emailAddressRadioButtons.add(rb);
		rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					for (RadioButton r : emailAddressRadioButtons) {
						if (r != rb) {
							r.setChecked(false);
						}
					}
					String email = currentUser.getEmails().get(position);
					currentUser.setEmail(email);
				}
			}
		});
	}

	private void addRadioButtonListener(final RadioButton rb, final int position) {
		radioButtons.add(rb);
		rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					for (RadioButton r : radioButtons) {
						if (r != rb) {
							r.setChecked(false);
						}
					}
					String msisdn = currentUser.getPhones().get(position);
					currentUser.setMsisdn(msisdn);
					if (!Utils.isNullStringOrEmpty(msisdn)) {
						((TextView) findViewById(R.id.profilePhoneNumber))
								.setText(msisdn);
					}
				}
			}
		});
	}

	private LinearLayout getView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.usercontact_deleteoption, parent, false);
		return layout;
	}

	private void addEmailDeleteButton(final LinearLayout layout,
			final LinearLayout parent) {
		final EditText txt = (EditText) layout.findViewById(R.id.txtView);
		final Button btn = (Button) layout.findViewById(R.id.btnDeleteOption);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ProfileActivity.this);
				builder.setTitle("Delete email address");
				builder.setMessage("Are you sure you want to delete the following email address: "
						+ txt.getText().toString());
				builder.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								int index = emailAddresses.indexOf(txt);
								if (index > -1) {
									currentUser.getEmails().remove(index);
									parent.removeView(layout);
									emailAddresses.remove(index);
								}
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.show();
			}
		});
	}

	private void addPhoneDeleteButton(final LinearLayout layout,
			final LinearLayout parent) {
		final EditText txt = (EditText) layout.findViewById(R.id.txtView);
		final Button btn = (Button) layout.findViewById(R.id.btnDeleteOption);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ProfileActivity.this);
				builder.setTitle("Delete phone number");
				builder.setMessage("Are you sure you want to delete the following phone number: "
						+ txt.getText().toString());
				builder.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								int index = phoneNumbers.indexOf(txt);
								if (index > -1) {
									currentUser.getPhones().remove(index);
									parent.removeView(layout);
									phoneNumbers.remove(index);
								}
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.show();
			}
		});
	}

	public void viewMoreEmailAddresses(View view) {
		if (currentUser != null && currentUser.getEmails().size() > 0) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.moreEmailAddresses);
			int state = layout.getVisibility();
			if (state == View.GONE) {
				layout.setVisibility(View.VISIBLE);
				view.setBackgroundResource(R.drawable.mimi_connect_less);
			} else {
				layout.setVisibility(View.GONE);
				view.setBackgroundResource(R.drawable.mimi_connect_more);
			}
		}
	}

	public void viewMorePhoneNumbers(View view) {
		if (currentUser != null && currentUser.getPhones().size() > 0) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.morePhoneNumbers);
			int state = layout.getVisibility();
			if (state == View.GONE) {
				layout.setVisibility(View.VISIBLE);
				view.setBackgroundResource(R.drawable.mimi_connect_less);
			} else {
				layout.setVisibility(View.GONE);
				view.setBackgroundResource(R.drawable.mimi_connect_more);
			}
		}
	}

	private void doEdit() {
		// set current first/last names
		String surname = getEditText(R.id.txtFirstName).getText().toString();
		String otherNames = getEditText(R.id.txtLastName).getText().toString();
		currentUser.setSurname(surname);
		currentUser.setOtherNames(otherNames);
		// set organization and title
		String org = getEditText(R.id.txtOrganization).getText().toString();
		String title = getEditText(R.id.txtTitle).getText().toString();
		currentUser.setOrganization(org);
		currentUser.setTitle(title);
		for (EditText txt : phoneNumbers) {
			int idx = phoneNumbers.indexOf(txt);
			String newPhoneNumber = txt.getText().toString();
			if (Utils.isNullStringOrEmpty(newPhoneNumber)) {
				currentUser.getPhones().remove(idx);
			} else {
				currentUser.addPhone(newPhoneNumber, idx);
			}
		}
		for (EditText txt : emailAddresses) {
			int idx = emailAddresses.indexOf(txt);
			String newEmailAddress = txt.getText().toString();
			if (Utils.isNullStringOrEmpty(newEmailAddress)) {
				currentUser.getEmails().remove(idx);
			} else {
				currentUser.addEmail(newEmailAddress, idx);
			}
		}
	}

	private EditText getEditText(int id) {
		return (EditText) findViewById(id);
	}

	public void handleQRCodeGeneration(View view) {
		new QREncoderTask(this, null).execute();
	}

	public void handleSaveProfile(View view) {
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				if (currentUser != null) {
					doEdit();
					String result = HttpRequestManager.doRequest(Settings
							.getProfileRequestUrl(), Settings
							.getProfileRequestEditParameter(currentUser));
					return result;
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				PersonalPhonebookActivity.endProgress();
				show();
				Toast.makeText(ProfileActivity.this, result + "",
						Toast.LENGTH_SHORT).show();
				if (result != null && result.toLowerCase().contains("success")) {
					if (MimiProtectGeneralManager.hasAccessibility()) {
						MimiProtectGeneralManager.getUserSetting().setPassword(
								currentUser.getPassword());
						MimiProtectGeneralManager.updateUserSetting();
						MimiProtectGeneralManager.updateUserInformation();
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Saving...",
						ProfileActivity.this, this);
			}
		}.execute(new String[] {});
	}

	public void handleShareContactAsQrCode(View view) {
		handleQRCodeGeneration(view);
	}

	public void showUserSettings(View view) {
		PhonebookActivity.startGeneralActivity(this, "Settings",
				UserSettingActivity.class, R.layout.usercontact_tabview, false);
	}

	public void handleSendProfileContact(View view) {
		OnContactSelectionComplete ocsc = new OnContactSelectionComplete() {

			public void contactSelected(final List<Contact> contactsToSendTo) {
				Log.i("Selected Contacts", contactsToSendTo.toString());
				// send the data to the server for sharing or sending to
				// contacts
				HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

					public void onTaskStarted() {
					}

					public void onTaskCompleted(HttpResponseData result) {
						Log.i("Profile Sent: ",
								(result != null) ? result.toString()
										: "result is null");
						if (result != null) {
							Log.i("Profile Sent: ", result.toString());
							Toast.makeText(ProfileActivity.this,
									result.toString(), Toast.LENGTH_LONG)
									.show();
						}
					}

					public HttpResponseData doTask(Void... params) {
						return HttpRequestManager
								.doRequestWithResponseData(
										Settings.getContactSharedUrl(),
										Settings.makeSendOrShareProfileContactParameter(contactsToSendTo));
					}
				};
				new HttpRequestTask<Void, Void, HttpResponseData>(listener,
						"Sending/Sharing Contacts. Please wait...",
						ProfileActivity.this).execute();
			}
		};
		ContactGeneralSelectionManager cgsm = new ContactGeneralSelectionManager(
				true, this, "Send Profile Info", "Send", ocsc);
		SearchParameter sp = new SearchParameter();
		sp.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
		sp.setMaxResult(PersonalPhonebookActivity.getMaximumListRows(this) * 4);
		PersonalPhonebookActivity.updateSearchParameterForPersonalContacts(sp);
		cgsm.execute(new SearchParameter[] { sp });
	}

	public void handleCloseProfile(View view) {
		finish();
	}

	public void handleAddPhoneNumber(View view) {
		final Dialog d = new Dialog(this);
		d.setTitle("Add Phone Number");
		d.setContentView(R.layout.usercontact_adddetail);
		Button btnAdd = (Button) d.findViewById(R.id.btnAddDetail);
		((EditText) d.findViewById(R.id.newDetail)).setHint("Add Phone Number");
		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String phone_ = ((EditText) d.findViewById(R.id.newDetail))
						.getText().toString();
				if (phone_ != null && !"".equals(phone_)) {
					boolean isDefault = ((CheckBox) d
							.findViewById(R.id.newDetailDefault)).isChecked();
					String internationalDialingCode = getInternationalDialingCode();
					if (!Utils.isNullStringOrEmpty(phone_)
							&& phone_.length() <= 10
							&& !Utils
									.isNullStringOrEmpty(internationalDialingCode)) {
						if (phone_.startsWith("0")) {
							phone_ = phone_.substring(1);
						}
						phone_ = internationalDialingCode + phone_;
					}
					if (isDefault) {
						currentUser.setMsisdn(phone_);
					}
					currentUser.addPhone(phone_);
					show();
					d.dismiss();
				}
			}
		});
		d.show();
	}

	public void handleAddEmailAddress(View view) {
		final Dialog d = new Dialog(this);
		d.setTitle("Add Email Address");
		d.setContentView(R.layout.usercontact_adddetail);
		EditText edTxt = (EditText) d.findViewById(R.id.newDetail);
		edTxt.setHint("Add Email Address");
		edTxt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		Button btnAdd = (Button) d.findViewById(R.id.btnAddDetail);
		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String txt = ((EditText) d.findViewById(R.id.newDetail))
						.getText().toString();
				if (txt != null && !"".equals(txt)) {
					boolean isDefault = ((CheckBox) d
							.findViewById(R.id.newDetailDefault)).isChecked();
					if (isDefault) {
						currentUser.setEmail(txt);
					}
					currentUser.addEmail(txt);
					show();
					d.dismiss();
				}
			}
		});
		d.show();
	}

	public void handleChangePassword(View view) {
		final Dialog d = new Dialog(this);
		d.setContentView(R.layout.mimi_connect_editpassword);
		d.setTitle("Edit Password");
		Button btnSave = (Button) d.findViewById(R.id.btnEditPassword);
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String opass = ((EditText) d.findViewById(R.id.oldPassword))
						.getText().toString();
				String pass = ((EditText) d.findViewById(R.id.newPassword))
						.getText().toString();
				String cpass = ((EditText) d
						.findViewById(R.id.confirmNewPassword)).getText()
						.toString();
				if (!(currentUser.getPassword() + "").equals(opass + "")) {
					Toast.makeText(ProfileActivity.this,
							"Sorry! Invalid Password.", Toast.LENGTH_LONG)
							.show();
					return;
				}
				if (pass != null && pass.equals(cpass)) {
					currentUser.setPassword(pass);
					d.dismiss();
				} else {
					Toast.makeText(ProfileActivity.this,
							"Passwords do not match", Toast.LENGTH_LONG).show();
				}
			}
		});
		d.show();
	}

	private void addViewMyLivelinkOption(Menu menu) {
		MenuItem addViewMyLivelinkOption = menu.add("My Livelinks");
		addViewMyLivelinkOption.setIcon(R.drawable.mimi_connect_livelink);
		addViewMyLivelinkOption
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						if (Settings.isLoggedIn()) {
							PhonebookActivity.startGeneralActivity(
									ProfileActivity.this, "My Livelinks",
									MyLivelinkActivity.class,
									R.layout.usercontact_tabview, false);
						} else {
							Toast.makeText(
									ProfileActivity.this,
									"You must be logged in to view your livelinks",
									Toast.LENGTH_LONG).show();
						}
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addViewMyLivelinkOption(menu);
		return super.onCreateOptionsMenu(menu);
	}

}
