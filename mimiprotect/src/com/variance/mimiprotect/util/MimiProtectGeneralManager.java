package com.variance.mimiprotect.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.contacts.business.settings.BusinessContactConstants;
import com.variance.mimiprotect.contacts.cache.ContactsStore;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.xml.VDocument;

public final class MimiProtectGeneralManager {
	private static volatile UserSetting userSetting;
	private static UserSetting DEFAULT_USER_SETTING;
	private static User currentUser;
	private static Activity context;
	private static final String PREFERENCE_USER_SETTING_ID = "USERSETTINGS_02832829";
	private static final String PREFERENCE_USER_INFORMATION_ID = "USERINFORMATION_02832829";

	public static void clearSettings() {
		userSetting = null;
		currentUser = null;
	}

	/**
	 * This initialization must be called after the user has logged in.
	 * 
	 * @param context
	 */
	public static void init(Activity context) {
		MimiProtectGeneralManager.context = context;
		// load user setting from server. Also load user profile from server.
		// if necessary, also update the cache.
		synchronized (MimiProtectGeneralManager.class) {
			try {
				// do the loading in a synchronized block
				loadUserProfile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void initUserSetting(Activity context) {
		MimiProtectGeneralManager.context = context;
		// load user setting from server. Also load user profile from server.
		// if necessary, also update the cache.
		synchronized (MimiProtectGeneralManager.class) {
			try {
				loadUserSetting();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void initializeDefaults(Activity context) {
		MimiProtectGeneralManager.context = context;
		// load user setting from server. Also load user profile from server.
		// if necessary, also update the cache.
		synchronized (MimiProtectGeneralManager.class) {
			try {
				loadUserSetting();
				loadDefaultUserProfile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void clearSettingAndCacheIfAny() {

	}

	public static void initAll(Activity context) {
		MimiProtectGeneralManager.context = context;
		// load user setting from server. Also load user profile from server.
		// if necessary, also update the cache.
		synchronized (MimiProtectGeneralManager.class) {
			try {
				// do the loading in a synchronized block
				// this calls should be in this order.
				loadUserSetting();
				loadUserProfile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean hasCurrentPhoneLock() {
		return hasLock();
	}

	/**
	 * Has accessibility due to the fact that the user has not set his/her
	 * settings.
	 * 
	 * @return
	 */
	public static boolean hasImplicitAccessibility() {
		boolean hasAccessibility = currentUser != null
				&& currentUser.getUserName() != null
				&& hasDefaultAccessibility();
		return hasAccessibility;
	}

	/**
	 * Has accessibility due to the fact that the user has not set his/her
	 * settings. In this case, the phone is locked only due to the fact the user
	 * has not logged in, and yet the default setting has been specified. To
	 * avoid corruption of default setting, we refuse some privilege access.
	 * 
	 * @return
	 */
	public static boolean hasDefaultAccessibility() {
		boolean hasAccessibility = DEFAULT_USER_SETTING != null
				&& DEFAULT_USER_SETTING.getUsername().equals("username");
		return hasAccessibility;
	}

	public static boolean hasExplicitAccessibility() {
		boolean hasAccessibility = currentUser != null
				&& currentUser.getUserName() != null
				&& DEFAULT_USER_SETTING != null
				&& DEFAULT_USER_SETTING.getUsername().equals(
						currentUser.getUserName());
		return hasAccessibility;
	}

	/**
	 * Has accessibility due to the fact the the user has set her/his settings
	 * 
	 * @return
	 */
	public static boolean hasAccessibility() {
		boolean hasAccessibility = hasImplicitAccessibility()
				|| hasExplicitAccessibility();
		return hasAccessibility;
	}

	public static boolean hasLock() {
		boolean hasLock = hasAccessibility()
				&& DEFAULT_USER_SETTING.isLockMimiProtectToCurrentPhone();
		return hasLock;
	}

	public static void onNewContactAdded(PhonebookActivity activity) {
		if (hasCurrentPhoneLock() && getUserSetting().isAllowLocalCache()) {
			ContactsStore cache = ContactsStore.getInstance(context);
			try {
				try {
					PhonebookType type = activity.getType();
					if (type != null) {
						switch (type) {
						case OFFICE:
							syncOfficePhonebook();
							break;
						case PRIVATE:
							syncPrivatePhonebook();
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				cache.close();
			}
		}
	}

	public static void clearCache() {
		ContactsStore cache = ContactsStore.getInstance(context);
		try {
			cache.clearCache();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cache.close();
		}
	}

	public static void onNewContactAdded(PhonebookType type) {
		if (getUserSetting().isAllowLocalCache()) {
			ContactsStore cache = ContactsStore.getInstance(context);
			try {
				if (type != null) {
					switch (type) {
					case OFFICE:
						syncOfficePhonebook();
						break;
					case PRIVATE:
						syncPrivatePhonebook();
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cache.close();
			}
		}
	}

	public static void onNewContactAdded(Contact contact, PhonebookType type) {
		if (getUserSetting().isAllowLocalCache()) {
			ContactsStore cache = ContactsStore.getInstance(context);
			try {
				try {
					if (type != null) {
						switch (type) {
						case OFFICE:
							cache.saveOfficeContact(contact);
							break;
						case PRIVATE:
							cache.savePersonalContact(contact);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				cache.close();
			}
		}
	}

	private static void loadUserSetting() {
		// this two must be different instances.
		DEFAULT_USER_SETTING = getSavedUserSetting();
		try {
			userSetting = (DEFAULT_USER_SETTING != null) ? DEFAULT_USER_SETTING
					.clone() : null;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private static UserSetting getSavedUserSetting() {
		String loadUserSettingStr = Settings.getPreference(context,
				PREFERENCE_USER_SETTING_ID);
		if (loadUserSettingStr != null) {
			try {
				Log.e("getSavedUserSetting", loadUserSettingStr + "");
				VObjectMarshaller<UserSetting> m = new VObjectMarshaller<UserSetting>(
						UserSetting.class);
				VDocument doc = VDocument
						.parseDocumentFromString(loadUserSettingStr);
				UserSetting setting = m.unmarshall(doc);
				return setting;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new UserSetting();
	}

	private static void syncOfficePhonebook() {
		HttpRequestTaskListener<Void, String> listener = new HttpRequestTaskListener<Void, String>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(String result) {
				if (result != null) {
					final ArrayList<Contact> contacts = DataParser
							.getBusinessContacts(result);
					Log.i("syncOfficePhonebook:", result);
					if (contacts != null && !contacts.isEmpty()) {
						// cache personal contacts
						ContactsStore cache = ContactsStore
								.getInstance(context);
						// cache.clearDatabase();
						try {
							int saved = 0;
							for (Contact c : contacts) {
								try {
									if (cache.saveOfficeContact(c)) {
										saved++;
									}
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
							Log.i("syncOfficePhonebook:", saved + "");
						} finally {
							cache.close();
						}
					}
				}
			}

			public String doTask(Void... params) {
				if (!Settings.isDebugging()) {
					SearchParameter searchParameter = new SearchParameter(
							BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM,
							0, PersonalPhonebookActivity.getMaximumListRows(
									context, 30));
					return HttpRequestManager
							.doRequest(
									Settings.getBusinessContactUrl(),
									Settings.makeLoadBusinessContactParameters(searchParameter));
				}
				return null;
			}
		};
		new HttpRequestTask<Void, Void, String>(listener, "Loading profile...",
				context).executeInBackground();

	}

	public static void syncCacheIfNecessary() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// load all personal contacts
				Log.i("Checking cache status", "loadCacheIfNecessary");
				if (hasCurrentPhoneLock() && userSetting != null
						&& userSetting.isAllowLocalCache()) {
					// get the date
					Calendar now = Calendar.getInstance();
					Calendar lastSyncDate = userSetting.getLastCacheUpdate();
					if (lastSyncDate != null) {
						now.set(Calendar.DAY_OF_YEAR,
								now.get(Calendar.DAY_OF_YEAR)
										- userSetting.getCacheUpdatePeriod());
						if (lastSyncDate.before(now)) {
							syncPrivatePhonebook();
							syncOfficePhonebook();
							userSetting.setLastCacheUpdate(Calendar
									.getInstance());
							updateUserSetting();
						}
					}
				}
				return null;
			}

		}.execute();
	}

	private static void syncPrivatePhonebook() {
		HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(HttpResponseData result) {
				if (result != null
						&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
					ArrayList<Contact> contacts = DataParser
							.getPersonalContacts(result.getMessage());
					Log.i("loadCacheIfNecessary:", contacts.size() + "");
					if (contacts != null && !contacts.isEmpty()) {
						// cache personal contacts
						ContactsStore cache = ContactsStore
								.getInstance(context);
						cache.clearDatabase();
						try {
							int saved = 0;
							for (Contact c : contacts) {
								try {
									if (cache.savePersonalContact(c)) {
										saved++;
									}
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
							Log.i("loadCacheIfNecessary:", saved + "");
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							cache.close();
						}
					}
				}
			}

			public HttpResponseData doTask(Void... params) {
				if (!Settings.isDebugging()) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getSearchContactUrl(),
							Settings.makeLoadAllPersonalContactsParameter(),
							context);
				}
				return null;
			}
		};
		new HttpRequestTask<Void, Void, HttpResponseData>(listener,
				"Loading profile...", context).executeInBackground();
	}

	public synchronized static void loadDefaultUserProfile() {
		String userInformationStr = Settings.getPreference(context,
				PREFERENCE_USER_INFORMATION_ID);
		if (userInformationStr != null) {
			Log.e("loadDefaultUserProfile", userInformationStr);
			try {
				VObjectMarshaller<User> m = new VObjectMarshaller<User>(
						User.class);
				VDocument doc = VDocument
						.parseDocumentFromString(userInformationStr);
				currentUser = m.unmarshall(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadUserProfile() {
		HttpRequestTaskListener<Void, String> listener = new HttpRequestTaskListener<Void, String>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(String result) {
				if (result != null) {
					User user = DataParser.getUserFrom(result);
					Log.i("loadUserProfile", result + "");
					Log.i("loadUserProfile", user + "");
					if (user != null) {
						synchronized (MimiProtectGeneralManager.class) {
							currentUser = user;
						}
						if (hasAccessibility()) {
							updateUserInformation();
						}
					}
				}
				syncCacheIfNecessary();
			}

			public String doTask(Void... params) {
				if (!Settings.isDebugging()) {
					return HttpRequestManager.doRequest(
							Settings.getProfileRequestUrl(),
							Settings.getProfileRequestLoadParameter());
				}
				return null;
			}
		};
		new HttpRequestTask<Void, Void, String>(listener, "Loading profile...",
				context).executeInBackground();
	}

	public synchronized static UserSetting getUserSetting() {
		if (userSetting == null || !hasAccessibility()) {
			userSetting = new UserSetting();
		}
		return userSetting;
	}

	public synchronized static UserSetting getUserSettingOverride() {
		return DEFAULT_USER_SETTING;
	}

	public synchronized static boolean updateUserSetting() {
		if (hasAccessibility()) {
			String userSettingStr = getUserSetting().serialize();
			Log.i("updateUserSetting:", userSettingStr);
			Settings.setPreference(context, PREFERENCE_USER_SETTING_ID,
					userSettingStr);
			return true;
		} else {
			Toast.makeText(context,
					"Sorry! You cannot update your settings on this phone.",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public synchronized static void updateUserInformation() {
		if (hasAccessibility()) {
			String userInformationStr = currentUser.serialize();
			Log.i("updateUserInformation:", userInformationStr);
			Settings.setPreference(context, PREFERENCE_USER_INFORMATION_ID,
					userInformationStr);
		} else {
			Toast.makeText(context,
					"You cannot update your user information on this phone.",
					Toast.LENGTH_LONG).show();
		}
	}

	public synchronized static User getCurrentUser() {
		return currentUser;
	}

	public synchronized static void deleteContact(Contact contact) {
		if (hasCurrentPhoneLock()) {
			ContactsStore cache = null;
			try {
				cache = ContactsStore.getInstance(context);
				if (cache.deleteContact(contact)) {
					Toast.makeText(
							context,
							"Contact: " + contact.getName().toUpperCase()
									+ " Deleted from cache", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(
							context,
							"Failed to remove Contact: "
									+ contact.getName().toUpperCase()
									+ " from cache", Toast.LENGTH_SHORT).show();
				}
			} finally {
				if (cache != null) {
					cache.close();
				}
			}
		}
	}

	public synchronized static void deleteContacts(List<Contact> contacts) {
		if (hasCurrentPhoneLock()) {
			ContactsStore cache = null;
			try {
				cache = ContactsStore.getInstance(context);
				if (cache.deleteContacts(contacts)) {
					Toast.makeText(
							context,
							"Contact: " + contacts.size()
									+ " Deleted from cache", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(
							context,
							"Failed to remove Contact: " + contacts.size()
									+ " from cache", Toast.LENGTH_SHORT).show();
				}
			} finally {
				if (cache != null) {
					cache.close();
				}
			}
		}
	}

	public synchronized static void updateContact(Contact contact) {
		if (hasCurrentPhoneLock()) {
			ContactsStore cache = null;
			try {
				cache = ContactsStore.getInstance(context);
				if (cache.updateContact(contact)) {
					Toast.makeText(
							context,
							contact.getName().toUpperCase()
									+ " Updated in cache", Toast.LENGTH_SHORT)
							.show();
				}
			} finally {
				if (cache != null) {
					cache.close();
				}
			}
		}
	}

}
