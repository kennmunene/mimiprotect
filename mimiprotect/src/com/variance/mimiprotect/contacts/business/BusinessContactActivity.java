package com.variance.mimiprotect.contacts.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.UserInfo;
import com.variance.mimiprotect.contacts.business.settings.BusinessContactConstants;
import com.variance.mimiprotect.contacts.business.task.BusinessContactSearchTask;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseConstants;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.ui.contact.NewContactActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class BusinessContactActivity extends PhonebookActivity {

	private String searchName;
	private EditText searchTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscontact_search);
		addSoftKeyEnterActionOnSearch();
		initParameters();
		doSearch();
	}

	@Override
	protected void onResume() {
		super.onResume();
		doBackgroundSearch();
	}

	public static int maxBusinessRecords() {
		HttpResponseData recordResults = HttpRequestManager
				.doRequestWithResponseData(Settings.getBusinessContactUrl(),
						Settings.makeCountBusinessContactsParameter());
		if (recordResults != null
				&& recordResults.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String num = recordResults.getMessage();
			int record = Integer.parseInt(num.trim());
			return record;
		}
		return -1;
	}

	public static int maxBusinessRecordsForSearch(String searchTerm) {
		HttpResponseData recordResults = HttpRequestManager
				.doRequestWithResponseData(
						Settings.getSearchContactUrl(),
						Settings.makeCountBusinessContactsForSearchParameter(searchTerm));
		if (recordResults != null
				&& recordResults.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String num = recordResults.getMessage();
			int record = Integer.parseInt(num.trim());
			Log.i("Max Records: ", num);
			return record;
		}
		return -1;
	}

	public void updateSearchParameter() {
		if (searchParameter.getSearchTerm() == null
				|| "".equals(searchParameter.getSearchTerm().trim())) {
			searchParameter
					.setSearchTerm(BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM);
		}
		if (searchParameter.getSearchTerm() != null
				&& !searchParameter
						.getSearchTerm()
						.equals(BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM)) {
			this.searchParameter
					.setMaxRecords(maxBusinessRecordsForSearch(searchParameter
							.getSearchTerm()));
		} else {
			this.searchParameter.setMaxRecords(maxBusinessRecords());
		}
	}

	private void initParameters() {
		searchParameter = new SearchParameter(
				BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM,
				0, PersonalPhonebookActivity.getMaximumListRows(
						BusinessContactActivity.this, 30));
	}

	private void addSoftKeyEnterActionOnSearch() {
		searchTxt = (EditText) findViewById(R.id.txtBusinessSearch);
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						search(searchTxt);
						return true;
					default:
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DEL:
						String txt = searchTxt.getText().toString();
						if ("".equals(txt.trim())) {
							searchParameter
									.setSearchTerm(BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM);
							searchParameter.setCurrentPage(0);
							doBackgroundSearch();
						}
						return true;
					}
				}
				return false;
			}
		});
	}

	public void search(EditText eTxt) {
		String search = eTxt.getText().toString();
		if (search != null) {
			searchName = search.trim();
			if (searchParameter != null) {
				searchParameter.setSearchTerm(searchName);
				searchParameter.setCurrentPage(0);
			} else {
				searchParameter = new SearchParameter(searchName, 0,
						PersonalPhonebookActivity.getMaximumListRows(this, 30));
			}
			doSearch();
		}
	}

	public void search() {
		String search = searchTxt.getText().toString();
		if (search != null) {
			searchName = search.trim();
			if (searchParameter != null) {
				searchParameter.setSearchTerm(searchName);
				searchParameter.setCurrentPage(0);
			} else {
				searchParameter = new SearchParameter(searchName, 0,
						PersonalPhonebookActivity.getMaximumListRows(this, 30));
			}
			doSearch();
		}
	}

	protected void doBackgroundSearch() {
		Log.e("doBackgroundSearch:", "doBackgroundSearch");
		super.doBackgroundSearch();
		Log.e("isLoadedFromCache: ", isLoadedFromCache() + "");
		if (!isLoadedFromCache()) {
			new BusinessContactSearchTask(BusinessContactActivity.this, true)
					.execute(new SearchParameter[] { searchParameter });
		}
	}

	@Override
	protected boolean doLoadFromCache() {
		boolean loadFromCache = super.doLoadFromCache()
				|| searchParameter
						.getSearchTerm()
						.equals(BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM);
		Log.i("Loading from cache", loadFromCache + "");
		return loadFromCache;
	}

	protected void doSearch() {
		super.doSearch();
		if (!isLoadedFromCache()) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					new BusinessContactSearchTask(BusinessContactActivity.this)
							.execute(new SearchParameter[] { searchParameter });
				}
			});
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	public void addBusinessContact(View view) {
		switch (view.getId()) {
		case R.id.btnAddBusinessContact:
			addBusinessContact();
			break;
		}
	}

	private void addBusinessContact() {
		final Dialog newBusinessContactOptionDlg = new Dialog(this);
		newBusinessContactOptionDlg.setTitle("New Business Contact Option");
		newBusinessContactOptionDlg
				.setContentView(R.layout.businesscontact_newcontactoptions);
		View titleView = newBusinessContactOptionDlg.getWindow().findViewById(
				android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView
						.setBackgroundResource(R.drawable.mimi_connect_background);
			}
		}
		LinearLayout selectFromPersonalContacts = (LinearLayout) newBusinessContactOptionDlg
				.findViewById(R.id.txtSelectFromPersonalPhonebook);
		selectFromPersonalContacts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (Settings.isLoggedIn()) {
					Map<String, Boolean> extras = new HashMap<String, Boolean>();
					extras.put(
							MimiProtectIntentConstants.MIMI_PROTECT_IS_BUSINESS_CONTACT,
							true);
					PhonebookActivity
							.startGeneralActivity(
									BusinessContactActivity.this,
									DashBoardActivity.DASH_BOARD_ACTIVITY
											.getBusinessName(),
									SelectBusinessContactFromPersonalContactActivity.class,
									R.layout.usercontact_tabview, false);
				} else {
					Toast.makeText(
							BusinessContactActivity.this,
							"Sorry! You must be logged in to add a business contact from personal contacts.",
							Toast.LENGTH_LONG).show();
				}
				newBusinessContactOptionDlg.dismiss();
			}
		});
		LinearLayout scanQrCode = (LinearLayout) newBusinessContactOptionDlg
				.findViewById(R.id.txtScanQrCode);
		if (scanQrCode != null) {
			scanQrCode.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Map<String, Boolean> extras = new HashMap<String, Boolean>();
					extras.put(
							MimiProtectIntentConstants.MIMI_PROTECT_NEWCONTACT_FROM_QR,
							true);
					extras.put(
							MimiProtectIntentConstants.MIMI_PROTECT_IS_BUSINESS_CONTACT,
							true);
					PhonebookActivity.startGeneralActivity(
							BusinessContactActivity.this,
							DashBoardActivity.DASH_BOARD_ACTIVITY
									.getBusinessName(),
							NewContactActivity.class,
							R.layout.usercontact_tabview, false, extras);
				}
			});
		}
		LinearLayout txtEnterUserInformation = (LinearLayout) newBusinessContactOptionDlg
				.findViewById(R.id.txtEnterUserInformation);
		txtEnterUserInformation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Map<String, Boolean> extras = new HashMap<String, Boolean>();
				extras.put(
						MimiProtectIntentConstants.MIMI_PROTECT_IS_BUSINESS_CONTACT,
						true);
				PhonebookActivity
						.startGeneralActivity(BusinessContactActivity.this,
								DashBoardActivity.DASH_BOARD_ACTIVITY
										.getBusinessName(),
								NewContactActivity.class,
								R.layout.usercontact_tabview, false, extras);
			}
		});
		LinearLayout livelinkRequest = (LinearLayout) newBusinessContactOptionDlg
				.findViewById(R.id.txtLivelinkRequest);
		if (livelinkRequest != null) {
			livelinkRequest.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					performLivelinkRequest(null);
				}
			});
		}
		newBusinessContactOptionDlg.show();
	}

	public void performLivelinkRequest(View view) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("MiMiProtect Livelink");
		alert.setMessage("Please enter the username for the user:");
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Send Request",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String userName = input.getText().toString();
						if (userName != null && !"".equals(userName)) {
							doLivelinkRequest(userName);
						} else {
							Toast.makeText(BusinessContactActivity.this,
									"Invalid user name!", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
		alert.show();
	}

	private void doLivelinkRequest(String username) {
		PersonalPhonebookActivity.showProgress("Please wait...", this);
		String result = HttpRequestManager.doRequest(Settings.getLivelinkUrl(),
				Settings.makeLivelinkRequestParameter(username));
		PersonalPhonebookActivity.endProgress();
		if (result != null) {
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		}
	}

	private boolean canAddCompanyBusinessAccessForUsers() {
		HttpResponseData data = HttpRequestManager.doRequestWithResponseData(
				Settings.getBusinessContactUrl(),
				Settings.makeUserHasBusinessContactParameters());
		String isAdmin = data != null ? data
				.getExtra(HttpResponseConstants.HTTP_RESPONSE_BUSINESS_IS_ADMINISTRATOR)
				: null;
		return isAdmin != null ? Boolean.parseBoolean(isAdmin.trim()) : false;
	}

	private void addCompanyUserOptions(Menu menu) {
		if (!canAddCompanyBusinessAccessForUsers()) {
			return;
		}
		MenuItem businessContactMenu = menu.add("Add Users");
		businessContactMenu.setIcon(R.drawable.mimi_connect_contactphoto);
		businessContactMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						AlertDialog.Builder alert = new AlertDialog.Builder(
								BusinessContactActivity.this);
						alert.setTitle("Add Company Users");
						alert.setMessage("Add the mimiprotect username for the Company "
								+ "Employee who can access the Business Contacts.");
						// Set an EditText view to get user input
						final EditText input = new EditText(
								BusinessContactActivity.this);
						alert.setView(input);
						alert.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										final String value = input.getText()
												.toString();
										new AsyncTask<String, Void, HttpResponseData>() {

											@Override
											protected void onPostExecute(
													HttpResponseData result) {
												PersonalPhonebookActivity
														.endProgress();
												String msg = result != null ? result
														.toString() : "Error";
												Toast.makeText(
														BusinessContactActivity.this,
														msg, Toast.LENGTH_LONG)
														.show();
											}

											@Override
											protected void onPreExecute() {
												PersonalPhonebookActivity
														.showProgress(
																"Adding user...",
																BusinessContactActivity.this);
											}

											@Override
											protected HttpResponseData doInBackground(
													String... params) {
												return HttpRequestManager
														.doRequestWithResponseData(
																Settings.getBusinessContactUrl(),
																Settings.makeAddBusinessContactUser(value));
											}

										}.execute(new String[] {});
									}
								});
						alert.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Canceled.
									}
								});
						alert.show();
						return false;
					}

				});
	}

	private void addRemoveUsersOptions(Menu menu) {
		if (!canAddCompanyBusinessAccessForUsers()) {
			return;
		}
		MenuItem removeUsers = menu.add("Remove Users");
		removeUsers.setIcon(R.drawable.mimi_connect_removeuser);
		removeUsers.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				new AsyncTask<String, Void, HttpResponseData>() {
					LayoutInflater inflater = null;

					@Override
					protected void onPostExecute(HttpResponseData result) {
						PersonalPhonebookActivity.endProgress();
						if (result != null
								&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							final ArrayList<UserInfo> cs = DataParser
									.getUserInformation(result.getMessage());
							final ArrayList<UserInfo> selected = new ArrayList<UserInfo>();
							if (cs != null && !cs.isEmpty()) {
								final Dialog showSelectUsersDlg = new Dialog(
										BusinessContactActivity.this);
								showSelectUsersDlg
										.setTitle("Select Users To Remove");
								showSelectUsersDlg
										.setContentView(R.layout.businesscontact_selectbusinesscontactusers);
								ArrayAdapter<UserInfo> adapter = new ArrayAdapter<UserInfo>(
										BusinessContactActivity.this,
										R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
										cs) {
									@Override
									public View getView(int position,
											View convertView, ViewGroup parent) {
										if (inflater == null) {
											inflater = (LayoutInflater) BusinessContactActivity.this
													.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										}

										View rowView = inflater
												.inflate(
														R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
														parent, false);

										final CheckBox cb = (CheckBox) rowView
												.findViewById(R.id.cbSelectPersonalContact);
										TextView txtView = (TextView) rowView
												.findViewById(R.id.txtSelectPersonalContact);
										final UserInfo c = cs.get(position);
										txtView.setText(c.getActualName());
										cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

											public void onCheckedChanged(
													CompoundButton buttonView,
													boolean isChecked) {
												if (isChecked) {
													selected.add(c);
												} else {
													selected.remove(c);
												}
											}
										});
										txtView.setOnClickListener(new OnClickListener() {

											public void onClick(View v) {
												if (selected.contains(c)) {
													cb.setChecked(false);
												} else {
													cb.setChecked(true);
												}
											}
										});
										return rowView;
									}
								};
								ListView view = (ListView) showSelectUsersDlg
										.findViewById(R.id.listSelectBusinessContactUsersView);
								view.setAdapter(adapter);
								Button btnRemoveUsers = (Button) showSelectUsersDlg
										.findViewById(R.id.btnSelectBusinessContactUsersConfirm);
								btnRemoveUsers
										.setOnClickListener(new OnClickListener() {

											public void onClick(View v) {
												if (!selected.isEmpty()) {
													new AsyncTask<String, Void, HttpResponseData>() {

														@Override
														protected void onPostExecute(
																HttpResponseData result) {
															PersonalPhonebookActivity
																	.endProgress();
															String msg = result != null ? result
																	.toString()
																	: "Error";
															Toast.makeText(
																	BusinessContactActivity.this,
																	msg,
																	Toast.LENGTH_SHORT)
																	.show();
															showSelectUsersDlg
																	.dismiss();
														}

														@Override
														protected void onPreExecute() {
															PersonalPhonebookActivity
																	.showProgress(
																			"Removing users...",
																			BusinessContactActivity.this);
														}

														@Override
														protected HttpResponseData doInBackground(
																String... params) {
															return HttpRequestManager
																	.doRequestWithResponseData(
																			Settings.getBusinessContactUrl(),
																			Settings.makeRemoveBusinessContactUser(selected));
														}

													}.execute(new String[] {});
												}
											}
										});
								showSelectUsersDlg.show();
							}
						} else {
							String msg = result != null ? result.toString()
									: "Error";
							Toast.makeText(BusinessContactActivity.this, msg,
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					protected void onPreExecute() {
						PersonalPhonebookActivity.showProgress(
								"Removing user...",
								BusinessContactActivity.this);
					}

					@Override
					protected HttpResponseData doInBackground(String... params) {
						return HttpRequestManager.doRequestWithResponseData(
								Settings.getBusinessContactUrl(),
								Settings.makeSelectBusinessContactUsers());
					}

				}.execute(new String[] {});
				return false;
			}
		});
	}

	private void addBusinessInfoOptions(Menu menu) {
		MenuItem addBusinessInfoOptions = menu.add("Business Info");
		addBusinessInfoOptions
				.setIcon(R.drawable.mimi_connect_office_directory);
	}

	private void addSendSmsOptions(Menu menu) {
		MenuItem addSendSmsOptions = menu.add("Send Message");
		addSendSmsOptions.setIcon(R.drawable.mimi_connect_sendtext);
		addSendSmsOptions
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity
								.startGeneralActivity(
										BusinessContactActivity.this,
										"Send Message",
										BusinessSmsMessageActivity.class,
										R.layout.usercontact_whitetitled_tabview,
										false);
						return false;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addSendSmsOptions(menu);
		addBusinessInfoOptions(menu);
		addCompanyUserOptions(menu);
		addRemoveUsersOptions(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
		new BusinessContactSearchTask(this).loadViewFromCache(contacts);
	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.OFFICE;
	}
}
