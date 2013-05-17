package com.variance.mimiprotect.contacts.selection;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class ContactGeneralSelectionManager extends
		AsyncTask<SearchParameter, Void, HttpResponseData> {
	private List<Contact> contacts;
	private boolean multiSelection;
	private ArrayAdapter<Contact> currentAdapter;
	private Context context;
	private OnContactSelectionComplete onContactSelectionComplete;
	private SearchParameter searchParameter;
	private String title;
	private String okButtonTxt;
	private boolean initialized;
	private Dialog contactViewDialog;
	private List<Contact> selectedContacts;
	private boolean selectAllContacts;
	private CheckBox selectAllContactsCb;

	public ContactGeneralSelectionManager(boolean multiSelection,
			Context context, String title, String okButtonText,
			OnContactSelectionComplete onContactSelectionComplete) {
		super();
		this.multiSelection = true;
		this.context = context;
		this.onContactSelectionComplete = onContactSelectionComplete;
		this.title = title;
		this.okButtonTxt = okButtonText;
		this.contactViewDialog = new Dialog(context);
		this.selectedContacts = new ArrayList<Contact>();
	}

	public OnContactSelectionComplete getOnContactSelectionComplete() {
		return onContactSelectionComplete;
	}

	public void setOnContactSelectionComplete(
			OnContactSelectionComplete onContactSelectionComplete) {
		this.onContactSelectionComplete = onContactSelectionComplete;
	}

	private ContactGeneralMultiSelectionAdapter getContactGeneralMultiSelectionAdapter() {
		if (multiSelection) {
			return (ContactGeneralMultiSelectionAdapter) currentAdapter;
		}
		return null;
	}

	public boolean isSelectAllContacts() {
		return selectAllContacts;
	}

	public void setSelectAllContacts(boolean selectAllContacts) {
		this.selectAllContacts = selectAllContacts;
		selectAllContactsCb.setChecked(this.selectAllContacts);
	}

	private void update() {
		this.currentAdapter = new ContactGeneralMultiSelectionAdapter(context,
				R.layout.usercontact_singleselectionview, contacts, this);
		if (selectAllContacts) {
			selectedContacts.addAll(contacts);
		}
		ListView contactListView = (ListView) contactViewDialog
				.findViewById(R.id.usercontact_selectionOption_View);
		contactListView.setAdapter(currentAdapter);
	}

	private void init() {
		this.currentAdapter = new ContactGeneralMultiSelectionAdapter(context,
				R.layout.usercontact_singleselectionview, contacts, this);
		contactViewDialog.setTitle(title);
		contactViewDialog.setContentView(R.layout.usercontact_selectionoption);
		View titleView = contactViewDialog.getWindow().findViewById(
				android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView
						.setBackgroundResource(R.drawable.mimi_connect_background);
			}
		}
		contactViewDialog.setCancelable(true);
		// set ok button options
		ListView contactListView = (ListView) contactViewDialog
				.findViewById(R.id.usercontact_selectionOption_View);
		contactListView.setAdapter(currentAdapter);
		// add the button and check box options
		selectAllContactsCb = (CheckBox) contactViewDialog
				.findViewById(R.id.usercontact_selectAllContactCb);
		selectAllContactsCb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							getContactGeneralMultiSelectionAdapter()
									.selectAllContacts();
						} else {
							getContactGeneralMultiSelectionAdapter()
									.deselectAllContacts();
						}
					}
				});
		Button okBtn = (Button) contactViewDialog
				.findViewById(R.id.usercontact_onSelectionComplete);
		okBtn.setText(okButtonTxt);
		okBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (onContactSelectionComplete != null) {
					onContactSelectionComplete
							.contactSelected(getContactGeneralMultiSelectionAdapter()
									.getSelectedContacts());
					Log.i("Selected Contacts: ",
							getContactGeneralMultiSelectionAdapter()
									.getSelectedContacts().toString());
				}
			}
		});
		initControllers(contactViewDialog);
		initialized = true;
		contactViewDialog.show();
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	private void doLoadPersonalContactsForSelection() {
		Log.i("SearchParameter: ", searchParameter.toString());
		PersonalPhonebookActivity.showProgress("Please wait", context, this);
		System.gc();
		onPostExecute(HttpRequestManager.doRequestWithResponseData(
				Settings.getSearchContactUrl(),
				Settings.makeLoadAllPersonalContactsParameter(searchParameter)));
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		PersonalPhonebookActivity.endProgress();
		if (result != null
				&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			contacts = DataParser.getPersonalContacts(result.getMessage());
			if (!initialized) {
				init();
			} else {
				update();
			}
		}
	}

	private void initControllers(Dialog d) {
		final EditText searchContact = (EditText) d
				.findViewById(R.id.txtSearchGeneralPersonalContact);
		searchContact.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						String searchText = searchContact.getText().toString();
						Log.i("Search Personal Contact: ", searchText);
						searchParameter.setSearchTerm(searchText);
						searchParameter.setCurrentPage(0);
						doLoadPersonalContactsForSelection();
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		Button btnSearchContacts = (Button) d
				.findViewById(R.id.btnSearchContacts);
		btnSearchContacts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String searchText = searchContact.getText().toString();
				Log.i("Search Personal Contact: ", searchText);
				searchParameter.setSearchTerm(searchText);
				searchParameter.setCurrentPage(0);
				doLoadPersonalContactsForSelection();
			}
		});
		Button btnLoadPreviousFromPersonalListContact = (Button) d
				.findViewById(R.id.btnLoadPreviousFromContactList);
		btnLoadPreviousFromPersonalListContact
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (searchParameter != null
								&& searchParameter.getCurrentPage() > 0) {
							searchParameter.decrementPage();
							doLoadPersonalContactsForSelection();
						}
					}
				});
		Button btnLoadNextFromPersonalListContact = (Button) d
				.findViewById(R.id.btnLoadNextFromContactList);
		btnLoadNextFromPersonalListContact
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (searchParameter != null
								&& searchParameter.getCurrentPage() < searchParameter
										.getMaxPage()) {
							searchParameter.incrementPage();
							doLoadPersonalContactsForSelection();
						}
					}
				});
		d.show();
	}

	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Loading contacts...", context,
				this);
	}

	@Override
	protected HttpResponseData doInBackground(SearchParameter... params) {
		// We load all the personal contacts
		searchParameter = params[0];
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getSearchContactUrl(),
				Settings.makeLoadAllPersonalContactsParameter(searchParameter));
	}
}