package com.variance.mimiprotect.chat;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.cache.ContactsStore;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Pair;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UserSetting;
import com.variance.mimiprotect.util.Utils;

public class ChatManagerActivity extends MimiProtectActivity {
	private class MyLog {
		void i(String title, String message) {
			Log.i(Settings.getCurrentDeviceId(ChatManagerActivity.this) + ":"
					+ title, message);
		}
	}

	private MyLog log = new MyLog();
	private EditText editText;
	private Contact contact;
	private final Map<String, Pair<Calendar, LinearLayout>> sentMessagesLayouts = Collections
			.synchronizedMap(new HashMap<String, Pair<Calendar, LinearLayout>>());
	private UserSetting userSetting;
	private ContactsStore cache;
	private boolean loadedChatMessages;
	private String chatId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.userSetting = MimiProtectGeneralManager.getUserSetting();
		this.cache = ContactsStore.getInstance(this);
		reloadView();
		preloadChatId(getIntent());
		loadChatMessages();
		initOnLoad(getIntent());
	}

	private void reloadView() {
		setContentView(R.layout.usercontact_chatwindow);
		editText = (EditText) findViewById(R.id.chatBoxField);
	}

	private void loadChatMessages() {
		if (!loadedChatMessages) {
			loadedChatMessages = true;
			if (chatId != null) {
				List<ChatMessage> msgs = cache
						.getContactChatMessages(normalizeChatId());
				Log.i("ChatMessages cache:", msgs.toString());
				for (ChatMessage ch : msgs) {
					if (ch.getChatType() != null) {
						switch (ch.getChatType()) {
						case RECEIPT:
							updateViewWithReceivedMessage(ch);
							break;
						case SENT:
							updateViewWithSentMessage(ch);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadChatMessages();
	}

	@Override
	protected void onPause() {
		super.onPause();
		loadedChatMessages = false;
	}

	private MessageListener createChatMessageListener() {
		return new MessageListener() {

			public void processMessage(Chat chat, Message msg) {
				ChatManagerTabActivity chatManager = ChatManagerTabActivity.CHAT_MANAGER;
				String id = chat.getParticipant();
				// if chat manager is null, then we need to notify the user that
				// a chat has been requested through a notification manager.
				if (chatManager != null) {
					// set current active tab
					chatManager.setCurrentActiveChat(id);
					// we set the chat for sending message, if it is already
					// not set
					ChatMessage chatMsg = new ChatMessage();
					chatMsg.setChatMessageId(id);
					chatMsg.setChatMessageType(ChatMessageType.USER_MESSAGE);
					chatMsg.setMessage(msg.getBody());
					receiveMessage(chatMsg);
					if (!chatManager.isActive()) {
						MimiProtectChatManagerListener
								.sendChatNotificationRequest(id,
										chat.getParticipant(), msg.getBody());
					}
				} else {
					// create a notification here.
					MimiProtectChatManagerListener.sendChatNotificationRequest(
							id, chat.getParticipant(), msg.getBody());
				}
			}
		};
	}

	private void preloadChatId(Intent i) {
		chatId = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_ID);
		if (chatId != null) {
			// Add this chat to currently active chats.
			ChatManagerTabActivity.CHAT_MANAGER.addCurrentActiveChat(this,
					chatId);
		}
	}

	private void initOnLoad(Intent i) {
		if (chatId != null) {
			// Add this chat to currently active chats.
			ChatManagerTabActivity.CHAT_MANAGER.addCurrentActiveChat(this,
					chatId);
			// if this is a user initiated chat, then create the chat.
			if (i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_INITIATED_BY_USER)) {
				ChatStatus status = (ChatStatus) i
						.getSerializableExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_STATUS);
				Log.e("chat status id: ", status.getId());
				ChatManagerTabActivity chatManager = ChatManagerTabActivity.CHAT_MANAGER;
				if (chatManager != null) {
					chatManager.addCurrentActiveChat(this, chatId);
					chatManager.setCurrentActiveChat(chatId);
				}
				DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager()
						.startChat(status, createChatMessageListener());
			}
			String message = i
					.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_MESSAGE);
			if (message != null) {
				ChatMessage chatMsg = new ChatMessage();
				chatMsg.setChatMessageId(chatId + System.currentTimeMillis());
				chatMsg.setChatMessageType(ChatMessageType.USER_MESSAGE);
				chatMsg.setMessage(message);
				receiveMessage(chatMsg);
			}
		}
		if (contact == null
				&& i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_INITIATED_BY_USER)) {
			Log.e("Contact null", "contact is null");
			if (i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT)) {
				contact = (Contact) getIntent()
						.getSerializableExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_SELECTED_CONTACT);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		PhonebookActivity.startGeneralActivity(this, "Mimi Protect",
				DashBoardActivity.class,
				R.layout.usercontact_whitetitled_tabview, true);
		super.finish();

	}

	public void handleMessage(View button) {
		try {
			String txtMessage = editText.getText().toString();
			sendMessage(txtMessage);
		} finally {
			editText.setText("");
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// initialized = false;
		initOnLoad(intent);
	}

	private Chat getChatThread() {
		return MimiProtectChatManagerListener.getChatThread(chatId);
	}

	private boolean sendMessageToContact(final String message) {
		try {
			getChatThread().sendMessage(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	boolean updateViewWithSentMessage(ChatMessage chatMessage) {
		LinearLayout chatWindow = (LinearLayout) findViewById(R.id.chatBox);
		LinearLayout chatBox = getView(chatWindow);
		if (chatBox != null) {
			TextView view = (TextView) chatBox.findViewById(R.id.chatView);
			TextView state = (TextView) chatBox.findViewById(R.id.chatState);
			if (view != null && state != null) {
				state.setText("Sent: "
						+ Utils.toIsoString(Calendar.getInstance()));
				chatWindow.addView(chatBox);
				view.setText(chatMessage.getMessage());
				updateScrollView(chatBox);
				return true;
			}
		}
		return false;
	}

	/**
	 * normalized if for using with chat messages to remove letters that may
	 * make the insertion or removal fail.
	 * 
	 * @return
	 */
	public String normalizeChatId() {
		byte[] data = chatId.getBytes();
		long value = 0;
		for (byte b : data) {
			value += b;
		}
		return Long.toHexString(value);
	}

	public void sendMessage(String txtMessage) {
		try {
			if (!Utils.isNullStringOrEmpty(txtMessage)) {
				ChatMessage chatMessage = encodeChatMessage(txtMessage);
				if (updateViewWithSentMessage(chatMessage)) {
					sendMessageToContact(txtMessage);
					if (userSetting != null && userSetting.isSaveChatMessages()
							&& MimiProtectGeneralManager.hasCurrentPhoneLock()) {
						chatMessage.setChatType(ChatType.SENT);
						chatMessage.setContactId(normalizeChatId());
						if (cache != null) {
							cache.saveChatMessage(chatMessage);
						}
					}
				}
			}
		} finally {
			editText.setText("");
		}
	}

	public void sendAcknowledgement(ChatMessage receivedMessage) {
		ChatMessage ack = new ChatMessage("Acknowledged",
				Utils.getDeviceIpAddress(), Settings.getChatPort(),
				ChatMessageType.RECEIPT_ACKNOWLEDGED,
				receivedMessage.getChatMessageId());
		String txtMessage = ack.toString();
		sendMessageToContact(txtMessage);
	}

	void setCurrentChat(Chat chat) {
	}

	void updateViewWithReceivedMessage(final ChatMessage msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LinearLayout chatWindow = (LinearLayout) findViewById(R.id.chatBox);
				LinearLayout chatBox = getReceiveView(chatWindow);
				if (chatBox != null) {
					TextView view = (TextView) chatBox
							.findViewById(R.id.chatView);
					TextView state = (TextView) chatBox
							.findViewById(R.id.chatState);
					if (view != null) {
						chatWindow.addView(chatBox);
						view.setText(msg.getMessage());
						state.setText("Received: "
								+ Utils.toIsoString(Calendar.getInstance()));
						updateScrollView(chatBox);
					}
				}
			}
		});
	}

	void receiveMessage(ChatMessage msg) {
		if (msg != null && !Utils.isNullStringOrEmpty(msg.getMessage())) {
			switch (msg.getChatMessageType()) {
			case USER_MESSAGE:
				updateViewWithReceivedMessage(msg);
				ChatManagerTabActivity.CHAT_MANAGER.setChatIndicator(chatId,
						msg.getMessage(), contact != null ? contact.getName()
								: chatId);
				// save message if enabled
				if (userSetting != null && userSetting.isSaveChatMessages()
						&& MimiProtectGeneralManager.hasCurrentPhoneLock()) {
					msg.setChatType(ChatType.RECEIPT);
					msg.setContactId(normalizeChatId());
					if (cache != null) {
						cache.saveChatMessage(msg);
					}
				}
				break;
			case RECEIPT_ACKNOWLEDGED:
				setMessagetSentState(msg, "Sent: ");
				break;
			}
		}
	}

	private void setMessagetSentState(ChatMessage msg, String stateInfo) {
		synchronized (sentMessagesLayouts) {
			Pair<Calendar, LinearLayout> layouts = sentMessagesLayouts.get(msg
					.getChatMessageId());
			if (layouts != null) {
				LinearLayout layout = layouts.getSecond();
				sentMessagesLayouts.remove(msg.getChatMessageId());
				TextView state = (TextView) layout.findViewById(R.id.chatState);
				state.setText(stateInfo
						+ Utils.toIsoString(Calendar.getInstance()));
			}
		}
	}

	private ChatMessage encodeChatMessage(String message) {
		ChatMessage chatMessage = new ChatMessage(message,
				Utils.getDeviceIpAddress(), Settings.getChatPort());
		return chatMessage;
	}

	private void updateScrollView(View view) {
		final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
		Runnable runnable = new Runnable() {

			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}

		};
		new Handler().postDelayed(runnable, 100l);
	}

	private LinearLayout getView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.usercontact_chatbox_me, parent, false);
		return layout;
	}

	private LinearLayout getReceiveView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.usercontact_chatbox_friend, parent, false);
		return layout;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			log.i("ChatWindow", "Getting out of chat window");
			if (ChatManagerTabActivity.CHAT_MANAGER != null) {
				ChatManagerTabActivity.CHAT_MANAGER
						.removeCurrentActiveChat(chatId);
			}
			finish();
		}
		return false;
	}

	private void addClearChats(Menu menu) {
		MenuItem userSettingMenuItem = menu.add("Clear Chats");
		userSettingMenuItem.setIcon(R.drawable.mimi_connect_clear_chat);
		userSettingMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						MimiProtectActivity.showYesOrNoOption(
								ChatManagerActivity.this,
								"Clear your chat history.",
								"Clear Chat Hostroy",
								new OnRequestComplete<Boolean>() {

									@Override
									public void requestComplete(Boolean result) {
										Log.i("Deleting chats",
												"deleting chat: " + result);
										if (result != null && result) {
											// delete the cache
											if (cache != null
													&& cache.deleteChatMessage(normalizeChatId())) {
												Log.i("Chat Cache",
														"Chat Cache deleted");
												reloadView();
												loadChatMessages();
											}
										}
									}
								});
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addClearChats(menu);
		return super.onCreateOptionsMenu(menu);
	}

}
