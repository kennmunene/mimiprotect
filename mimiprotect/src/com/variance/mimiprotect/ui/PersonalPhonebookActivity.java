package com.variance.mimiprotect.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.SessionInitializationListener;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.NewBusinessActivity;
import com.variance.mimiprotect.contacts.selection.ContactGeneralSelectionManager;
import com.variance.mimiprotect.contacts.selection.OnContactSelectionComplete;
import com.variance.mimiprotect.contacts.task.BackupTask;
import com.variance.mimiprotect.contacts.task.ContactCacheLoadTask;
import com.variance.mimiprotect.contacts.task.ContactSearchTask;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.backuprestore.BackupRestoreActivity;
import com.variance.mimiprotect.ui.contact.NewContactActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class PersonalPhonebookActivity extends PhonebookActivity {
	private static final class MimiProtectProgressDialog extends ProgressDialog {
		private Context context;
		private boolean indicateCancellation;
		private String cancellationMessage;

		public MimiProtectProgressDialog(Context context,
				boolean indicateCancellation, String cancellationMessage) {
			super(context);
			this.context = context;
			this.indicateCancellation = indicateCancellation;
			this.cancellationMessage = cancellationMessage;
		}

		@Override
		public void onBackPressed() {
			if (indicateCancellation) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setMessage(cancellationMessage);
				builder.setTitle("Cancelling!");
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								MimiProtectProgressDialog.super.onBackPressed();
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// we do nothing literally
								dialog.dismiss();
							}
						});
				builder.show();
			} else {
				super.onBackPressed();
			}
		}

	}

	private static final int CAMERA_REQUEST = 1888;
	private EditText searchText;
	private static ProgressDialog progressDialog;
	private boolean loaded;
	/**
	 * This constant is a hack, we will need to push them through the intents to
	 * the broadcast listeners.
	 */
	public static String CURRENT_DIALLED_NUMBER;
	public static PersonalPhonebookActivity START_ACTIVITY;
	/**
	 * Represents only current personal contacts
	 */
	private List<Contact> currentContacts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_search);
		START_ACTIVITY = this;
		initFromSignup();
		addSoftKeyEnterActionOnSearch();
		// we load set the default search from any other place if any!
		Intent i = getIntent();
		if (i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_SEARCH_TERM)) {
			final String searchTerm = i
					.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_SEARCH_TERM);
			if (!Utils.isNullStringOrEmpty(searchTerm)) {
				// we will need to wait if the session is still initializing
				if (Settings.isSessionInitializing()) {
					SessionInitializationListener listener = new SessionInitializationListener() {

						public void sessionInitialized() {
							searchText.setText(searchTerm);
							loaded = true;
							search(searchTerm);
						}

						public void sessionDestroyed() {

						}
					};
					synchronized (Settings.class) {
						if (Settings.isSessionInitializing()) {
							Settings.addSessionInitializationListener(listener);
						} else {
							searchText.setText(searchTerm);
							loaded = true;
							search(searchTerm);
						}
					}
				}
			}
		}
		initialize();
	}

	public List<Contact> getCurrentContacts() {
		return currentContacts;
	}

	public void setCurrentContacts(List<Contact> currentContacts) {
		this.currentContacts = currentContacts;
	}

	/**
	 * Check if we have just signed up, then we bring up the backup contacts
	 * option
	 */
	private void initFromSignup() {
		int isFromSignup = getIntent().getIntExtra(
				MimiProtectIntentConstants.ON_SIGNUP_EXTRA, -1);
		if (isFromSignup == MimiProtectIntentConstants.ON_SIGNUP_VALUE) {

			new AlertDialog.Builder(PersonalPhonebookActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Backup Your Contacts...")
					.setMessage(
							"Welcome to MiMiProtect!"
									+ "\nStart off by backing up your contacts."
									+ "\nIf you choose later, you can perform the backup by clicking on your phones menu.")
					.setPositiveButton("Backup Now",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									new BackupTask(
											PersonalPhonebookActivity.this)
											.execute(new Void[] {});
									getIntent()
											.removeExtra(
													MimiProtectIntentConstants.ON_SIGNUP_EXTRA);
								}

							})
					.setNegativeButton("Later",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									getIntent()
											.removeExtra(
													MimiProtectIntentConstants.ON_SIGNUP_EXTRA);
								}
							}).show();
		}
	}

	protected void initialize() {
		if (!loaded && !Settings.isDebugging()) {
			loaded = true;
			initLoad();
		}
	}

	public boolean isDefaultSearch() {
		Log.i("Search Param: ", searchParameter.getSearchTerm());
		return searchParameter == null
				|| searchParameter.getSearchTerm() == null
				|| searchParameter.getSearchTerm().equals(
						Settings.INIT_ANDROID_LOAD_TXT);
	}

	private void addSoftKeyEnterActionOnSearch() {
		searchText = (EditText) findViewById(R.id.txtSearchContacts);
		searchText.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						search();
						return true;
					default:
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DEL:
						String txt = searchText.getText().toString();
						if (Utils.isNullOrEmpty(txt)) {
							searchParameter
									.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
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

	public void addSoftKeyEnterActionOnSearch(final EditText searchTxt) {
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						search();
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
									.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
							searchParameter.setCurrentPage(0);
							search();
						}
						return true;
					}
				}
				return false;
			}
		});
	}

	private void initLoad() {
		search();
	}

	private View getNewContactOptionView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.usercontact_newcontactoptions,
				null, false);
		if (view != null) {
			// get the button options
			LinearLayout scanQrCode = (LinearLayout) view
					.findViewById(R.id.txtScanQrCode);
			if (scanQrCode != null) {
				scanQrCode.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Map<String, Boolean> extras = new HashMap<String, Boolean>();
						extras.put(
								MimiProtectIntentConstants.MIMI_PROTECT_NEWCONTACT_FROM_QR,
								true);
						PhonebookActivity.startGeneralActivity(
								PersonalPhonebookActivity.this, "My Phonebook",
								NewContactActivity.class,
								R.layout.usercontact_tabview, false, extras);
					}
				});
			}
			LinearLayout enterUserInformation = (LinearLayout) view
					.findViewById(R.id.txtEnterUserInformation);
			if (enterUserInformation != null) {
				enterUserInformation.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						PhonebookActivity.startGeneralActivity(
								PersonalPhonebookActivity.this, "My Phonebook",
								NewContactActivity.class,
								R.layout.usercontact_tabview, false);
					}
				});
			}
			LinearLayout livelinkRequest = (LinearLayout) view
					.findViewById(R.id.txtLivelinkRequest);
			if (livelinkRequest != null) {
				livelinkRequest.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						performLivelinkRequest(null);
					}
				});
			}
		}
		return view;
	}

	public void handleAddContact(View view) {
		final String[] newContactOptions = { "Scan QR Code",
				"Enter user information", "Livelink Request" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select new contact options");
		View newContactOptionView = getNewContactOptionView();
		if (newContactOptionView != null) {
			builder.setView(newContactOptionView);
		} else {
			builder.setSingleChoiceItems(newContactOptions, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int index) {
							switch (index) {
							case 0:
								Map<String, Boolean> extras = new HashMap<String, Boolean>();
								extras.put(
										MimiProtectIntentConstants.MIMI_PROTECT_NEWCONTACT_FROM_QR,
										true);
								PhonebookActivity.startGeneralActivity(
										PersonalPhonebookActivity.this,
										"My Phonebook",
										NewContactActivity.class,
										R.layout.usercontact_tabview, false,
										extras);
								break;
							case 1:
								PhonebookActivity.startGeneralActivity(
										PersonalPhonebookActivity.this,
										"My Phonebook",
										NewContactActivity.class,
										R.layout.usercontact_tabview, false);
								break;
							case 2:
								performLivelinkRequest(null);
								break;
							case 3:
								Toast.makeText(PersonalPhonebookActivity.this,
										"Sorry comming soon",
										Toast.LENGTH_SHORT).show();
								break;
							}
						}
					});
		}
		AlertDialog alert = builder.create();
		alert.show();
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
							Toast.makeText(PersonalPhonebookActivity.this,
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

	public static void updateProgressBar(int update) {
		if (progressDialog != null && progressDialog.getMax() > 0) {

			progressDialog.setProgress(update);
		}
	}

	public static void showProgress(final String message, Context context) {
		showProgress(message, context, false, 0);
	}

	public static void showProgress(final String message, Context context,
			boolean bar, int max) {
		/*
		 * if (progressDialog == null) { progressDialog = new
		 * ProgressDialog(context); } else { progressDialog.dismiss(); }
		 */
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(true);
		if (bar) {
			progressDialog.setMax(max);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				HttpRequestManager.abortRequest();
			}
		});
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}

	public static <P, S, T> void showProgress(final String message,
			Context context, final AsyncTask<P, S, T> asyncTask) {
		showProgress(message, context, asyncTask, false, 0);
	}

	public static <P, S, T> void showProgress(final String message,
			Context context, final AsyncTask<P, S, T> asyncTask, boolean bar,
			int max) {
		/*
		 * if (progressDialog == null) { progressDialog = new
		 * ProgressDialog(context); } else { progressDialog.dismiss(); }
		 */
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				HttpRequestManager.abortRequest();
				asyncTask.cancel(true);
			}
		});
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (bar) {
			progressDialog.setMax(max);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		progressDialog.show();
	}

	public static <P, S, T> void showProgress(final String message,
			final Context context, final AsyncTask<P, S, T> asyncTask,
			boolean bar, int max, final boolean indicateCancellation,
			final String cancellingMessage) {
		/*
		 * if (progressDialog == null) { progressDialog = new
		 * ProgressDialog(context); } else { progressDialog.dismiss(); }
		 */
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = new MimiProtectProgressDialog(context,
				indicateCancellation, cancellingMessage);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(final DialogInterface dialog) {
				HttpRequestManager.abortRequest();
				asyncTask.cancel(true);
			}
		});
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (bar) {
			progressDialog.setMax(max);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		progressDialog.show();
	}

	public static void endProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void addContactFromBusinessCard() {
		try {
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_REQUEST);
		} catch (Exception e) {
			Log.e("Error Starting Camera: ", e.toString());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == CAMERA_REQUEST) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				String result = HttpRequestManager.doRequest(
						Settings.getProcessBusinessCardImageUrl(),
						Settings.makeBusinessCardParameter(photo));
				if (result != null) {
					Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				}
				Log.i("Process Business Card Result: ", result);
			}
		} catch (Exception e) {
			Log.e("Error Processing Image result: ", e.toString());
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}

	public static void showCallOptions(final String[] phones,
			final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Contact Number to call");
		builder.setSingleChoiceItems(phones, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String number = phones[item];
						CURRENT_DIALLED_NUMBER = number;
						Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + number));
						context.startActivity(callIntent);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showMessage(final String title, final String message,
			final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showTextOptions(final String[] phones,
			final Context context) {
		final List<String> selectedPhones = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Contact Number to text");
		builder.setMultiChoiceItems(phones, new boolean[phones.length],
				new OnMultiChoiceClickListener() {

					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							selectedPhones.add(phones[which]);
						} else {
							selectedPhones.remove(phones[which]);
						}
					}
				});
		builder.setPositiveButton("sms", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String separator = ";";
				if (android.os.Build.MANUFACTURER.toLowerCase().contains(
						"samsung")) {
					separator = ",";
				}
				String numbers = "";
				for (String s : selectedPhones) {
					if (!"".equals(numbers)) {
						numbers += separator;
					}
					numbers += s;
				}
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("sms:" + numbers)));
			}
		});
		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						selectedPhones.clear();
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showEmailOptions(final String[] emails,
			final Context context) {
		final List<String> selectedEmails = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Contact emails to send message");
		builder.setMultiChoiceItems(emails, new boolean[emails.length],
				new OnMultiChoiceClickListener() {

					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							selectedEmails.add(emails[which]);
						} else {
							selectedEmails.remove(emails[which]);
						}
					}
				});
		builder.setPositiveButton("E-mail",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						String toEmails[] = selectedEmails
								.toArray(new String[0]);
						i.putExtra(Intent.EXTRA_EMAIL, toEmails);
						try {
							context.startActivity(Intent.createChooser(i,
									"Send mail..."));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(context,
									"There are no email clients installed.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						selectedEmails.clear();
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static int getMaximumListRows(Activity context) {
		return getMaximumListRows(context, 60);
	}

	public static int getMaximumListRows(Activity context,
			final int listRowHeight) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rows = dm.widthPixels / listRowHeight;
		return rows + 5;
	}

	public void search() {
		String searchTerm = searchText.getText().toString().trim();
		search(searchTerm);
	}

	public void search(String searchTerm) {
		this.searchParameter.setSearchTerm(searchTerm);
		this.searchParameter.setCurrentPage(0);
		this.searchParameter.setMaxResult(getMaximumListRows(this));
		Log.e("Search Parameters: ", searchParameter.toString());
		doSearch();
	}

	protected void doSearch() {
		super.doSearch();
		if (!isLoadedFromCache()) {
			Log.e("isLoadedFromCache: ", isLoadedFromCache() + "");
			new ContactSearchTask(this)
					.execute(new SearchParameter[] { searchParameter });
		}
	}

	protected void doBackgroundSearch() {
		Log.e("doBackgroundSearch:", "doBackgroundSearch");
		super.doBackgroundSearch();
		Log.e("isLoadedFromCache: ", isLoadedFromCache() + "");
		if (!isLoadedFromCache()) {
			new ContactSearchTask(this, true)
					.execute(new SearchParameter[] { searchParameter });
		}
	}

	private void addBusinessTabOptions(Menu menu) {
		if (!hasBusinessContacts()
				&& !MimiProtectGeneralManager.getUserSettingOverride()
						.isBusinessPhonebookUser()) {
			MenuItem businessContactMenu = menu.add("Add Office Phonebook");
			businessContactMenu
					.setIcon(R.drawable.mimi_connect_business_directory);
			businessContactMenu
					.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						Activity activity = PersonalPhonebookActivity.this;

						public boolean onMenuItemClick(MenuItem item) {
							if (Settings.isLoggedIn()) {
								PhonebookActivity.startGeneralActivity(
										activity, "Office",
										NewBusinessActivity.class);
							} else {
								Toast.makeText(
										PersonalPhonebookActivity.this,
										"You must be logged in to create a new business phonebook",
										Toast.LENGTH_LONG).show();
							}
							return true;
						}
					});
		}
	}

	private void addBackupRestoreOptions(Menu menu) {
		MenuItem backupRestoreSubMenu = menu.add("Backup/Restore");
		backupRestoreSubMenu
				.setIcon(R.drawable.mimi_connect_backuprestoreimage);
		backupRestoreSubMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					Activity activity = PersonalPhonebookActivity.this;

					public boolean onMenuItemClick(MenuItem item) {
						if (Settings.isLoggedIn()) {
							if (MimiProtectGeneralManager.hasCurrentPhoneLock()) {
								PhonebookActivity.startGeneralActivity(
										activity, "Backup/Restore",
										BackupRestoreActivity.class);
							} else {
								Toast.makeText(
										PersonalPhonebookActivity.this,
										"This phone is either locked or you are offline",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(
									PersonalPhonebookActivity.this,
									"Sorry! You must be logged in to backup or restore your phone contents.",
									Toast.LENGTH_LONG).show();
						}
						return true;
					}
				});
	}

	private void addLivelinkOptions(Menu menu) {
		MenuItem livelinkMenu = menu.add("Livelink");
		livelinkMenu.setIcon(R.drawable.mimi_connect_livelink);
		livelinkMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			Activity activity = PersonalPhonebookActivity.this;

			public boolean onMenuItemClick(MenuItem item) {
				if (Settings.isLoggedIn()) {
					PhonebookActivity.startGeneralActivity(activity,
							"Livelink", LiveLinkRequestsActivity.class);
				} else {
					Toast.makeText(
							PersonalPhonebookActivity.this,
							"Sorry! You must be logged in to view or send livelink requests.",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}

	private void addDeleteOptions(Menu menu) {
		MenuItem livelinkMenu = menu.add("Delete");
		livelinkMenu.setIcon(R.drawable.mimi_connect_deletecontacts);
		livelinkMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			Activity activity = PersonalPhonebookActivity.this;

			public boolean onMenuItemClick(MenuItem item) {
				if (Settings.isLoggedIn()) {
					OnContactSelectionComplete onContactSelectionComplete = new OnContactSelectionComplete() {

						public void contactSelected(
								final List<Contact> selectedContacts) {
							new AsyncTask<String, Void, String>() {

								@Override
								protected void onPostExecute(String result) {
									PersonalPhonebookActivity.endProgress();
									if (result != null) {
										Toast.makeText(activity, result,
												Toast.LENGTH_SHORT).show();
									}
									MimiProtectGeneralManager
											.deleteContacts(selectedContacts);
									doSearch();
								}

								@Override
								protected void onPreExecute() {
									PersonalPhonebookActivity.showProgress(
											"Deleting. Please wait...",
											activity);
								}

								@Override
								protected String doInBackground(
										String... params) {
									return HttpRequestManager.doRequest(
											Settings.getDeleteContactUrl(),
											Settings.makeDeleteContactsParameters(selectedContacts));
								}

							}.execute(new String[] {});
						}
					};
					ContactGeneralSelectionManager selectionManager = new ContactGeneralSelectionManager(
							true, activity, "Select Contacts", "Delete",
							onContactSelectionComplete);
					SearchParameter sp = new SearchParameter();
					sp.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
					sp.setMaxResult(PersonalPhonebookActivity
							.getMaximumListRows(activity) * 4);
					PersonalPhonebookActivity
							.updateSearchParameterForPersonalContacts(sp);
					selectionManager.execute(new SearchParameter[] { sp });
				} else {
					Toast.makeText(activity,
							"You must be logged in to delete contacts",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addLivelinkOptions(menu);
		addBusinessTabOptions(menu);
		addBackupRestoreOptions(menu);
		addDeleteOptions(menu);
		return super.onCreateOptionsMenu(menu);
	}

	public static boolean setShortCut(Context context, String appName) {
		if (!Settings.isShortcutCreated(context)) {
			System.out.println("in the shortcutapp on create method ");
			boolean flag = false;
			int app_id = -1;
			PackageManager p = context.getPackageManager();
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> res = p.queryIntentActivities(i, 0);
			System.out.println("the res size is: " + res.size());
			for (int k = 0; k < res.size(); k++) {
				System.out.println("the application name is: "
						+ res.get(k).activityInfo.loadLabel(p));
				if (res.get(k).activityInfo.loadLabel(p).toString()
						.equals(appName)) {
					flag = true;
					app_id = k;
					break;
				}
			}
			if (flag) {
				ActivityInfo ai = res.get(app_id).activityInfo;
				System.out.println("package: " + ai.packageName);
				Intent shortcutIntent = new Intent();
				shortcutIntent.setClassName(ai.packageName, ai.name);
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				shortcutIntent.putExtra(
						MimiProtectIntentConstants.SHORTCUT_INTENT_EXTRA,
						true + "");
				Intent intent = new Intent();
				intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
				intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
				intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
						Intent.ShortcutIconResource.fromContext(context,
								R.drawable.mimi_connect_logo));
				intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
				context.sendBroadcast(intent);
			} else {
				System.out.println("appllicaton not found");
			}
			Settings.setShortcutCreated(context);
		}
		return true;
	}

	@Override
	protected boolean doLoadFromCache() {
		return super.doLoadFromCache()
				|| searchParameter.getSearchTerm().equals(
						Settings.INIT_ANDROID_LOAD_TXT);
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
		new ContactCacheLoadTask(this, true).loadCachedView(contacts);
	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.PRIVATE;
	}
}
