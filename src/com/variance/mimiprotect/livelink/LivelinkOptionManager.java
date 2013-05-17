package com.variance.mimiprotect.livelink;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class LivelinkOptionManager {
	private Activity context;
	private LivelinkOptionAdapter adapter;
	private boolean showAsDialog;
	private Dialog dialog;
	private SearchParameter searchParameter;

	public LivelinkOptionManager(Activity context) {
		super();
		this.context = context;
		this.searchParameter = new SearchParameter();
	}

	public LivelinkOptionManager(Activity context, SearchParameter parameter) {
		super();
		this.context = context;
		this.searchParameter = parameter;
	}

	public LivelinkOptionManager(Activity context, boolean showAsDialog) {
		super();
		this.context = context;
		this.showAsDialog = showAsDialog;
		this.dialog = new Dialog(this.context);
	}

	private void initView() {
		// Add dialog to select the various contacts for performing live
		// linking.
		if (showAsDialog) {
			dialog.setContentView(R.layout.usercontact_livelinkoption);
			dialog.setTitle("Select Contacts For Livelinking");
		} else {
			context.setContentView(R.layout.usercontact_livelinkoption);
		}
		if (showAsDialog) {
			dialog.show();
		}
	}

	public void doShowContactForLivelinkOption() {
		initView();
		loadContactsForSelection(dialog, searchParameter);

	}

	public void doShowContactOnMimiForLivelinkOptionAfterSignup(
			ArrayList<Contact> personalContacts) {
		initView();
		showContactsForLivelink(personalContacts);
	}

	private View getView(int id) {
		return showAsDialog ? dialog.findViewById(id) : context
				.findViewById(id);
	}

	private void loadContactsForSelection(Dialog dg,
			SearchParameter searchParameter) {
		HttpRequestTaskListener<SearchParameter, String> listener = new HttpRequestTaskListener<SearchParameter, String>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(String result) {
				ArrayList<Contact> personalContacts = DataParser
						.getPersonalContacts(result);
				showContactsForLivelink(personalContacts);
			}

			public String doTask(SearchParameter... params) {
				SearchParameter searchParameter = params[0];
				return HttpRequestManager.doRequest(Settings
						.getSearchContactUrl(), Settings
						.makePersonalContactSearchParameter(searchParameter));
			}
		};
		new HttpRequestTask<SearchParameter, Void, String>(listener,
				"Please wait, loading contacts", context)
				.execute(new SearchParameter[] { searchParameter });
	}

	public void showContactsForLivelink(ArrayList<Contact> personalContacts) {
		if (personalContacts != null && !personalContacts.isEmpty()) {
			adapter = new LivelinkOptionAdapter(context,
					R.layout.usercontact_livelinkoption_singleview,
					personalContacts);
			ListView contactListView = (ListView) getView(R.id.usercontact_livelinkoption_View);
			contactListView.setAdapter(adapter);
			// add the button and check box options
			CheckBox selectAllContactsCb = (CheckBox) getView(R.id.usercontact_livelinkoption_selectAllContactCb);
			selectAllContactsCb
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								adapter.selectAllContacts();
							} else {
								adapter.deselectAllContacts();
							}
						}
					});
			LinearLayout livelinkBtn = (LinearLayout) getView(R.id.usercontact_livelinkBtn);
			livelinkBtn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// do the livelink
					if (adapter.getSelectedContacts().isEmpty()) {
						Toast.makeText(
								context,
								"You must select at least one contact for livelinking",
								Toast.LENGTH_LONG).show();
						return;
					}
					doLivelink();
				}
			});
		}
	}

	private void doLivelink() {
		HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(HttpResponseData result) {
				if (result != null) {
					Toast.makeText(context, result.toString(),
							Toast.LENGTH_LONG).show();
					Log.i("Livelink result: ", result.toString());
				} else {
					Toast.makeText(context,
							"Did not receive result from server",
							Toast.LENGTH_LONG).show();
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(Settings
						.getLivelinkUrl(), Settings
						.makeLivelinkRequestParameter(adapter
								.getSelectedContacts()));
			}
		};
		new HttpRequestTask<Void, Void, HttpResponseData>(listener,
				"Please wait. livelinking...", context).execute(new Void[] {});
	}
}
