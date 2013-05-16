package com.variance.mimiprotect.contacts.managers;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.chat.ChatManagerTabActivity;
import com.variance.mimiprotect.chat.ChatStatus;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.contact.ContactOptionsActivity;
import com.variance.mimiprotect.ui.contact.task.FindContactTask;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;
import com.variance.mimiprotect.widget.FastDialContacts;

public class ContactListAdapter extends ArrayAdapter<Contact> {
	private ArrayList<Contact> contacts;
	private Context context;
	private boolean businessAdpter;
	// private boolean showPersonalContacts = true;
	private boolean showBusinessListings = true;

	// private int textViewResourceId;

	public ContactListAdapter(Context context, int textViewResourceId,
			ArrayList<Contact> contacts) {
		super(context, textViewResourceId, contacts);
		this.contacts = contacts;
		this.context = context;
		// this.textViewResourceId = textViewResourceId;
	}

	public ContactListAdapter(Context context, int textViewResourceId,
			ArrayList<Contact> contacts, boolean showPersonalContacts,
			boolean showBusinessListings) {
		super(context, textViewResourceId, contacts);
		this.contacts = contacts;
		this.context = context;
		this.showBusinessListings = showBusinessListings;
		// this.showPersonalContacts = showPersonalContacts;
		// this.textViewResourceId = textViewResourceId;
	}

	public ContactListAdapter(Context context, int textViewResourceId,
			ArrayList<Contact> contacts, boolean businessAdpter) {
		super(context, textViewResourceId, contacts);
		this.contacts = contacts;
		this.context = context;
		this.businessAdpter = businessAdpter;
		// this.textViewResourceId = textViewResourceId;
	}

	public boolean isBusinessAdpter() {
		return businessAdpter;
	}

	@Override
	public void add(Contact object) {
		super.add(object);
		this.contacts.add(object);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		Contact c = contacts.get(position);
		rowView = inflater.inflate(R.layout.usercontact_singlecontactview,
				parent, false);
		TextView tickerView = (TextView) rowView
				.findViewById(R.id.businessTicker);
		LinearLayout layout = (LinearLayout) rowView
				.findViewById(R.id.firstDegreeConnectionDetails);
		TextView textView = (TextView) rowView.findViewById(R.id.viewContact);
		textView.setText(c.getName());
		if (c.isCorporateContact() && c.getMessage() != null
				&& !c.getMessage().trim().equals("")
				&& !"null".equals(c.getMessage().trim())
				&& showBusinessListings) {
			tickerView.setText(c.getMessage());
			tickerView.setSelected(true);
			addOnClickListener(tickerView, position, true);
			tickerView.setVisibility(View.VISIBLE);
		} else if (c.isFirstDegreeConnection()
				&& !Utils.isNullStringOrEmpty(c.getContactName())) {
			layout.setVisibility(View.VISIBLE);
			TextView contactFromViewField = (TextView) rowView
					.findViewById(R.id.firstDegreeContactDiscoveredFrom);
			TextView contactTitle = (TextView) rowView
					.findViewById(R.id.firstDegreeContactTitle);
			TextView contactOrganization = (TextView) rowView
					.findViewById(R.id.firstDegreeContactOrganization);
			// show from whom it was discovered from.
			String connectionName = "1st Degree Connection from: ("
					+ c.getContactName() + ")";
			contactFromViewField.setText(connectionName);
			if (!Utils.isNullStringOrEmpty(c.getTitle())) {
				contactTitle.setText(c.getTitle());
			} else {
				contactTitle.setVisibility(View.GONE);
			}
			if (!Utils.isNullStringOrEmpty(c.getOrganization())) {
				contactOrganization.setText(c.getOrganization());
			} else {
				contactOrganization.setVisibility(View.GONE);
			}
		}
		if (!c.isFirstDegreeConnection()) {
			// Invite the contact to chat by email or livelink id. We only do
			// We should not invite first degree contact to chat!!
			checkContactOnline(c, rowView);
		}
		if (!c.isDummyContac()) {
			addOnClickListener(rowView, position);
			if (!c.isFirstDegreeConnection() && !c.isCorporateContact()) {
				addOnLongClickListener(rowView, position);
			}
			if (c.getLivelinkId() != null
					&& !"null".equalsIgnoreCase(c.getLivelinkId())
					&& !"".equals(c.getLivelinkId().trim())) {
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.contactImage);
				imageView
						.setImageResource(R.drawable.mimi_connect_contactimage_livelinked);
			}
		}
		return rowView;
	}

	private void inviteToChat(View view, final Contact contact,
			final ChatStatus status) {
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(context,
						ChatManagerTabActivity.class);
				Intent callingintent = ((Activity) context).getIntent();
				if (callingintent != null) {
					intent.putExtras(callingintent);
				}
				intent.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT,
						contact);
				intent.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_CHAT_ID,
						status.getId());
				intent.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
						contact.getName());
				intent.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_CHAT_INITIATED_BY_USER,
						true);
				intent.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_CHAT_STATUS,
						status);
				context.startActivity(intent);
			}
		});
	}

	private boolean isNotNullNorEmpty(String value) {
		return value != null && !"".equals(value.trim())
				&& !"null".equals(value.trim());
	}

	private void addOnClickListener(View rowView, final int position) {
		// View view = rowView
		// .findViewById(R.id.singlecontactview_contactinfoview);
		rowView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final Contact c = contacts.get(position);
				if (c.isFirstDegreeConnection()) {
					// then ask the user to contact his contact for a
					// connection.
					String message = c.getName()
							+ ", is a first degree connection from your contact: "
							+ c.getContactName() + ". Do you want to call "
							+ c.getContactName() + " for an introduction to "
							+ c.getName() + "?";
					String title = "First Degree connection: " + c.getName();
					MimiProtectActivity
							.showYesOrNoOption(
									(Activity) context,
									message,
									title,
									new MimiProtectActivity.OnRequestComplete<Boolean>() {

										public void requestComplete(
												Boolean result) {
											if (result != null && result) {
												// request the contact details
												// from the server for the
												// current user.
												findContactFromServer(c
														.getContactId());
											}
										}
									});
				} else {
					showContactViewOptions(c);
				}
			}
		});
	}

	private void checkContactOnline(final Contact contact, final View rowView) {
		HttpRequestTaskListener<String, ChatStatus> listener = new HttpRequestTaskListener<String, ChatStatus>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(ChatStatus result) {
				if (result != null
						&& result.getStatus() == ChatStatus.STATUS_ONLINE) {
					View view = rowView
							.findViewById(R.id.livelinkedContactInviteToChat);
					if (view != null) {
						view.setVisibility(View.VISIBLE);
						contact.setChatId(result.getId());
						inviteToChat(view, contact, result);
						contact.setChatStatus(result);
					}
				}
			}

			public ChatStatus doTask(String... params) {
				if (DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager() != null) {
					Log.i("ContactListAdapter ChatStatus","contact is "+contact);
					Log.i("Checking chat status for",
							"for " + contact.getName());
					ChatStatus status = null;
					if (contact.getLivelinkId() != null) {
						status = DashBoardActivity.DASH_BOARD_ACTIVITY
								.getChatManager().getChatStatusMimi(
										contact.getLivelinkId());
						Log.i("mimiprotect online:", "" + contact.getName()
								+ "(" + status + ")");
					}
					if (status == null
							|| status.getStatus() == ChatStatus.STATUS_OFFLINE) {
						// get email address
						for (String email : contact.getEmails()) {
							status = DashBoardActivity.DASH_BOARD_ACTIVITY
									.getChatManager().getChatStatusGmail(email);
							Log.i("gtalk/facebook online:", "" + email + "("
									+ status + ")");
							if (status != null
									&& status.getStatus() == ChatStatus.STATUS_ONLINE) {
								break;
							}
						}
					}
					return status;
				}
				return null;
			}
		};
		new HttpRequestTask<String, Void, ChatStatus>(listener,
				"Checking livelinked contacts online", context)
				.executeInBackground(contact.getLivelinkId());
	}

	private void showContactViewOptions(Contact c) {
		if (c != null && !c.isBusinessContactHeaderStart()
				&& !c.isPersonalContactHeaderStart() && context != null) {
			Intent intent = new Intent(context, GeneralTabActivity.class);
			Intent callingintent = ((Activity) context).getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
					ContactOptionsActivity.class.getName());
			String title = c.isCorporateContact() ? (isNotNullNorEmpty(c
					.getCompanyName())) ? c.getCompanyName()
					: "Business Directory" : (isNotNullNorEmpty(c
					.getOrganization())) ? c.getOrganization() : "My Phonebook";
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
					title);
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT, c);
			context.startActivity(intent);
		}
	}

	private void findContactFromServer(final String contactId) {
		// get the contact from the server
		new FindContactTask((Activity) context,
				new MimiProtectActivity.OnRequestComplete<Contact>() {

					public void requestComplete(Contact result) {
						if (result == null) {
							Toast.makeText(context,
									"Sorry! an error has occurred",
									Toast.LENGTH_LONG).show();
						} else {
							showContactViewOptions(result);
						}
					}
				}).execute(contactId);
	}

	private void addOnClickListener(TextView view, final int position,
			final boolean isTickerMessage) {
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Contact c = contacts.get(position);
				if (isTickerMessage) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(c.getMessage())
							.setCancelable(true)
							.setNegativeButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	}

	private void addOnLongClickListener(View view, final int position) {
		final Contact c = contacts.get(position);
		Log.i("Adding on long click listener", "adding on long click listener");
		view.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				Log.i("on long click listener",
						" on long click listener called");
				final Dialog d = new Dialog(context);
				d.setTitle("Contact Options...");
				d.setContentView(R.layout.usercontact_unlink);
				if (!Utils.isNullStringOrEmpty(c.getLivelinkId())) {
					CheckBox cb = (CheckBox) d.findViewById(R.id.unlinkRequest);
					cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							String result = HttpRequestManager.doRequest(
									Settings.getLivelinkUrl(),
									Settings.makeUnlinkParameters(c));
							Toast.makeText(context, result, Toast.LENGTH_SHORT)
									.show();
							d.dismiss();
							// update the activity
							if (context instanceof PhonebookActivity) {
								((PhonebookActivity) context).refresh();
							}
							// update the contact merge
							if (result.toLowerCase().contains("success")) {
								c.setLivelinkId(null);
								MimiProtectGeneralManager.updateContact(c);
							}
						}
					});
				}
				CheckBox cb = (CheckBox) d.findViewById(R.id.fastDial);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// add the fast dials.
						FastDialContacts dialContacts = Settings
								.getFastDialContacts(context);
						if (dialContacts == null) {
							dialContacts = new FastDialContacts();
						}
						dialContacts.getFastDials().add(c);
						Settings.setPreference(
								context,
								FastDialContacts.FAST_DIAL_CONTACT_PREFERENCE_ID,
								dialContacts.toString());
						Toast.makeText(
								context,
								"Contact successfully added as fast dial."
										+ "\nIf you have not added the on screen widget, you can add it now."
										+ "\nIt may take awhile before the widget updates with this new information.\n"
										+ "Also not that currently maximum of three contacts are supported for fast dial.",
								Toast.LENGTH_LONG).show();
						d.dismiss();
					}
				});
				d.show();
				return true;
			}
		});
	}
}
