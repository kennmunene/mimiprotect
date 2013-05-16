package com.variance.mimiprotect.ui.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.chat.ChatManagerTabActivity;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.managers.ContactDetailsViewManager;
import com.variance.mimiprotect.contacts.selection.ContactGeneralSelectionManager;
import com.variance.mimiprotect.contacts.selection.OnContactSelectionComplete;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.widget.FastDialContacts;
import com.variance.mimiprotect.widget.MimiProtectAppWidgetProvider;

public class ContactOptionsActivity extends MimiProtectActivity {
	private Contact contact;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_contactoptions);
		contact = (Contact) getIntent().getSerializableExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT);
		TextView txtView = (TextView) findViewById(R.id.txtContactName);
		txtView.setText(contact.getName());
		TextView phnView = (TextView) findViewById(R.id.contactPhoneNumber);
		if (contact.getPhones() != null && contact.getPhones().length > 0) {
			phnView.setText(contact.getPhones()[0]);
		}
	}

	public void handleContactOptions(View view) {
		switch (view.getId()) {
		case R.id.contactcall:
			makeCall();
			break;
		case R.id.sendContact:
			sendContact();
			break;
		case R.id.sendText:
			sendSms();
			break;
		case R.id.gotoWebsite:
			gotoWebsite();
			break;
		case R.id.sendEmail:
			sendEmail();
			break;
		case R.id.livelink:
			livelink();
			break;
		case R.id.viewDetails:
			viewDetails();
			break;
		case R.id.deleteContact:
			deleteContact();
			break;
		case R.id.viewFastDials:
			addFastDial();
			break;
		case R.id.connectToChat:
			startChat();
			break;
		}
	}

	private void startChat() {
		if (contact.getChatId() != null) {
			Intent intent = new Intent(this, ChatManagerTabActivity.class);
			Intent callingintent = getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT,
					contact);
			intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_ID,
					contact.getChatId());
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
					contact.getName());
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_CHAT_INITIATED_BY_USER,
					true);
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_CHAT_STATUS,
					contact.getChatStatus());
			startActivity(intent);
		} else {
			Toast.makeText(this, "Sorry, contact is not online",
					Toast.LENGTH_LONG).show();
		}
	}

	private void addFastDial() {
		final FastDialContacts dialContacts = Settings
				.getOrCreateFastDialContacts(this);
		// we need to check if we have already added at least three contacts and
		// then choose which one to replace.
		if (dialContacts.getFastDials().size() >= 3) {
			// then ask which contact to replace.
			MimiProtectActivity.showSelectOption("Select Contact To Replace.",
					this, dialContacts.getFastDials(),
					new OnRequestComplete<Contact>() {

						@Override
						public void requestComplete(Contact result) {
							setFastDial(dialContacts, result, result != null);
						}
					}, new StringConverter<Contact>() {

						@Override
						public String toString(Contact ob) {
							return ob.getName();
						}
					});
		} else {
			setFastDial(dialContacts, null, false);
		}

	}

	private void setFastDial(FastDialContacts dialContacts,
			Contact contactToReplace, boolean replace) {
		if (replace) {
			dialContacts.getFastDials().set(
					dialContacts.getFastDials().indexOf(contactToReplace),
					contact);
		} else if (!dialContacts.getFastDials().contains(contact)) {
			dialContacts.getFastDials().add(contact);
		} else {
			Toast.makeText(this, "The contact is already in fast dial!",
					Toast.LENGTH_LONG).show();
		}
		Settings.setPreference(this,
				FastDialContacts.FAST_DIAL_CONTACT_PREFERENCE_ID,
				dialContacts.toString());
		String message = "Contact successfully added as fast dial."
				+ "\nIf you have not added the on screen widget, you can add it now."
				+ "\nIt may take awhile before the widget updates with this new information.\n"
				+ "Also not that currently maximum of three contacts are supported for fast dial.";
		String title = "Contact Added to first dial.";
		PersonalPhonebookActivity.showMessage(title, message, this);
		// lets try to update the appwidget here.
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.mimi_connect_widget);
		ComponentName widget = new ComponentName(this,
				MimiProtectAppWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(widget, views);
	}

	private void makeCall() {
		String[] phones = contact.getPhones();
		if (phones != null && phones.length > 1) {
			showCallOptions(phones, this);
		} else {
			PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = phones != null
					&& phones.length > 0 ? phones[0] : null;
			if (PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER != null
					&& !"".equals(PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER
							.trim())) {
				Intent callIntent = new Intent(
						Intent.ACTION_CALL,
						Uri.parse("tel:"
								+ PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER));
				startActivity(callIntent);
			} else {
				Toast.makeText(
						this,
						contact.isCorporateContact() ? "Business does not have a telephone contact"
								: "User does not have telephone contact",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private static LinearLayout getView(ViewGroup parent, Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.usercontact_calloption, parent, false);
		return layout;
	}

	public static void viewMorePhoneNumbers(View view, Dialog d) {
		View layout = d.findViewById(R.id.viewMorePhoneNumbersPanel);
		int state = layout.getVisibility();
		if (state == View.GONE) {
			layout.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.mimi_connect_less);
		} else {
			layout.setVisibility(View.GONE);
			view.setBackgroundResource(R.drawable.mimi_connect_more);
		}
	}

	private static void addOnCallListener(View view, final String phone,
			final Context context) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = phone;
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + phone));
				context.startActivity(callIntent);
			}
		});
	}

	private static void showCallOptions(final String[] phones,
			final Context context) {
		final Dialog d = new Dialog(context);
		d.setTitle("Call Option");
		d.setContentView(R.layout.usercontact_callortextoptions);
		TextView defaultView = (TextView) d
				.findViewById(R.id.default_call_number_view);
		defaultView.setText(phones[0] + " (default)");
		addOnCallListener(defaultView, phones[0], context);
		ListView list = (ListView) d
				.findViewById(R.id.viewMorePhoneNumbersPanel);
		final String[] optionsPhones = new String[phones.length - 1];
		System.arraycopy(phones, 1, optionsPhones, 0, optionsPhones.length);
		list.setAdapter(new ArrayAdapter<String>(context,
				R.layout.usercontact_calloption, optionsPhones) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = null;
				rowView = inflater.inflate(R.layout.usercontact_calloption,
						parent, false);
				TextView view = (TextView) rowView
						.findViewById(R.id.call_number_view);
				view.setText(optionsPhones[position]);
				addOnCallListener(rowView, optionsPhones[position], context);
				return rowView;
			}
		});
		final Button btnShowMorePhoneNumbers = (Button) d
				.findViewById(R.id.btnShowMorePhoneNumbers);
		btnShowMorePhoneNumbers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewMorePhoneNumbers(btnShowMorePhoneNumbers, d);
			}
		});
		// if (phones.length > 1) {
		// for (int i = 1; i < phones.length; i++) {
		// LinearLayout option = getView(morePhoneNumbersView, context);
		// TextView view = (TextView) option
		// .findViewById(R.id.call_number_view);
		// view.setText(phones[i]);
		// addOnCallListener(view, phones[i], context);
		// morePhoneNumbersView.addView(option);
		// }
		//
		// }
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setTitle("Contact Number to call");
		// builder.setSingleChoiceItems(phones, -1,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int item) {
		// String number = phones[item];
		// PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = number;
		// Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
		// .parse("tel:" + number));
		// context.startActivity(callIntent);
		// }
		// });
		// AlertDialog alert = builder.create();
		// alert.show();
		d.show();
	}

	public void sendSms() {
		String[] phones = contact.getPhones();
		if (phones != null && phones.length > 1) {
			showTextOptions(phones, this);
		} else {
			PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = phones != null
					&& phones.length > 0 ? phones[0] : null;
			if (PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER != null
					&& !"".equals(PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER
							.trim())) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
						+ PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER)));
			} else {
				Toast.makeText(
						this,
						contact.isCorporateContact() ? "Business does not have a telephone contact"
								: "User does not have telephone contact",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private static void showTextOptions(final String[] phones,
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

	public void sendEmail() {
		String[] emails = contact.getEmails();
		if (emails != null && emails.length > 1) {
			showEmailOptions(emails, this);
		} else {
			String emailAddr = emails != null && emails.length > 0 ? emails[0]
					: null;
			if (emailAddr != null && !"".equals(emailAddr)) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddr });
				i.putExtra(Intent.EXTRA_SUBJECT, "Hi");
				i.putExtra(Intent.EXTRA_TEXT, "Hi");
				try {
					startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(this,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(
						this,
						contact.isCorporateContact() ? "Business does not have an email address"
								: "User does not have an email address",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private static void showEmailOptions(final String[] emails,
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

	private void livelink() {
		if (!contact.isCorporateContact()) {
			String liveLinkId = contact.getLivelinkId();
			if (!(liveLinkId == null || "null".equals(liveLinkId) || ""
					.equals(liveLinkId))) {
				Toast.makeText(this, "Live link has already been made.",
						Toast.LENGTH_LONG).show();
				return;
			}
			new AsyncTask<String, Void, String>() {

				@Override
				protected void onPreExecute() {
					PersonalPhonebookActivity.showProgress(
							"Please wait. Livelinking...",
							ContactOptionsActivity.this);
				}

				@Override
				protected String doInBackground(String... params) {

					final String shareContactUrl = Settings
							.getContactSharedUrl();
					String result = HttpRequestManager.doRequest(
							shareContactUrl,
							Settings.makeContactShareIdParameters(contact));
					return result;
				}

				@Override
				protected void onPostExecute(String result) {
					PersonalPhonebookActivity.endProgress();
					if (result != null) {
						Toast.makeText(ContactOptionsActivity.this, result,
								Toast.LENGTH_LONG).show();
					}
				}

			}.execute(new String[] {});
		} else {
			Toast.makeText(
					this,
					"Sorry you cannot share your contact with a Business organization",
					Toast.LENGTH_LONG).show();
		}
	}

	private void viewDetails() {
		ContactDetailsViewManager cdvm = new ContactDetailsViewManager(contact,
				this);
		cdvm.initializeAndShow();
	}

	private void gotoWebsite() {
		if (contact.getWebsite() != null && !contact.getWebsite().equals("")) {
			String url = contact.getWebsite();
			Intent webView = new Intent(Intent.ACTION_VIEW);
			webView.setData(Uri.parse(url));
			startActivity(webView);
		} else {
			Toast.makeText(
					this,
					contact.isCorporateContact() ? "Business does not have web address"
							: "User does not have web address",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void sendContact() {
		// Get the list of contacts to display without requesting
		// from server.
		// we get any form of contact to send actually
		List<Contact> contactToSend = new ArrayList<Contact>();
		contactToSend.add(contact);
		final ContactGeneralSelectionManager cgsm = new ContactGeneralSelectionManager(
				true, this, "Send To...", "Send Contact", null);
		OnContactSelectionComplete ocsc = new OnContactSelectionComplete() {

			public void contactSelected(List<Contact> selectedContacts) {
				Log.i("Selected Contacts", selectedContacts.toString());
				// send the data to the server for sharing or
				HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

					public void onTaskStarted() {
					}

					public void onTaskCompleted(HttpResponseData result) {
						if (result != null) {
							Toast.makeText(ContactOptionsActivity.this,
									result.toString(), Toast.LENGTH_LONG)
									.show();
						}
					}

					public HttpResponseData doTask(Void... params) {
						return HttpRequestManager.doRequestWithResponseData(
								Settings.getContactSharedUrl(), Settings
										.makeSendOrShareContactsParameter(
												Arrays.asList(contact),
												cgsm.getSelectedContacts()));
					}
				};
				new HttpRequestTask<Void, Void, HttpResponseData>(listener,
						"Sending/Sharing Contacts. Please wait...",
						ContactOptionsActivity.this).execute();
			}
		};
		cgsm.setOnContactSelectionComplete(ocsc);
		SearchParameter sp = new SearchParameter();
		sp.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
		sp.setMaxResult(PersonalPhonebookActivity.getMaximumListRows(this) * 4);
		PersonalPhonebookActivity.updateSearchParameterForPersonalContacts(sp);
		cgsm.execute(new SearchParameter[] { sp });
	}

	private void deleteContact() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm delete contact...")
				.setMessage(
						"Are you sure you want to delete this contact? "
								+ "Note: This will delete contact from the server only.")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {

								new AsyncTask<String, Void, String>() {

									@Override
									protected void onPostExecute(String result) {
										PersonalPhonebookActivity.endProgress();
										Toast.makeText(
												ContactOptionsActivity.this,
												result + "", Toast.LENGTH_SHORT)
												.show();
										if (result != null
												&& result.toLowerCase()
														.contains("success")) {
											MimiProtectGeneralManager
													.deleteContact(contact);
										}
									}

									@Override
									protected void onPreExecute() {
										PersonalPhonebookActivity.showProgress(
												"Deleting. Please wait...",
												ContactOptionsActivity.this);
									}

									@Override
									protected String doInBackground(
											String... params) {
										String result = HttpRequestManager.doRequest(
												Settings.getDeleteContactUrl(),
												Settings.makeDeleteContactParameters(contact
														.getId()));
										return result;
									}

								}.execute(new String[] {});
							}

						}).setNegativeButton("No", null).show();
	}
}
