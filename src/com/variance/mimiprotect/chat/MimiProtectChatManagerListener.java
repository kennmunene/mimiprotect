package com.variance.mimiprotect.chat;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.cache.ContactsStore;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;

public class MimiProtectChatManagerListener implements ChatManagerListener {
	// Use default activity.
	// Set as appropriate depending on initialization.
	public static Context packageContext = DashBoardActivity.DASH_BOARD_ACTIVITY;
	private static final Map<String, Chat> chatThreads = new HashMap<String, Chat>();

	public static Chat getChatThread(String chatId) {
		return chatThreads.get(chatId);
	}

	/**
	 * Called if the chat is initiated by the current user.
	 * 
	 * @param chat
	 * @param chatId
	 */
	public static void addChatThread(Chat chat, String chatId) {
		chatThreads.put(chatId, chat);
	}

	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		// Create a new chat window, if any.
		// At this point we add a message listener.
		chat.addMessageListener(new MessageListener() {

			public void processMessage(Chat chat, Message msg) {
				ChatManagerTabActivity chatManager = ChatManagerTabActivity.CHAT_MANAGER;
				String id = getId(chat.getParticipant());
				chatThreads.put(id, chat);
				// if chat manager is null, then we need to notify the user that
				// a chat has been requested through a notification manager.
				if (chatManager != null) {
					ChatManagerActivity chatWindow = chatManager
							.getCurrentActiveChat(id);
					if (chatWindow == null) {
						// This is a different chat individual, create a new
						// chat window.
						// create a notification here.
						sendChatNotificationRequest(id, chat.getParticipant(),
								msg.getBody());
						Log.e("chat window:",
								"chat window for: " + chat.getParticipant()
										+ " is null");
					} else {
						chatWindow.setCurrentChat(chat);
						ChatMessage chatMsg = new ChatMessage();
						chatMsg.setChatMessageId(id);
						chatMsg.setChatMessageType(ChatMessageType.USER_MESSAGE);
						chatMsg.setMessage(msg.getBody());
						chatWindow.receiveMessage(chatMsg);
						if (!chatManager.isActive()) {
							sendChatNotificationRequest(id,
									chat.getParticipant(), msg.getBody());
						}
					}
				} else {
					// create a notification here.
					sendChatNotificationRequest(id, chat.getParticipant(),
							msg.getBody());
				}
			}
		});
	}

	/**
	 * This method should be called once, at createChat event. Every call to
	 * this call cannot return the same value.
	 * 
	 * @param name
	 * @return
	 */
	private String getId(String name) {
		if (!name.contains("/")) {
			return name;
		}
		return name.substring(0, name.indexOf('/'));
	}

	private static int nextInt() {
		int val = (int) (System.currentTimeMillis() & 0xfffffff);
		Log.i("nextInt:", val + "");
		return val;
	}

	// we use this to notify the user if he is currently not active on chat.
	static void sendChatNotificationRequest(String chatId, String title,
			String message) {
		Context context = packageContext.getApplicationContext();
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.mimi_connect_logo16x16;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, message, when);
		nm.cancelAll();
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		title = title.contains("@") ? title.substring(0, title.indexOf("@"))
				: title;
		Contact livelinkedContact = ContactsStore.getInstance(context)
				.getLivelinkedPersonalContact(title);
		CharSequence contentText = message;
		Intent notificationIntent = new Intent(context,
				ChatManagerTabActivity.class);
		if (livelinkedContact != null) {
			Log.i("livelinked contact:", "livelinked contact: "
					+ livelinkedContact);
			title = livelinkedContact.getName();
			notificationIntent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT,
					livelinkedContact);
		}
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_CHAT_ID, chatId);
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE, title);
		notificationIntent.putExtra(
				MimiProtectIntentConstants.MIMI_PROTECT_CHAT_MESSAGE, message);
		notificationIntent.setAction(nextInt() + "");
		int id = nextInt();
		PendingIntent contentIntent = PendingIntent.getActivity(context, id,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, title, contentText,
				contentIntent);
		nm.notify(id, notification);
	}
}
