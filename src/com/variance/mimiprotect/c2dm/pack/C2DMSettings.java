package com.variance.mimiprotect.c2dm.pack;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class C2DMSettings {
	public final static String C2DM_VALUE_MESSAGE_TYPE_LINK_REQUEST = "C2DM_KEY_LINK_REQUEST";
	public final static String C2DM_VALUE_MESSAGE_TYPE_LINK_SUGGESTION = "C2DM_KEY_LINK_SUGGESTION";
	public final static String C2DM_VALUE_MESSAGE_TYPE_CHAT_NOTIFICATION = "C2DM_CHAT_NOTIFICATION";
	public final static String C2DM_KEY_MESSAGE_TYPE = "C2DM_KEY_MESSAGE_TYPE";
	public final static String C2DM_KEY_MESSAGE = "C2DM_KEY_MESSAGE";
	public final static String C2DM_KEY_CONTACT_ID = "chat_010";
	public final static String C2DM_KEY_CONTACT_NAME = "chat_011";
	public final static String C2DM_GCM_KEY = "gcmkey";
	private static String registrationID = "";
	private static SharedPreferences preferences;
	private static Editor editor;
	private static String KEY_SAVE_REG_ID = "REG_ID";
	public static String SENDER_ID = "673948273791";

	public static String getSavedRegistrationID(Context context) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		String savedReg = preferences.getString(KEY_SAVE_REG_ID, "");
		return savedReg;
	}

	public static String getRegistrationID() {
		return registrationID;
	}

	public static void saveRegistrationID(Context context, String reg) {
		if (preferences == null) {
			preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		editor = preferences.edit();
		editor.putString(KEY_SAVE_REG_ID, reg);
		editor.commit();
		registrationID = reg;
	}
}
