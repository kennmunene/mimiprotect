package com.variance.mimiprotect.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.selection.OnContactSelectionComplete;
import com.variance.mimiprotect.livelink.LivelinkOptionManager;
import com.variance.mimiprotect.livelink.LivelinkRequestTask;
import com.variance.mimiprotect.livelink.LivelinkViewManager;
import com.variance.mimiprotect.livelink.util.LiveLinkRequest;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;

public class LiveLinkRequestsActivity extends PhonebookActivity implements
		OnContactSelectionComplete {
	private List<LiveLinkRequest> requests = new ArrayList<LiveLinkRequest>();
	private LivelinkRequestTask livelinkRequestTask;
	private List<Contact> selectedContacts;
	private EditText searchTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLivelinkOption();
	}

	private void showLivelinkOption() {
		this.selectedContacts = new ArrayList<Contact>();
		setContentView(R.layout.usercontact_livelinkoption);
		searchTxt = (EditText) findViewById(R.id.txtSearchPersonalContact);
		initSoftKey();
		doSearch();
	}

	public void handleSelectAll(View view) {
		// find the button
		CheckBox cbn = (CheckBox) view
				.findViewById(R.id.usercontact_livelinkoption_selectAllContactCb);
		if (cbn != null) {
			if (cbn.isChecked()) {
				cbn.setChecked(false);
				livelinkRequestTask.deselectAllContacts();
			} else {
				cbn.setChecked(true);
				livelinkRequestTask.selectAllContacts();
			}
		}
	}

	public void handleContactOptions(View view) {
		livelinkRequestTask.doLivelink();
	}

	private void showLivelinkOptionOnDialog() {
		new LivelinkOptionManager(LiveLinkRequestsActivity.this, true)
				.doShowContactForLivelinkOption();
	}

	private void checkLivelinkRequests() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return addLivelinkTab();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					setContentView(R.layout.usercontact_livelinkrequest);
					getLiveLinkRequests();
				} else {
					showLivelinkOption();
				}
			}

		}.execute();
	}

	private boolean addLivelinkTab() {
		HttpResponseData data = HttpRequestManager.doRequestWithResponseData(
				Settings.getLiveLinkRequestsDownloadURL(),
				Settings.makeLivelinkRequestAvailableParameter());
		return data != null
				&& data.getResponseStatus() == HttpResponseStatus.SUCCESS
				&& Boolean.parseBoolean(data.getMessage().trim());
	}

	@Override
	protected void onResume() {
		checkLivelinkRequests();
		super.onResume();
	}

	public void refresh() {
		checkLivelinkRequests();
	}

	public void getLiveLinkRequests() {
		String url = Settings.getLiveLinkRequestsDownloadURL();
		String livelinkStr = HttpRequestManager.doRequest(url,
				Settings.makeLivelinkRequestParameter());
		requests = DataParser.getLivelinkRequestFrom(livelinkStr);
		new LivelinkViewManager(this, requests).initialize();
	}

	private void initSoftKey() {
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						String searchText = searchTxt.getText().toString();
						Log.i("Search Personal Contact: ", searchText);
						searchParameter.setSearchTerm(searchText);
						searchParameter.setCurrentPage(0);
						selectedContacts.clear();
						doSearch();
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
	}

	public void requestLivelinkToContacts(View view) {
		switch (view.getId()) {
		case R.id.btnLivelinkToContacts:
			showLivelinkOptionOnDialog();
			break;
		}
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
							Toast.makeText(LiveLinkRequestsActivity.this,
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

	public void contactSelected(List<Contact> selectedContacts) {

	}

	public void search() {
		String searchText = searchTxt.getText().toString();
		Log.i("Search Personal Contact: ", searchText);
		searchParameter.setSearchTerm(searchText);
		searchParameter.setCurrentPage(0);
		selectedContacts.clear();
		doSearch();
	}

	@Override
	protected void doSearch() {
		super.doSearch();
		if (!isLoadedFromCache()) {
			livelinkRequestTask = new LivelinkRequestTask(this,
					searchParameter, selectedContacts);
			livelinkRequestTask.execute();
		}
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
		livelinkRequestTask = new LivelinkRequestTask(this, searchParameter,
				selectedContacts);
		livelinkRequestTask.onContactedLoadedFormCache(contacts);
	}

	@Override
	public PhonebookType getType() {
		// technically, this is a private phonebook.
		return PhonebookType.PRIVATE;
	}

	private void addViewMyLivelinkOption(Menu menu) {
		MenuItem addViewMyLivelinkOption = menu.add("My Livelinks");
		addViewMyLivelinkOption.setIcon(R.drawable.mimi_connect_livelink);
		addViewMyLivelinkOption
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						if (Settings.isLoggedIn()) {
							PhonebookActivity.startGeneralActivity(
									LiveLinkRequestsActivity.this,
									"My Livelinks", MyLivelinkActivity.class,
									R.layout.usercontact_tabview, false);
						} else {
							Toast.makeText(
									LiveLinkRequestsActivity.this,
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
