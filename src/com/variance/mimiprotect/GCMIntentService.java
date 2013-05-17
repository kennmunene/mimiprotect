package com.variance.mimiprotect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.variance.mimiprotect.c2dm.pack.C2DMSettings;
import com.variance.mimiprotect.c2dm.pack.RegisterServerTask;
import com.variance.mimiprotect.chat.ChatManagerActivity;
import com.variance.mimiprotect.chat.ChatMessage;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Utils;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(C2DMSettings.SENDER_ID);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.d("c2dm", "error " + errorId);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		handleMessage(context, intent);
	}

	@Override
	protected void onRegistered(Context context, String registrationID) {
		Log.d("c2dm", "reg " + registrationID);
		if (registrationID != null) {
			C2DMSettings.saveRegistrationID(context, registrationID);
			RegisterServerTask rtask = new RegisterServerTask(context);
			rtask.execute();
		}
	}

	@Override
	protected void onUnregistered(Context context, String arg1) {
		Log.e("", arg1);
	}

	public String getGCMIntentServiceClassName() {
		return GCMIntentService.class.getSimpleName();
	}

	private void handleMessage(Context context, Intent intent) {
		Log.d("c2dm service", "push message received");
		String msg = "";
		String messageType = intent.getExtras().getString(
				C2DMSettings.C2DM_KEY_MESSAGE_TYPE);
		if (C2DMSettings.C2DM_VALUE_MESSAGE_TYPE_LINK_REQUEST
				.equals(messageType)) {
			Log.d("c2dm", "link request received");
			msg = intent.getExtras().getString(C2DMSettings.C2DM_KEY_MESSAGE);
			notifyUser("MiMi Protect Link Request", msg);
		} else if (C2DMSettings.C2DM_VALUE_MESSAGE_TYPE_LINK_SUGGESTION
				.equals(messageType)) {
			Log.d("c2dm", "link suggestion received");
			notifyUser("MiMi Protect Suggestions", msg);
		} else if (C2DMSettings.C2DM_VALUE_MESSAGE_TYPE_CHAT_NOTIFICATION
				.equals(messageType)) {
			Log.d("c2dm serviCE", "CHAT NOTIFICATION MESSAGE RECEIVED");
			String contactId = intent
					.getStringExtra(C2DMSettings.C2DM_KEY_CONTACT_ID);
			String contactName = intent
					.getStringExtra(C2DMSettings.C2DM_KEY_CONTACT_NAME);
			msg = intent.getExtras().getString(C2DMSettings.C2DM_KEY_MESSAGE);
			Log.d("c2dm serviCE", "CONTACT ID: " + contactId);
			Log.d("c2dm serviCE", "CONTACT NAME: " + contactName);
			Log.d("c2dm serviCE", "CHAT MSG: " + msg);
			if (!Utils.isNullStringOrEmpty(contactId)
					&& !Utils.isNullStringOrEmpty(contactName)) {
				sendChatNotificationRequest("Chat Notification", msg,
						contactId, contactName);
			}
		}
	}

	private void notifyUser(String title, String msg) {
		Context context = getApplicationContext();
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.mimi_connect_logo16x16;
		CharSequence text = msg;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, text, when);
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = title;
		CharSequence contentText = msg;
		Intent notificationIntent = new Intent(this, GeneralTabActivity.class);
		notificationIntent
				.putExtra(
						MimiProtectIntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA,
						true);
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				LiveLinkRequestsActivity.class.getName());
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				"Livelink Requests");
		notificationIntent
				.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
						R.layout.usercontact_whitetitled_tabview);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		int id = 93;
		nm.notify(id, notification);
	}

	private void sendChatNotificationRequest(String title, String msg,
			String contactId, String contactName) {
		Context context = getApplicationContext();
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
		int icon = R.drawable.mimi_connect_logo16x16;
		CharSequence text = msg;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, text, when);
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = title;
		ChatMessage chat = ChatMessage.getInstance(msg);
		CharSequence contentText = chat != null ? chat.getMessage()
				: contactName;
		Intent notificationIntent = new Intent(this, ChatManagerActivity.class);
		notificationIntent.setAction(nextInt() + "");
		notificationIntent
				.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_CHAT_NOTIFICATION,
						true);
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				ChatManagerActivity.class.getName());
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				contactName);
		notificationIntent
				.putExtra(
						MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
						R.layout.usercontact_whitetitled_tabview);
		// add chat data that must be transferred.
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_CHAT_CONTACT_ID,
				contactId);
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_CHAT_MESSAGE, msg);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				nextInt(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		int id = nextInt();
		nm.notify(id, notification);
	}

	private int nextInt() {
		// create a unique integer.
		int val = (int) (System.currentTimeMillis() & 0xfffffff);
		Log.i("nextInt:", val + "");
		return val;
	}
}
