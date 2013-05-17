package com.variance.mimiprotect.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.business.directory.BusinessDirectoryActivity;
import com.variance.mimiprotect.chat.ChatManagerActivity;
import com.variance.mimiprotect.chat.ChatManagerTabActivity;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.BusinessContactActivity;
import com.variance.mimiprotect.contacts.business.NewBusinessActivity;
import com.variance.mimiprotect.contacts.business.settings.BusinessContactConstants;
import com.variance.mimiprotect.contacts.cache.ContactsStore;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.contact.DiscoveryActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UserSetting;
import com.variance.mimiprotect.util.Utils;

public abstract class PhonebookActivity extends MimiProtectActivity {
	protected SearchParameter searchParameter;
	private ContactsStore cache;
	private String previousSearchTerm;

	public static void startGeneralActivity(Activity context, String title,
			Class<?> activityClass) {
		startGeneralActivity(context, title, activityClass,
				R.layout.usercontact_whitetitled_tabview);
	}

	public static void startGeneralActivity(Activity context, String title,
			Class<?> activityClass, int tabview_layout) {
		startGeneralActivity(context, title, activityClass, tabview_layout,
				true);
	}

	public static void startGeneralActivity(Activity context, String title,
			Class<?> activityClass, int tabview_layout,
			boolean finishCurrentActivity) {
		Intent intent = new Intent(context, GeneralTabActivity.class);
		Intent callingintent = context.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				activityClass.getName());
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
				tabview_layout);
		// if (finishCurrentActivity) {
		// context.finish();
		// }
		context.startActivity(intent);
	}

	public static void startGeneralActivity(Activity context, String title,
			Class<?> activityClass, int tabview_layout,
			boolean finishCurrentActivity,
			Map<String, ? extends Serializable> forwadedEXtras) {
		Intent intent = new Intent(context, GeneralTabActivity.class);
		Intent callingintent = context.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				activityClass.getName());
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
				tabview_layout);
		for (Map.Entry<String, ? extends Serializable> e : forwadedEXtras
				.entrySet()) {
			intent.putExtra(e.getKey(), e.getValue());
		}
		context.startActivity(intent);
	}

	public static void startGeneralActivity(Activity context, String title,
			Class<? extends TabActivity> tabActivutyClass,
			Class<?> activityClass, int tabview_layout,
			boolean finishCurrentActivity,
			Map<String, ? extends Serializable> forwadedEXtras) {
		Intent intent = new Intent(context, tabActivutyClass);
		Intent callingintent = context.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				activityClass.getName());
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
				tabview_layout);
		for (Map.Entry<String, ? extends Serializable> e : forwadedEXtras
				.entrySet()) {
			intent.putExtra(e.getKey(), e.getValue());
		}
		context.startActivity(intent);
	}

	public static void startChatManagerActivity(Activity context, String title,
			int tabview_layout, boolean finishCurrentActivity,
			Map<String, ? extends Serializable> forwadedEXtras) {
		Intent intent = new Intent(context, ChatManagerTabActivity.class);
		Intent callingintent = context.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				ChatManagerActivity.class.getName());
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
				tabview_layout);
		for (Map.Entry<String, ? extends Serializable> e : forwadedEXtras
				.entrySet()) {
			intent.putExtra(e.getKey(), e.getValue());
		}
		context.startActivity(intent);
	}

	private boolean loadedFromCache;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searchParameter = new SearchParameter();
		searchParameter.setMaxResult(PersonalPhonebookActivity
				.getMaximumListRows(this) + 5);
	}

	@Override
	protected void onResume() {
		super.onResume();
		doBackgroundSearch();
	}

	public void refresh() {
		doBackgroundSearch();
	}

	public void handleShowPhonebook(View view) {
		startGeneralActivity(this, "My Phonebook",
				PersonalPhonebookActivity.class, R.layout.usercontact_tabview);
	}

	public void handleShowOfficePhonebook(View view) {
		String officeBusinessname = getBusinessName();
		Log.i("officeBusinessname", officeBusinessname + "");
		if (hasBusinessContacts()
				|| (MimiProtectGeneralManager.hasCurrentPhoneLock() && MimiProtectGeneralManager
						.getUserSettingOverride().isBusinessPhonebookUser())) {
			if (MimiProtectGeneralManager.hasCurrentPhoneLock()
					&& officeBusinessname == null
					&& (MimiProtectGeneralManager.getUserSettingOverride()
							.getBusinessPhonebookName() != null && MimiProtectGeneralManager
							.getUserSettingOverride().isBusinessPhonebookUser())) {
				officeBusinessname = MimiProtectGeneralManager
						.getUserSettingOverride().getBusinessPhonebookName();
			}
			// update user if locked.
			if (MimiProtectGeneralManager.hasCurrentPhoneLock()
					&& !MimiProtectGeneralManager.getUserSetting()
							.isBusinessPhonebookUser()) {
				UserSetting setting = MimiProtectGeneralManager
						.getUserSetting();
				setting.setBusinessPhonebookUser(true);
				setting.setBusinessPhonebookName(officeBusinessname);
				MimiProtectGeneralManager.updateUserSetting();
			}
			startGeneralActivity(this, officeBusinessname,
					BusinessContactActivity.class, R.layout.usercontact_tabview);
		} else {
			// get the user contacts
			startGeneralActivity(this, officeBusinessname,
					NewBusinessActivity.class, R.layout.usercontact_tabview);
		}
	}

	public void handleShowDirectory(View view) {
		if (Settings.isLoggedIn()) {
			startGeneralActivity(this, "Business Directory",
					BusinessDirectoryActivity.class,
					R.layout.usercontact_tabview);
		} else {
			Toast.makeText(this,
					"Sorry! You must be logged in to view business listings",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void handleLivelink(View view) {
		if (Settings.isLoggedIn()) {
			// Livelink tab does not have options to access the other previous
			// phonebooks.
			// we therefore do not finish the current activity
			startGeneralActivity(this, "Livelink",
					LiveLinkRequestsActivity.class,
					R.layout.usercontact_tabview);
		} else {
			Toast.makeText(
					this,
					"Sorry! You must be logged in to view or send livelink requests.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void handleContactDiscovery(View view) {
		if (Settings.isLoggedIn()) {
			startGeneralActivity(this, "Contacts Connections",
					DiscoveryActivity.class, R.layout.usercontact_tabview);
		} else {
			Toast.makeText(
					this,
					"Sorry! You must be logged in to view or send livelink requests.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void updateSearchParameter() {
		try {
			if (this.previousSearchTerm == null
					|| !this.previousSearchTerm.equals(searchParameter
							.getSearchTerm())) {
				if (searchParameter.getSearchTerm() == null
						|| "".equals(searchParameter.getSearchTerm().trim())) {
					searchParameter
							.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
				}
				if (searchParameter.getSearchTerm() != null
						&& !searchParameter.getSearchTerm().equals(
								Settings.INIT_ANDROID_LOAD_TXT)) {
					this.searchParameter
							.setMaxRecords(maxPersonalRecordsForSearch(searchParameter
									.getSearchTerm()));
				} else {
					this.searchParameter.setMaxRecords(maxAESPersonalRecords());
				}
				Log.i("SearchParameter: ", searchParameter.toString());
			}
		} finally {
			previousSearchTerm = searchParameter.getSearchTerm();
		}
	}

	public static void updateSearchParameterForPersonalContacts(
			SearchParameter searchParameter) {
		searchParameter.setMaxRecords(maxPersonalRecords());
	}

	public static int maxPersonalRecords() {
		HttpResponseData recordResults = HttpRequestManager
				.doRequestWithResponseData(Settings.getSearchContactUrl(),
						Settings.makeCountPersonalContactsParameter());
		if (recordResults != null
				&& recordResults.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String num = recordResults.getMessage();
			int record = Integer.parseInt(num.trim());
			return record;
		}
		return -1;
	}

	public static int maxAESPersonalRecords() {
		HttpResponseData recordResults = HttpRequestManager
				.doRequestWithResponseData(Settings.getSearchContactUrl(),
						Settings.makeAESCountPersonalContactsParameter());
		if (recordResults != null
				&& recordResults.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String num = recordResults.getMessage();
			int record = Integer.parseInt(num.trim());
			return record + 5;
		}
		return -1;
	}

	public static int countBusinessRecords() {
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

	public static int maxPersonalRecordsForSearch(String searchTerm) {
		HttpResponseData recordResults = HttpRequestManager
				.doRequestWithResponseData(
						Settings.getSearchContactUrl(),
						Settings.makeCountPersonalContactsForSearchParameter(searchTerm));
		if (recordResults != null
				&& recordResults.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String num = recordResults.getMessage();
			int record = Integer.parseInt(num.trim());
			Log.i("Max Records: ", num);
			return record + 5;
		}
		return -1;
	}

	public void handleSearchPrevious(View view) {
		Log.i("SearchPrevious", "Searching previous");
		if (searchParameter != null && searchParameter.getCurrentPage() > 0) {
			searchParameter.decrementPage();
			doSearch();
		}
	}

	public void handleSearch(View view) {
		search();
	}

	public abstract void search();

	public void handleSearchNext(View view) {
		Log.i("SearchNext", "Searching next");
		if (searchParameter != null && !searchParameter.isMaxPage()) {
			searchParameter.incrementPage();
			doSearch();
		}
	}

	public SearchParameter getSearchParameter() {
		return searchParameter;
	}

	public boolean isLoadedFromCache() {
		return loadedFromCache;
	}

	protected boolean doLoadFromCache() {
		return (searchParameter.getSearchTerm() == null || searchParameter
				.getSearchTerm().trim().equals(""));
	}

	private void clearDummyContacts(List<Contact> contacts) {
		for (ListIterator<Contact> it = contacts.listIterator(); it.hasNext();) {
			Contact c = it.next();
			if (c.isDummyContac() || Utils.isNullStringOrEmpty(c.getId())) {
				it.remove();
			}
		}
	}

	private void loadFromCache() {
		loadedFromCache = false;
		if (MimiProtectGeneralManager.hasCurrentPhoneLock()
				&& (!Settings.isLoggedIn() || doLoadFromCache())) {
			try {
				cache = ContactsStore.getInstance(this);
				PhonebookType type = getType();
				Log.i("Loading from cache for:", type.name());
				if (type != null) {
					ArrayList<Contact> cached = null;
					switch (type) {
					case PRIVATE:
						if (!Utils.isNullStringOrEmpty(searchParameter
								.getSearchTerm())
								&& !searchParameter.getSearchTerm().equals(
										Settings.INIT_ANDROID_LOAD_TXT)) {
							cached = cache.getPersonalContacts(searchParameter
									.getSearchTerm());
						}
						if (cached == null || cached.isEmpty()) {
							cached = cache.getPersonalContacts();
						}
						break;
					case OFFICE:
						if (!Utils.isNullStringOrEmpty(searchParameter
								.getSearchTerm())
								&& !searchParameter
										.getSearchTerm()
										.equals(BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM)) {
							cached = cache.getOfficeContacts(searchParameter
									.getSearchTerm());
						}
						if (cached == null || cached.isEmpty()) {
							cached = cache.getOfficeContacts();
						}
						break;
					case PUBLIC:
						cached = cache.getPublicContacts();
						break;
					}
					clearDummyContacts(cached);
					if (cached != null && !cached.isEmpty()) {
						Log.i("onContactLoadedFromCache:" + type,
								"" + cached.size());
						loadedFromCache = !cached.isEmpty();
						// arrange this contacts
						onContactLoadedFromCache(cached);
					}
				}
			} finally {
				if (cache != null) {
					cache.close();
				}
			}
		}
	}

	protected void doBackgroundSearch() {
		loadFromCache();
		if (!isLoadedFromCache()) {
			updateSearchParameter();
		}
	}

	protected void doSearch() {
		loadFromCache();
		if (!isLoadedFromCache()) {
			updateSearchParameter();
		}
	}

	protected boolean hasBusinessContacts() {
		return DashBoardActivity.DASH_BOARD_ACTIVITY != null
				&& DashBoardActivity.DASH_BOARD_ACTIVITY
						.isHasBusinessPhonebook();
	}

	protected String getBusinessName() {
		return DashBoardActivity.DASH_BOARD_ACTIVITY != null ? DashBoardActivity.DASH_BOARD_ACTIVITY
				.getBusinessName() : "office Phonebook";
	}

	protected abstract void onContactLoadedFromCache(List<Contact> contacts);

	public abstract PhonebookType getType();
}
