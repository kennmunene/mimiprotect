package com.variance.mimiprotect.util;

import java.util.Calendar;

import android.util.Log;

import com.variance.mimiprotect.chat.settings.LoginCredentials;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.android.annotations.Transient;
import com.variance.vjax.android.converter.Converter;

public class UserSetting implements Cloneable {
	public static final String USERSETTING_lastCacheUpdate = "lastCacheUpdate";
	public static final String USERSETTING_cacheUpdatePeriod = "cacheUpdatePeriod";
	public static final String USERSETTING_allowLocalCache = "allowLocalCache";
	public static final String USERSETTING_lockMimiProtectToCurrentPhone = "lockMimiProtectToCurrentPhone";
	public static final String USERSETTING_imei = "imei";
	public static final String USERSETTING_username = "username";
	public static final String USERSETTING_password = "password";
	public static final String USERSETTING_enableAutomaticLogin = "enableAutomaticLogin";
	public static final String USERSETTING_showSplashScreen = "showSplashScreen";
	public static final String USERSETTING_showDashboard = "showDashboard";
	public static final String USERSETTING_businessPhonebookUser = "businessPhonebookUser";
	public static final String USERSETTING_businessPhonebookName = "businessPhonebookName";

	public static final class CalendarConverter implements
			Converter<Calendar, String> {

		public String convertFrom(Calendar value) {
			return Utils.toIsoTString(value);
		}

		public Calendar convertTo(String value) {
			Calendar cal = Utils.parseISODate(value);
			return cal;
		}

	}

	@com.variance.vjax.android.annotations.Converter(CalendarConverter.class)
	private Calendar lastCacheUpdate;
	private int cacheUpdatePeriod;
	private boolean allowLocalCache;
	private boolean lockMimiProtectToCurrentPhone;
	private String imei = "IMEI";
	private String id;
	private String username = "username";
	private String password;
	private boolean enableAutomaticLogin;
	private boolean showSplashScreen = true;
	private boolean showDashboard = true;
	private boolean businessPhonebookUser;
	private String businessPhonebookName;
	private boolean saveChatMessages = true;
	private LoginCredentials gtalkCredential;
	private LoginCredentials facebookCredential;
	@Transient
	private LoginCredentials mimiProtectCredential;

	private UserSetting(UserSetting settings) {
		this();
		this.lastCacheUpdate = Calendar.getInstance();
		this.lastCacheUpdate.setTimeInMillis(settings.getLastCacheUpdate()
				.getTimeInMillis());
		this.cacheUpdatePeriod = settings.getCacheUpdatePeriod();
		this.allowLocalCache = settings.allowLocalCache;
		this.lockMimiProtectToCurrentPhone = settings.lockMimiProtectToCurrentPhone;
		this.imei = settings.imei;
		this.username = settings.username;
		this.id = settings.id;
		this.password = settings.password;
		this.enableAutomaticLogin = settings.enableAutomaticLogin;
		this.showSplashScreen = settings.showSplashScreen;
		this.showDashboard = settings.showDashboard;
		this.businessPhonebookName = settings.businessPhonebookName;
		this.businessPhonebookUser = settings.businessPhonebookUser;
		this.saveChatMessages = settings.saveChatMessages;
		this.gtalkCredential = new LoginCredentials(settings
				.getGtalkCredential().getUsername(), settings
				.getGtalkCredential().getPassword());
		this.facebookCredential = new LoginCredentials(settings
				.getFacebookCredential().getUsername(), settings
				.getFacebookCredential().getPassword());
	}

	public UserSetting() {
		super();
		this.gtalkCredential = new LoginCredentials();
		this.facebookCredential = new LoginCredentials();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LoginCredentials getGtalkCredential() {
		return gtalkCredential;
	}

	public void setGtalkCredential(LoginCredentials gtalkCredential) {
		this.gtalkCredential = gtalkCredential;
	}

	public LoginCredentials getFacebookCredential() {
		return facebookCredential;
	}

	public void setFacebookCredential(LoginCredentials facebookCredential) {
		this.facebookCredential = facebookCredential;
	}

	public LoginCredentials getMimiProtectCredential() {
		if (this.mimiProtectCredential == null
				&& MimiProtectGeneralManager.getCurrentUser() != null)
			this.mimiProtectCredential = new LoginCredentials(
					MimiProtectGeneralManager.getCurrentUser()
							.getUserContactId(), MimiProtectGeneralManager
							.getCurrentUser().getPassword());
		return this.mimiProtectCredential;
	}

	public boolean isSaveChatMessages() {
		return saveChatMessages;
	}

	public void setSaveChatMessages(boolean saveChatMessages) {
		this.saveChatMessages = saveChatMessages;
	}

	public boolean isBusinessPhonebookUser() {
		return businessPhonebookUser;
	}

	public void setBusinessPhonebookUser(boolean businessPhonebookUser) {
		this.businessPhonebookUser = businessPhonebookUser;
	}

	public String getBusinessPhonebookName() {
		return businessPhonebookName;
	}

	public void setBusinessPhonebookName(String businessPhonebookName) {
		this.businessPhonebookName = businessPhonebookName;
	}

	public boolean isEnableAutomaticLogin() {
		return enableAutomaticLogin;
	}

	public void setEnableAutomaticLogin(boolean enableAutomaticLogin) {
		this.enableAutomaticLogin = enableAutomaticLogin;
	}

	public boolean isShowSplashScreen() {
		return showSplashScreen;
	}

	public void setShowSplashScreen(boolean showSplashScreen) {
		this.showSplashScreen = showSplashScreen;
	}

	public boolean isShowDashboard() {
		return showDashboard;
	}

	public void setShowDashboard(boolean showDashboard) {
		this.showDashboard = showDashboard;
	}

	public Calendar getLastCacheUpdate() {
		if (lastCacheUpdate == null) {
			lastCacheUpdate = Calendar.getInstance();
			lastCacheUpdate.set(1972, 0, 1, 0, 0, 0);
			lastCacheUpdate.set(Calendar.MILLISECOND, 0);
		}
		Log.i("getLastCacheUpdate:", Utils.toIsoString(lastCacheUpdate) + "");
		return lastCacheUpdate;
	}

	public void setLastCacheUpdate(Calendar lastCacheUpdate) {
		this.lastCacheUpdate = lastCacheUpdate;
		Log.i("setLastCacheUpdate:", Utils.toIsoString(lastCacheUpdate) + "");
	}

	public int getCacheUpdatePeriod() {
		return cacheUpdatePeriod;
	}

	public void setCacheUpdatePeriod(int cacheUpdatePeriod) {
		this.cacheUpdatePeriod = cacheUpdatePeriod;
	}

	public boolean isAllowLocalCache() {
		return allowLocalCache;
	}

	public void setAllowLocalCache(boolean allowLocalCache) {
		this.allowLocalCache = allowLocalCache;
	}

	public boolean isLockMimiProtectToCurrentPhone() {
		return lockMimiProtectToCurrentPhone;
	}

	public void setLockMimiProtectToCurrentPhone(
			boolean lockMimiProtectToCurrentPhone) {
		this.lockMimiProtectToCurrentPhone = lockMimiProtectToCurrentPhone;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String serialize() {
		return new VObjectMarshaller<UserSetting>(UserSetting.class)
				.doMarshall(this);
	}

	@Override
	protected UserSetting clone() throws CloneNotSupportedException {
		Object o = super.clone();
		return new UserSetting((UserSetting) o);
	}

}
