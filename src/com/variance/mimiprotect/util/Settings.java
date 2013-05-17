package com.variance.mimiprotect.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.variance.mimiprotect.SessionInitializationListener;
import com.variance.mimiprotect.ad.AdRequestInfo;
import com.variance.mimiprotect.business.meeting.Meeting;
import com.variance.mimiprotect.call.CallInformation;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.contacts.UserInfo;
import com.variance.mimiprotect.contacts.UserLoginInformation;
import com.variance.mimiprotect.contacts.Users;
import com.variance.mimiprotect.contacts.business.BusinessInformation;
import com.variance.mimiprotect.contacts.business.SmsMessage;
import com.variance.mimiprotect.widget.FastDialContacts;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.xml.VDocument;

@SuppressWarnings("unused")
public class Settings {
	/**
	 * Chat constants
	 */
	public static final String LIVELINKED_CONTACT_ONLINE_ID_PARAMETER = "lk_online";
	public static final String LIVELINKED_CONTACT_ONLINE = "lv_0001";
	public static final String CURRENT_USER_CHAT_PORT = "chat_001";
	public static final String CURRENT_USER_CHAT_IP = "chat_002";
	public static final String CURRENT_USER_CHAT_NOTIFICATION_MESSAGE = "chat_005";
	public static final String CURRENT_USER_CHAT_NOTIFICATION_USER = "chat_006";
	/**
	 * Ad Request info parameter
	 */
	public static final String AD_REQUEST_INFO = "ad_1002";
	// Android unique initialization identifier for the loading of initial
	// contacts
	public static final String INIT_ANDROID_LOAD_TXT = "45454545";
	public static final String SESSION_ID_PARAMETER = "sessionID";
	public static final String MIMIPROTECT_IMSIS = "imsis_0932555";
	/**
	 * Sync constants
	 */
	public static final String PERSONAL_CONTACT_HASH_PARAMETER = "hashes";
	public static final String PERSONAL_CONTACT_ONSYNC_REQUEST_PARAMETER = "on_sync_result";
	/**
	 * GCM KEY
	 */
	public static final String MIMIPROTECT_GCM_KEY_REGISTERED = "GCM_KEY_REGISTERED";

	private static final String ACTION_PARAMETER = "action";

	public static final String FILE_QR_PATH = "temp-vcard.png";

	private static final String CALL_INFORMATION_URL = "callmanager";
	private static final String CALL_INFORMATION_INFO_PARAMETER = "call";

	public static final String LIVE_LINK_CONFIRM_SHARE = "10000001";
	public static final String LIVE_LINK_CONFIRM_SHARE_SEND = "10000002";
	public static final String MIMIPROTECT_INTERNATIONAL_DIALING_CODE = "0xA00010001";

	private static final String LATITUDE_PARAMETER = "lat";
	private static final String LONGITUDE_PARAMETER = "lon";

	private static final String MIMIPROTECT_CONTACTS_TO_SHARE = "contactstoshare";
	private static final String MIMIPROTECT_CONTACTS_TO_SEND_TO = "contactstosendto";
	private static final String MIMIPROTECT_SHARE_PROFILE_BUSINESS_CARD = "shareprofilebusinesscard";

	private static final String MIMIPROTECT_GENERAL_URL = "mimiprotect";

	private static final String PROFILE_REQUEST_CODE = "prc";
	private static final String PROFILE_EDIT = "edit";
	private static final String PROFILE_LOAD = "load";
	private static final String PROFILE_DATA = "data";
	private static final String PROFILE_URL = "userprofile";

	private static final String FILE_LIST_ACTION_PARAMETER = "action";
	private static final String FILE_LIST_ACTION_PARAMETER_VALUE = "listfiles";

	private static final String CONTACT_BACKUP_URL = "";
	private static final String CONTACT_BACKUP_ACTION_PARAMETER_VALUE = "backup";
	private static final String CONTACT_BACKUP_CONTACT_PARAMETER = "contact";
	private static final String CONTACT_BACKUP_ENC_TYPE_PARAMETER = "ENC_TYPE";
	private static final String CONTACT_BACKUP_AES_ENC_TYPE = "AES";

	private static final String SIGNUP_SURNAME_PARAMETER = "surname";
	private static final String SIGNUP_OTHERNAMES_PARAMETER = "otherNames";
	private static final String SIGNUP_USER_PARAMETER = "user";
	private static final String SIGNUP_PASSWORD_PARAMETER = "password";
	private static final String SIGNUP_CPASSWORD_PARAMETER = "cpassword";
	private static final String SIGNUP_EMAIL_PARAMETER = "email";
	private static final String MIMIPROTECT_MSISDN_PARAMETER = "msisdn";

	private static final String MIMIPROTECT_SUBSCRIBER_ID_PARAMETER = "subid";
	private static final String MIMIPROTECT_SIM_SERIAL_NUMBER_PARAMETER = "simserialnum";
	private static final String MIMIPROTECT_SIM_COUNTRY_ISO_PARAMETER = "simcountryiso";
	public static final String MIMIPROTECT_ANDROID_STATUS = "mimiprotect_android_status";

	private static final String CONTACT_RESTORE_ACTION_PARAMETER = "action";
	private static final String CONTACT_RESTORE_ACTION_PARAMETER_VALUE = "restore";
	private static final String CONTACT_RESTORE_PAGE_PARAMETER = "page";

	private static final String CONTACT_UPDATE_DATA = "cupdate";
	private static final String CONTACT_UPDATE_ACTION = "action";
	private static final String CONTACT_UPDATE_ACTION_VALUE = "updateContact";

	private static final String CONTACT_DELETE_ACTION_PARAMETER = "action";
	private static final String CONTACT_DELETE_ACTION_PARAMETER_VALUE = "delete";
	private static final String CONTACT_DELETE_CONTACT_ID_PARAMETER = "contactid";

	private static final String CONTACT_SEARCH_PARAMETER = "search";
	private static final String CONTACT_SEARCH_LOAD_RESULT_PARAMETER = "load";
	private static final String CONTACT_SEARCH_RESULT_PAGE_PARAMETER = "page";
	private static final String CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER = "maxresult";
	private static final String CONTACT_CURRENT_LATITUDE = "lat";
	private static final String CONTACT_CURRENT_LONGITUDE = "lon";

	private static final String LIVELINK_REVOKE_MYLIVELINKS_ON = "users";
	private static final String LIVELINK_REQUEST_PARAMETER = "livelinkRequest";
	private static final String LIVELINK_OCR_REQUEST_PARAMETER = "livelinkOCR";
	private static final String LIVELINK_REQUEST_URL = "livelink";
	private static final String LIVELINK_GCM_REGISTRATION_URL = "livelinkgcm";
	private static final String LIVELINK_REQUESTS_DOWNLOAD_URL = "livelinkdownload";
	private static final String LIVELINK_REQUESTS_ACCEPTED = "accepted";
	private static final String LIVELINK_REQUESTS_IGNORED = "rejected";
	private static final String LIVELINK_REQUESTS_STATUS = "livelinkstatus";
	private static final String LIVELINK_REQUESTS_LIVELINKID = "livelinkid";
	private static final String LIVELINK_REQUESTS_CONFIRMATION_URL = "livelinkconfirm";
	public static final String LIVELINK_CONTACT_ID_USERCONTACT_ID_PARAMETER = "livelinkonusercontacts_onid";

	private static final String UNLINK_REQUEST_PARAMETER = "unlink";

	private static final String PROCESS_BUSINESS_CARD_IMAGE_URL = "businesscard";
	private static final String PROCESS_BUSINESS_CARD_IMAGE_PARAMETER = "card";

	private static final String QR_ENCODER_URL = "http://chart.apis.google.com/chart";

	private static final String LOGIN_USER_PARAMETER = "user";
	private static final String LOGIN_PASSWORD_PARAMETER = "password";
	private static final String LOGIN_CLIENT_PARAMETER = "client";
	private static final String LOGIN_INFORMATION_DATA_PARAMETER = "login_data";
	private static final String LOGIN_CLIENT_PARAMETER_VALUE = "cbnapp";
	private static final String LOGIN_ACTION_PARAMETER_VALUE = "signin";

	private static final String LOGOUT_ACTION_PARAMETER_VALUE = "signout";

	private static final String BUSINESS_CONTACTS_URL = "BusinessContactManager";
	private static final String BUSINESS_CONTACTS_SEARCH_PARAMETER = "bsearch";
	private static final String BUSINESS_CONTACTS_NEW_PARAMETER = "bnew";
	private static final String BUSINESS_CONTACTS_SEARCH_PARAMETER_DEFAULT_VALUE = "1000001";
	private static final String BUSINESS_CONTACTS_ADD_FROM_PERSONAL_CONTACTS_PARAMETER = "contacts";
	private static final String BUSIENSS_CONTACTS_ADD_NEW_CONTACT_PARAMETER = "newcontact";
	private static final String BUSINESS_CONTACTS_USER_PARAMETER = "buser";
	public static final String BUSINESS_CONTACTS_SMS_MESSAGE = "b_smsmessage";

	public static final String BUSINESS_MEETING_MEETING_CODE = "bm_134647855";
	public static final String BUSINESS_MEETING_MEETING_ID = "bm_6254895115";
	public static final String BUSINESS_MEETING_USER_ID = "bm_59745621456f";
	public static final String BUSINESS_MEETING_MEETING_INFO = "bm_254688455";

	private static final String DEVELOPMENT_IP_OFFICE = "http://192.168.0.26";
	private static final String DEVELOPMENT_IP_AMAZON_EC2 = "HTTP://54.235.246.64";
	private static final String DEVELOPMENT_IP_MIMI_NETWORK = "http://192.168.2.108";
	private static final String DEVELOPMENT_IP_SAMSUNG_GALAXY = "http://192.168.43.132";
	private static final String DEVELOPMENT_IP_SERVER = "http://41.57.96.94";

	private static final String DEVELOPMENT_IP_OFFICE_ADDRESS = "192.168.0.12";
	private static final String DEVELOPMENT_IP_MIMI_NETWORK_ADDRESS = "192.168.2.108";
	private static final String DEVELOPMENT_IP_SAMSUNG_GALAXY_ADDRESS = "192.168.43.132";
	private static final String DEVELOPMENT_IP_SERVER_ADDRESS = "41.57.96.94";

	/**
	 * IP for actual connection to be set here
	 */
	public static final String IP_ADDRESS = DEVELOPMENT_IP_AMAZON_EC2;
	public static final String MIMI_PROTECT_SERVER_IP = DEVELOPMENT_IP_MIMI_NETWORK_ADDRESS;

	/**
	 * Currently available location data to be transferred to the server for
	 * search
	 */
	private static volatile double currentLongitude = 0.0;
	private static volatile double currentLatitude = 0.0;

	private static String port = "80";
	private static String context = "variance";
	private static String contactSharedUrl = "contactshare";
	private static String contactRequestUrl = "contactrequest";
	private static String searchContactUrl = "numberfind";
	private static String contactShareIdParameter = "shareId";
	private static String contactShareNameParameter = "shareName";
	private static String contactSharePhonesParameter = "sharePhones";
	private static String contactShareEmailsParameter = "shareEmails";
	private static String contactShareConfirmSmsSendParameter = "shareConfirmSend";
	private static String contactManagerUrl = "manage";
	private static volatile String sessionID = null;
	private static volatile boolean sessionIsInitializing;
	private static int chatPort = 4040;

	public static boolean DIRECT_VCF_WRITTEN = false;
	public static boolean DIRECT_VCF_REQUESTED = false;
	public static boolean DIRECT_VCF_IN_PROGRESS = false;
	private static final List<SessionInitializationListener> SESSION_INITIALIZATION_LISTENERS = Collections
			.synchronizedList(new ArrayList<SessionInitializationListener>());

	public synchronized static boolean addSessionInitializationListener(
			SessionInitializationListener listener) {
		if (sessionIsInitializing) {
			return SESSION_INITIALIZATION_LISTENERS.add(listener);
		}
		return false;
	}

	public static int getChatPort() {
		return chatPort;
	}

	public static void setChatPort(int chatPort) {
		Settings.chatPort = chatPort;
	}

	public static boolean isDebugging() {
		return false;
	}

	public static boolean isLoggedIn() {
		return getSessionID() != null;
	}

	public static Map<String, String> getSessionParameter() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(SESSION_ID_PARAMETER, getSessionID());
		map.put(LATITUDE_PARAMETER, getCurrentLatitude() + "");
		map.put(LONGITUDE_PARAMETER, getCurrentLongitude() + "");
		return map;
	}

	public static Map<String, String> makeLivelinkRequestParameter() {
		Map<String, String> map = getSessionParameter();
		map.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REQUEST.toString());
		return map;
	}

	public static Map<String, String> makeRequestParameter(
			ActionParameterValue actionParameterValue,
			Map<String, String> requestParams) {
		Map<String, String> m = getSessionParameter();
		if (actionParameterValue != null) {
			m.put(ACTION_PARAMETER, actionParameterValue.toString());
		}
		if (requestParams != null) {
			m.putAll(requestParams);
		}
		return m;
	}

	public static Map<String, String> makeCountPersonalContactsForSearchParameter(
			String searchTerm) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_COUNTS.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchTerm);
		return m;
	}

	public static Map<String, String> makeCountBusinessContactsForSearchParameter(
			String searchTerm) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_COUNTS.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchTerm);
		return m;
	}

	public static Map<String, String> makeCountPersonalContactsParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_COUNTS.toString());
		return m;
	}

	public static Map<String, String> makeAESCountPersonalContactsParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_COUNTS.toString());
		// m.put(CONTACT_BACKUP_ENC_TYPE_PARAMETER,
		// CONTACT_BACKUP_AES_ENC_TYPE);
		return m;
	}

	public static Map<String, String> makeLivelinkRequestForContactsOnMimiParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REQUEST_FIND_CONTACTS_ON_MIMI
						.toString());
		return m;
	}

	public static Map<String, String> makeLivelinkRequestForContactsNotOnMimiParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REQUEST_FIND_CONTACTS_NOT_ON_MIMI
						.toString());
		return m;
	}

	public static Map<String, String> makeCountBusinessContactsParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_COUNTS.toString());
		return m;
	}

	public static Map<String, String> makeAddBusinessContactUser(String username) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_ADD_USER.toString());
		m.put(BUSINESS_CONTACTS_USER_PARAMETER, username);
		return m;
	}

	public static Map<String, String> makeRemoveBusinessContactUser(
			String username) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_REMOVE_USER.toString());
		m.put(BUSINESS_CONTACTS_USER_PARAMETER, username);
		return m;
	}

	public static Map<String, String> makeRemoveBusinessContactUser(
			ArrayList<UserInfo> userInfo) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_REMOVE_USER.toString());
		m.put(BUSINESS_CONTACTS_USER_PARAMETER,
				DataParser.getUserNameInformation(userInfo));
		return m;
	}

	public static Map<String, String> makeSelectBusinessContactUsers() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_SELECT_USER.toString());
		return m;
	}

	public static Map<String, String> makeAddBusinessContactNewContact(
			String contact) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_ADD_NEW_CONTACT
						.toString());
		m.put(BUSIENSS_CONTACTS_ADD_NEW_CONTACT_PARAMETER, contact);
		return m;
	}

	public static Map<String, String> makeFindInternationalDialingCode() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_CURRENT_INTERNATIONAL_DIALING_CODE
						.toString());
		m.put(LATITUDE_PARAMETER, getCurrentLatitude() + "");
		m.put(LONGITUDE_PARAMETER, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makeAddBusinessContactFromPersonalContactParameter(
			List<Contact> contacts) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_ADD_FROM_PERSONAL_CONTACTS
						.toString());
		m.put(BUSINESS_CONTACTS_ADD_FROM_PERSONAL_CONTACTS_PARAMETER,
				DataParser.getPersonalContactIdInformation(contacts));
		return m;
	}

	public static Map<String, String> makeLoadAllPersonalContactsForBusinessSelectionParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_LOAD_CONTACTS_FOR_BUSINESS_SELECTION
						.toString());
		return m;
	}

	public static Map<String, String> makeLoadAllPersonalContactsParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_LOAD_ALL.toString());
		return m;
	}

	public static Map<String, String> makeSendBusinessBulkSms(SmsMessage sms) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_SEND_BULK_MESSAGE.toString());
		m.put(BUSINESS_CONTACTS_SMS_MESSAGE, sms.toString());
		return m;
	}

	public static Map<String, String> makeLoadAllPersonalContactsParameter(
			SearchParameter searchParam) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_LOAD_CONTACTS_FOR_BUSINESS_SELECTION
						.toString());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParam.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParam.getMaxResult() + "");
		m.put(CONTACT_SEARCH_PARAMETER, searchParam.getSearchTerm());
		return m;
	}

	public static Map<String, String> makeLoadAllContactsParameter(
			SearchParameter searchParam,
			ActionParameterValue actionParameterValue) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, actionParameterValue.toString());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParam.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParam.getMaxResult() + "");
		m.put(CONTACT_SEARCH_PARAMETER, searchParam.getSearchTerm());
		return m;
	}

	public static Map<String, String> makeBusinessContactNewBusinessParameter(
			BusinessInformation bi) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_NEW.toString());
		m.put(BUSINESS_CONTACTS_NEW_PARAMETER,
				DataParser.marshallBusinessInformation(bi));
		return m;
	}

	public static Map<String, String> makeUserHasBusinessContactParameters() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_CHECK_USER_MEMBERSHIP
						.toString());
		return m;
	}

	public static Map<String, String> makeSendOrShareContactsParameter(
			List<Contact> contactsToShareOrSend, List<Contact> contactsToSendTo) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_SHARE_CONTACT.toString());
		m.put(MIMIPROTECT_CONTACTS_TO_SHARE, DataParser
				.getPersonalContactIdInformation(contactsToShareOrSend));
		m.put(MIMIPROTECT_CONTACTS_TO_SEND_TO,
				DataParser.getPersonalContactIdInformation(contactsToSendTo));
		return m;
	}

	public static Map<String, String> makeBusinessMeetingSearchParameter(
			String meetingId) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_FIND_MEETING.toString());
		m.put(BUSINESS_MEETING_MEETING_CODE, meetingId);
		return m;
	}

	public static Map<String, String> makeBusinessMeetingScheduleParameter(
			Meeting meeting) {
		Map<String, String> m = getSessionParameter();
		String meetingData = new VObjectMarshaller<Meeting>(Meeting.class)
				.doMarshall(meeting);
		Log.i("Meeting info", meetingData + "");
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_SCHEDULE_MEETING
						.toString());
		m.put(BUSINESS_MEETING_MEETING_INFO, meetingData);
		return m;
	}

	public static Map<String, String> makeBusinessMeetingStartParameter(
			Meeting meeting) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_START_MEETING.toString());
		m.put(BUSINESS_MEETING_MEETING_ID, meeting.getMeetingId() + "");
		return m;
	}

	public static Map<String, String> makeBusinessMeetingSuspendParameter(
			Meeting meeting) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_SUSPEND_MEETING
						.toString());
		m.put(BUSINESS_MEETING_MEETING_ID, meeting.getMeetingId() + "");
		return m;
	}

	public static Map<String, String> makeBusinessMeetingJoinParameter(
			Meeting meeting) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_JOIN_MEETING.toString());
		m.put(BUSINESS_MEETING_MEETING_ID, meeting.getMeetingId() + "");
		return m;
	}

	public static Map<String, String> makeBusinessMeetingLeaveParameter(
			Meeting meeting) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_LEAVE_MEETING.toString());
		m.put(BUSINESS_MEETING_MEETING_ID, meeting.getMeetingId() + "");
		return m;
	}

	public static Map<String, String> makeBusinessMeetingSaveCardParameter(
			long userId) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_MEETING_SAVE_BUSINESS_CARD
						.toString());
		m.put(BUSINESS_MEETING_USER_ID, userId + "");
		Log.i("params", m + "");
		return m;
	}

	public static Map<String, String> makeSendOrShareProfileContactParameter(
			List<Contact> contactsToSendTo) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_SHARE_PROFILE_BUSINESS_CARD
						.toString());
		m.put(MIMIPROTECT_CONTACTS_TO_SEND_TO,
				DataParser.getPersonalContactIdInformation(contactsToSendTo));
		return m;
	}

	public static Map<String, String> makeLivelinkRequestAvailableParameter() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_AVAILABLE.toString());
		return m;
	}

	public static Map<String, String> makeLoadBusinessContactParameters() {
		return makeLoadBusinessContactParameters(BUSINESS_CONTACTS_SEARCH_PARAMETER_DEFAULT_VALUE);
	}

	public static Map<String, String> makeLoadBusinessContactParameters(
			String searchTerm) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_LOAD.toString());
		m.put(BUSINESS_CONTACTS_SEARCH_PARAMETER, searchTerm);
		return m;
	}

	public static Map<String, String> makeLoadBusinessContactParameters(
			SearchParameter searchTerm) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.BUSINESS_CONTACTS_LOAD.toString());
		m.put(BUSINESS_CONTACTS_SEARCH_PARAMETER, searchTerm.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER, searchTerm.getCurrentPage()
				+ "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchTerm.getMaxResult() + "");
		return m;
	}

	public static Map<String, String> makeLogoutParameters() {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, LOGOUT_ACTION_PARAMETER_VALUE);
		return m;
	}

	public static Map<String, String> makeDeleteContactParameters(
			String contactId) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, CONTACT_DELETE_ACTION_PARAMETER_VALUE);
		m.put(CONTACT_DELETE_CONTACT_ID_PARAMETER, contactId);
		return m;
	}

	public static Map<String, String> makeDeleteContactsParameters(
			List<Contact> contacts) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACT_DELETE_CONTACTS
						.toString());
		m.put(CONTACT_DELETE_CONTACT_ID_PARAMETER,
				DataParser.getPersonalContactIdInformation(contacts));
		return m;
	}

	public static Map<String, String> makeDownloadFileListParameters() {
		Map<String, String> m = getSessionParameter();
		m.put(FILE_LIST_ACTION_PARAMETER, FILE_LIST_ACTION_PARAMETER_VALUE);
		return m;
	}

	public static Map<String, String> makeRestoreParameters(int page) {
		Map<String, String> m = getSessionParameter();
		m.put(CONTACT_RESTORE_ACTION_PARAMETER,
				CONTACT_RESTORE_ACTION_PARAMETER_VALUE);
		m.put(CONTACT_RESTORE_PAGE_PARAMETER, page + "");
		return m;
	}

	public static Map<String, String> makeRestoreAESParameters(int page) {
		Map<String, String> m = getSessionParameter();
		m.put(CONTACT_RESTORE_ACTION_PARAMETER,
				CONTACT_RESTORE_ACTION_PARAMETER_VALUE);
		m.put(CONTACT_RESTORE_PAGE_PARAMETER, page + "");
		// m.put(CONTACT_BACKUP_ENC_TYPE_PARAMETER,
		// CONTACT_BACKUP_AES_ENC_TYPE);
		return m;
	}

	public static Map<String, String> makeChatNotificationParameters(
			String userId, String message) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINKED_CONTACT_CHAT_REQUEST.toString());
		m.put(CURRENT_USER_CHAT_NOTIFICATION_MESSAGE, message);
		m.put(CURRENT_USER_CHAT_NOTIFICATION_USER, userId);
		return m;
	}

	public static Map<String, String> makeBusinessCardParameter(Bitmap image) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, out);
		byte[] imageData = out.toByteArray();
		String imageStrData = Arrays.toString(imageData);
		Map<String, String> m = getSessionParameter();
		m.put(PROCESS_BUSINESS_CARD_IMAGE_PARAMETER, imageStrData);
		return m;
	}

	public static Map<String, String> makeCallInformationParameters(
			CallInformation callInfo) {
		Map<String, String> m = getSessionParameter();
		// add lat and long
		callInfo.setLatitude(getCurrentLatitude());
		callInfo.setLongitude(getCurrentLongitude());
		m.put(CALL_INFORMATION_INFO_PARAMETER,
				DataParser.getCallInformation(callInfo));
		return m;
	}

	public static Map<String, String> makeUnlinkParameters(Contact c) {
		Map<String, String> m = getSessionParameter();
		String unlinkRequest = DataParser.getUnlinkDataFrom(c);
		m.put(UNLINK_REQUEST_PARAMETER, unlinkRequest);
		return m;
	}

	public static Map<String, String> getLivelinkAcceptedParameter(
			String livelinkId) {
		Map<String, String> m = getSessionParameter();
		m.put(LIVELINK_REQUESTS_LIVELINKID, livelinkId);
		m.put(LIVELINK_REQUESTS_STATUS, LIVELINK_REQUESTS_ACCEPTED);
		return m;
	}

	public static Map<String, String> getLivelinkIgnoredParameter(
			String livelinkId) {
		Map<String, String> m = getSessionParameter();
		m.put(LIVELINK_REQUESTS_LIVELINKID, livelinkId);
		m.put(LIVELINK_REQUESTS_STATUS, LIVELINK_REQUESTS_IGNORED);
		return m;
	}

	public static Map<String, String> getLoginParameters(String user,
			String password) {
		UserLoginInformation loginInformation = new UserLoginInformation(user,
				password);
		String loginInformationData = new VObjectMarshaller<UserLoginInformation>(
				UserLoginInformation.class).doMarshall(loginInformation);
		Log.i("loginInformationData", loginInformationData + "");
		Map<String, String> params = getSessionParameter();
		params.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_LOGIN_REQUEST.toString());
		params.put(LOGIN_INFORMATION_DATA_PARAMETER, loginInformationData);
		params.put(CURRENT_USER_CHAT_PORT, getChatPort() + "");
		params.put(CURRENT_USER_CHAT_IP, Utils.getDeviceIpAddress());
		return params;
	}

	public static Map<String, String> makeContactShareParameters(String id) {
		Map<String, String> map = getSessionParameter();
		map.put(contactShareIdParameter, id);
		return map;
	}

	public static Map<String, String> makeContactUpdateParameters(
			Contact contact) {
		Map<String, String> map = getSessionParameter();
		map.put(CONTACT_UPDATE_DATA,
				DataParser.getPersonalContactString(contact));
		map.put(CONTACT_UPDATE_ACTION, CONTACT_UPDATE_ACTION_VALUE);
		return map;
	}

	public static Map<String, String> makeLivelinkRequestParameter(
			String userName) {
		Map<String, String> map = getSessionParameter();
		map.put(LIVELINK_REQUEST_PARAMETER, userName);
		return map;
	}

	public static Map<String, String> makeLivelinkRequestParameter(
			List<Contact> contacts) {
		Map<String, String> map = getSessionParameter();
		map.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REQUEST.toString());
		map.put(LIVELINK_OCR_REQUEST_PARAMETER,
				DataParser.getPersonalContactIdInformation(contacts));
		return map;
	}

	public static Map<String, String> makeContactShareConfirmSmsSendParameters() {
		Map<String, String> map = getSessionParameter();
		map.put(contactShareConfirmSmsSendParameter,
				LIVE_LINK_CONFIRM_SHARE_SEND);
		return map;
	}

	public static Map<String, String> getProfileRequestLoadParameter() {
		Map<String, String> map = getSessionParameter();
		map.put(PROFILE_REQUEST_CODE, PROFILE_LOAD);
		return map;
	}

	public static Map<String, String> getProfileRequestEditParameter(User user) {
		Map<String, String> map = getSessionParameter();
		map.put(PROFILE_REQUEST_CODE, PROFILE_EDIT);
		String userData = new VObjectMarshaller<User>(User.class)
				.doMarshall(user);
		map.put(PROFILE_DATA, userData);
		return map;
	}

	public static Map<String, String> getLoadMyLivelinksParameter() {
		Map<String, String> map = getSessionParameter();
		map.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_MYLIVELINKS.toString());
		return map;
	}

	public static Map<String, String> getRevokeMyLivelinksParameter(
			Users usersToRevokeLivelinks) {
		Map<String, String> map = getSessionParameter();
		map.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REVOKE_MYLIVELINKS.toString());
		String usersData = new VObjectMarshaller<Users>(Users.class)
				.doMarshall(usersToRevokeLivelinks);
		map.put(LIVELINK_REVOKE_MYLIVELINKS_ON, usersData);
		return map;
	}

	public static Map<String, String> makeContactShareParameters(Contact c) {
		Map<String, String> map = getSessionParameter();
		map.put(contactShareIdParameter, c.getId());
		map.put(contactShareNameParameter, c.getName());
		map.put(contactSharePhonesParameter, Utils.toString(c.getPhones()));
		map.put(contactShareEmailsParameter, Utils.toString(c.getEmails()));
		return map;
	}

	public static Map<String, String> makeSearchParameter(String searchTerm) {
		Map<String, String> m = getSessionParameter();
		m.put(CONTACT_SEARCH_PARAMETER, searchTerm);
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makeBusinessDirectorySearchParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.CORPORATE_CONTACT_DETAILS.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;

	}

	public static Map<String, String> makeSearchParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makeAESSearchParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		// m.put(ACTION_PARAMETER,
		// ActionParameterValue.PERSONAL_CONTACTS_SEARCH.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		// m.put(CONTACT_BACKUP_ENC_TYPE_PARAMETER,
		// CONTACT_BACKUP_AES_ENC_TYPE);
		return m;
	}

	public static Map<String, String> makeFindContactParameter(String contactId) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACT_FIND.toString());
		m.put(CONTACT_SEARCH_PARAMETER, contactId);
		return m;
	}

	public static Map<String, String> makePersonalContactSearchParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.PERSONAL_CONTACTS_LOAD_CONTACTS_FOR_BUSINESS_SELECTION
						.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makeDiscoverySearchContactParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.DISCOVERY_SEARCH_CONTACTS.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makePersonalContactSearchForLivelinkParameter(
			SearchParameter searchParameter) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINK_REQUEST_FIND_CONTACTS_NOT_ON_MIMI
						.toString());
		m.put(CONTACT_SEARCH_PARAMETER, searchParameter.getSearchTerm());
		m.put(CONTACT_SEARCH_RESULT_PAGE_PARAMETER,
				searchParameter.getCurrentPage() + "");
		m.put(CONTACT_SEARCH_MAXIMUM_RESULT_PARAMETER,
				searchParameter.getMaxResult() + "");
		m.put(CONTACT_CURRENT_LATITUDE, getCurrentLatitude() + "");
		m.put(CONTACT_CURRENT_LONGITUDE, getCurrentLongitude() + "");
		return m;
	}

	public static Map<String, String> makeLoadSearchResultParameter(
			String resultId) {
		Map<String, String> m = getSessionParameter();
		m.put(CONTACT_SEARCH_LOAD_RESULT_PARAMETER, resultId);
		return m;
	}

	public static Map<String, String> makeBackupParameters(String contact) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, CONTACT_BACKUP_ACTION_PARAMETER_VALUE);
		m.put(CONTACT_BACKUP_CONTACT_PARAMETER, contact);
		return m;
	}

	public static Map<String, String> makeBackupAESParameters(String contact) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, CONTACT_BACKUP_ACTION_PARAMETER_VALUE);
		m.put(CONTACT_BACKUP_CONTACT_PARAMETER, contact);
		// m.put(CONTACT_BACKUP_ENC_TYPE_PARAMETER,
		// CONTACT_BACKUP_AES_ENC_TYPE);
		return m;
	}

	public static Map<String, String> makeSignupParameters(String surname,
			String otherNames, String user, String password, String cpassword,
			String email, String msisdn) {
		Map<String, String> m = getSessionParameter();
		m.put(SIGNUP_CPASSWORD_PARAMETER, cpassword);
		m.put(SIGNUP_SURNAME_PARAMETER, surname);
		m.put(SIGNUP_OTHERNAMES_PARAMETER, otherNames);
		m.put(SIGNUP_USER_PARAMETER, user);
		m.put(SIGNUP_PASSWORD_PARAMETER, password);
		m.put(SIGNUP_EMAIL_PARAMETER, email);
		m.put(MIMIPROTECT_MSISDN_PARAMETER, msisdn);
		return m;
	}

	public static Map<String, String> makeSignupParameters(String user,
			String password, String cpassword) {
		Map<String, String> m = getSessionParameter();
		m.put(SIGNUP_CPASSWORD_PARAMETER, cpassword);
		m.put(SIGNUP_USER_PARAMETER, user);
		m.put(SIGNUP_PASSWORD_PARAMETER, password);
		return m;
	}

	public static Map<String, String> makeSignupParameters(String user,
			String email, String password, String cpassword) {
		Map<String, String> m = getSessionParameter();
		m.put(SIGNUP_CPASSWORD_PARAMETER, cpassword);
		m.put(SIGNUP_USER_PARAMETER, user);
		m.put(SIGNUP_EMAIL_PARAMETER, email);
		m.put(SIGNUP_PASSWORD_PARAMETER, password);
		return m;
	}

	public static Map<String, String> makeMimiProtectSettingStatusRequest(
			Context context) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_ANDROID_STATUS.toString());
		m.put(MIMIPROTECT_SUBSCRIBER_ID_PARAMETER,
				getCurrentSubscriberId(context));
		m.put(MIMIPROTECT_SIM_SERIAL_NUMBER_PARAMETER,
				getCurrentSimSerialNumber(context));
		m.put(MIMIPROTECT_SIM_COUNTRY_ISO_PARAMETER,
				getCurrentSimCountryIso(context));
		return m;
	}

	public static Map<String, String> makeMimiProtectGCMStatusRequest(
			Context context) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_GCM_KEY_REGISTERED.toString());
		return m;
	}

	public static Map<String, String> makeCheckLivelinkedContactOnline(
			String livelinkId, Context context) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.LIVELINKED_CONTACT_ONLINE.toString());
		m.put(LIVELINKED_CONTACT_ONLINE_ID_PARAMETER, livelinkId);
		return m;
	}

	public static Map<String, String> makeMimiProtectSettings(Context context) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER,
				ActionParameterValue.MIMIPROTECT_ANDROID_PARAMETER.toString());
		m.put(MIMIPROTECT_SUBSCRIBER_ID_PARAMETER,
				getCurrentSubscriberId(context));
		m.put(MIMIPROTECT_SIM_SERIAL_NUMBER_PARAMETER,
				getCurrentSimSerialNumber(context));
		m.put(MIMIPROTECT_SIM_COUNTRY_ISO_PARAMETER,
				getCurrentSimCountryIso(context));
		return m;
	}

	public static Map<String, String> makeSignupParameters(String surname,
			String otherNames, String user, String password, String cpassword,
			String email, String msisdn, Context context) {
		Map<String, String> m = getSessionParameter();
		m.put(SIGNUP_CPASSWORD_PARAMETER, cpassword);
		m.put(SIGNUP_SURNAME_PARAMETER, surname);
		m.put(SIGNUP_OTHERNAMES_PARAMETER, otherNames);
		m.put(SIGNUP_USER_PARAMETER, user);
		m.put(SIGNUP_PASSWORD_PARAMETER, password);
		m.put(SIGNUP_EMAIL_PARAMETER, email);
		m.put(MIMIPROTECT_MSISDN_PARAMETER, msisdn);
		return m;
	}

	public static Map<String, String> makeContactShareIdParameters(Contact c) {
		return makeContactShareParameters(c.getId());
	}

	public static String getURL() {
		return IP_ADDRESS.concat(":").concat(port) + "/" + context;
	}

	public static String getBusinessContactUrl() {
		return getURL() + "/" + BUSINESS_CONTACTS_URL;
	}

	public static String getDeleteContactUrl() {
		return getContactManagerUrl();
	}

	public static String getProcessBusinessCardImageUrl() {
		return getURL() + "/" + PROCESS_BUSINESS_CARD_IMAGE_URL;
	}

	public static String getLivelinkConfirmationUrl() {
		return getURL() + "/" + LIVELINK_REQUESTS_CONFIRMATION_URL;
	}

	public static String getQREncoderURL() {
		return QR_ENCODER_URL;
	}

	public static String getLivelinkUrl() {
		return getURL() + "/" + LIVELINK_REQUEST_URL;
	}

	public static String getContactManagerUrl() {
		return getURL().concat("/").concat(contactManagerUrl);
	}

	public static String getProfileRequestUrl() {
		return getURL().concat("/").concat(PROFILE_URL);
	}

	public static String getSearchContactUrl() {
		return getURL().concat("/").concat(searchContactUrl);
	}

	public static String getFindContactUrl() {
		return getURL().concat("/").concat(searchContactUrl);
	}

	public static String getDiscoverySearchContactUrl() {
		return getURL().concat("/").concat(searchContactUrl);
	}

	public static String getContactSharedUrl() {
		return getURL().concat("/").concat(contactSharedUrl);
	}

	public static String getContactRequestUrl() {
		return getURL().concat("/").concat(contactRequestUrl);
	}

	public static String getNumbersURL() {
		return getURL().concat("/numberfind");
	}

	public static String getFileDownloadURL() {
		return getURL().concat("/downloadfile");
	}

	public static String getLiveLinkRegistrationURL() {
		return getURL().concat("/").concat(LIVELINK_GCM_REGISTRATION_URL);
	}

	public static String getMimiProtectGeneralUrl() {
		return getURL().concat("/").concat(MIMIPROTECT_GENERAL_URL);
	}

	public static String getServletUploadURL() {
		return getURL().concat("/UploadServlet");
	}

	public static String getBackupURL() {
		return getURL().concat("/manage");
	}

	public static String getBusinessNumbersURL() {
		return getURL().concat("/numberfind");
	}

	public static String getCallInformationUrl() {
		return getURL() + "/" + CALL_INFORMATION_URL;
	}

	public static String getNumbersDisplayURL() {
		return getURL().concat("/mobile/displayNumbers.jsp?");
	}

	public static String getSigninURL() {
		String url = "";
		url = getURL().concat("/signin");
		return url;
	}

	public static String getLogoutURL() {
		return getURL().concat("/signin");
	}

	public static String getLiveLinkRequestsDownloadURL() {
		return getURL().concat("/").concat(LIVELINK_REQUESTS_DOWNLOAD_URL);
	}

	public static String getSignupURL() {
		String url = "";
		url = getURL().concat("/signup");
		return url;
	}

	public static String getIpaddress() {
		return IP_ADDRESS;
	}

	public static String getPort() {
		return port;
	}

	public static synchronized void setSessionInitializing() {
		sessionIsInitializing = true;
	}

	public static synchronized boolean isSessionInitializing() {
		return sessionIsInitializing;
	}

	public static void stopSessionInitializationIfAny() {
		// If we log out abruptly, before the session initialization on the
		// server may be complete.
		sessionIsInitializing = false;
	}

	public static String getSessionID() {
		String sessionId_ = sessionID;
		if (sessionId_ == null) {
			synchronized (Settings.class) {
				sessionId_ = sessionID;
			}
		}
		return sessionId_;
	}

	public static synchronized double getCurrentLongitude() {
		return currentLongitude;
	}

	public static synchronized void setCurrentLongitude(double currentLongitude) {
		Settings.currentLongitude = currentLongitude;
	}

	public static synchronized double getCurrentLatitude() {
		return currentLatitude;
	}

	public static synchronized void setCurrentLatitude(double currentLatitude) {
		Settings.currentLatitude = currentLatitude;
	}

	private static SharedPreferences preferences;
	private static Editor editor;
	private static final String KEY_USER_NAME = "userName";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_LOGIN_AUTOMATICALLY = "loginAutomatic";
	private static final String KEY_SHORTCUT_CREATED = "mimi_protect_shortcut";

	public static String getSavedUserName(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		return preferences.getString(KEY_USER_NAME, "");

	}

	public static String getSavedPassword(Context context) {
		if (!isSetLoginAutomatically(context)) {
			return null;
		}
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		return preferences.getString(KEY_PASSWORD, "");
	}

	public static void saveUserName(Context context, String valUserName,
			String password) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		editor = preferences.edit();
		editor.putString(KEY_USER_NAME, valUserName);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}

	public static void setLoginAutomatically(Context context, boolean state) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		editor = preferences.edit();
		editor.putString(KEY_LOGIN_AUTOMATICALLY, "" + state);
		editor.commit();
	}

	public static boolean isSetLoginAutomatically(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		String res = preferences.getString(KEY_LOGIN_AUTOMATICALLY, "false");
		return res != null && "true".equalsIgnoreCase(res);
	}

	public synchronized static void saveSessionID(Context context,
			String sessionID) {
		// we only initialize this if we set session initialization
		if (sessionIsInitializing) {
			Settings.sessionID = sessionID;
			sessionIsInitializing = false;
			Settings.class.notifyAll();
		}
		for (SessionInitializationListener l : SESSION_INITIALIZATION_LISTENERS) {
			l.sessionInitialized();
		}
	}

	public synchronized static void releaseSessionOnLogout() {
		// We release the session and at the same time, stop any further
		// initialization.
		stopSessionInitializationIfAny();
		sessionID = null;
		// notify session listeners if any
		for (SessionInitializationListener l : SESSION_INITIALIZATION_LISTENERS) {
			l.sessionDestroyed();
		}
		SESSION_INITIALIZATION_LISTENERS.clear();
	}

	private static String qrfilepath = "";

	public static String getQRFilepath() {
		return qrfilepath;
	}

	public static void setQRFilepath(String path) {
		qrfilepath = path;
	}

	public static void setShortcutCreated(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		editor = preferences.edit();
		editor.putString(KEY_SHORTCUT_CREATED, true + "");
		editor.commit();
	}

	public static boolean isShortcutCreated(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		String res = preferences.getString(KEY_SHORTCUT_CREATED, "false");
		return res != null && "true".equalsIgnoreCase(res);
	}

	public static boolean getBooleanPreference(Context context, String key) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		String res = preferences.getString(key, "false");
		return res != null && "true".equalsIgnoreCase(res);
	}

	public static String getPreference(Context context, String key) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		String res = preferences.getString(key, "false");
		return res;
	}

	public static void setPreference(Context context, String key, String value) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static FastDialContacts getFastDialContacts(Context context) {
		String fastDial = Settings.getPreference(context,
				FastDialContacts.FAST_DIAL_CONTACT_PREFERENCE_ID);
		try {
			return fastDial != null ? (new VObjectMarshaller<FastDialContacts>(
					FastDialContacts.class).unmarshall(VDocument
					.parseDocumentFromString(fastDial))) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static FastDialContacts getOrCreateFastDialContacts(Context context) {
		String fastDial = Settings.getPreference(context,
				FastDialContacts.FAST_DIAL_CONTACT_PREFERENCE_ID);
		FastDialContacts dialContacts = new FastDialContacts();
		try {
			dialContacts = fastDial != null ? (new VObjectMarshaller<FastDialContacts>(
					FastDialContacts.class).unmarshall(VDocument
					.parseDocumentFromString(fastDial))) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialContacts;
	}

	public static void setSignedUp(Context context) {
		setPreference(context,
				MimiProtectIntentConstants.MIMI_RPOTECT_SIGNED_UP_OPTION,
				true + "");
	}

	public static boolean isSignedUp(Context context) {
		String result = getPreference(context,
				MimiProtectIntentConstants.MIMI_RPOTECT_SIGNED_UP_OPTION);
		return result != null && result.trim().equalsIgnoreCase("true");
	}

	public static String getCurrentSIMCountryCode(Context context) {
		String imsis = getCurrentSimSerialNumber(context);
		String code = imsis.trim().substring(2, 5);
		while (code.startsWith("0")) {
			code = code.substring(1);
		}
		return "+" + code;
	}

	public static String getCurrentSubscriberId(Context context) {
		TelephonyManager tmManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tmManager != null ? tmManager.getSubscriberId() : null;
	}

	public static String getCurrentSimSerialNumber(Context context) {
		TelephonyManager tmManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tmManager != null ? tmManager.getSimSerialNumber() : null;
	}

	public static String getCurrentDeviceId(Context context) {
		TelephonyManager tmManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tmManager != null ? tmManager.getDeviceId() : null;
	}

	public static String getCurrentSimCountryIso(Context context) {
		TelephonyManager tmManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tmManager != null ? tmManager.getSimCountryIso() : null;
	}

	public static void saveCurrentMimiProtectSettings(Activity activity) {
		setPreference(activity, MIMIPROTECT_SUBSCRIBER_ID_PARAMETER,
				getCurrentSubscriberId(activity));
		setPreference(activity, MIMIPROTECT_SIM_COUNTRY_ISO_PARAMETER,
				getCurrentSimCountryIso(activity));
		setPreference(activity, MIMIPROTECT_SIM_SERIAL_NUMBER_PARAMETER,
				getCurrentSimSerialNumber(activity));
	}

	public static boolean hasCurrentMimiProtectSettingsChanged(Context context) {
		String currentSubscirberId = getCurrentSubscriberId(context);
		String currentSimSerialId = getCurrentSimSerialNumber(context);
		String storedSubscriberId = getPreference(context,
				MIMIPROTECT_SUBSCRIBER_ID_PARAMETER);
		String storedSimSerialId = getPreference(context,
				MIMIPROTECT_SIM_SERIAL_NUMBER_PARAMETER);
		Log.i("Subscriber Id0:", currentSubscirberId);
		Log.i("Subscriber Id1:", storedSubscriberId);
		Log.i("SimSerialNumber Id0:", currentSimSerialId);
		Log.i("SimSerialNumber Id1:", storedSimSerialId);
		boolean subIdChanged = currentSubscirberId == null ? storedSubscriberId != null
				: currentSubscirberId.equals(storedSubscriberId);
		boolean simSerialChanged = currentSimSerialId == null ? storedSimSerialId != null
				: currentSimSerialId.equals(storedSimSerialId);
		boolean changed = !subIdChanged || !simSerialChanged;
		Log.i("MimiProtect Settings Chnaged:", changed + "");
		return changed;
	}

	public static String getAdRequestUrl() {
		return getURL() + "/admanager";
	}

	public static String getBusinessMeetingUrl() {
		return getURL() + "/meeting";
	}

	public static Map<String, String> getAdRequestInfo(int adId) {
		Map<String, String> m = getSessionParameter();
		m.put(ACTION_PARAMETER, ActionParameterValue.AD_REQUEST.toString());
		AdRequestInfo info = new AdRequestInfo(getCurrentLatitude(),
				getCurrentLongitude(), adId);
		m.put(AD_REQUEST_INFO, new VObjectMarshaller<AdRequestInfo>(
				AdRequestInfo.class).doMarshall(info));
		return m;
	}
}
