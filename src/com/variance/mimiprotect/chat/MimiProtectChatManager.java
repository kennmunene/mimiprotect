package com.variance.mimiprotect.chat;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

import com.variance.mimiprotect.chat.settings.ChatSettings;
import com.variance.mimiprotect.chat.settings.ChatSettings.ChatConfiguration;
import com.variance.mimiprotect.chat.settings.LoginCredentials;

public class MimiProtectChatManager {
	Connection gmailConnection;
	Connection fbConnection;
	Connection mimiConnection;
	Roster gmailRoster, mimiRoster, fbRoster;
	public final String serviceIdentifier = "@kenn-one";
	private Map<String, MimiProtectMessageListener> chatMessageListeners;

	public MimiProtectChatManager() {
		chatMessageListeners = new HashMap<String, MimiProtectMessageListener>();
	}

	public void login() {
		// login to MiMi, FB, and gmail if accounts have been set

		ChatConfiguration gmailConfig = ChatSettings.getGmailConfigurations();
		LoginCredentials gmailCreds = ChatSettings.getGmailCredentials();
		Log.i("gmail-creds", "" + gmailCreds);
		Log.i("gmail-config", "" + gmailConfig);
		if (gmailConfig != null && gmailCreds != null) {
			gmailConnection = connect(gmailConfig, gmailCreds);
			Log.i("gmail-connected", "" + isGtalkConnected());
		}

		ChatConfiguration fbConfig = ChatSettings.getFacebookConfigurations();
		LoginCredentials fbCreds = ChatSettings.getFacebookCredentials();
		if (fbConfig != null && fbCreds != null) {
			fbConnection = connect(fbConfig, fbCreds);
			Log.i("fb-connected", "" + isFacebbokConnected());
		}

		ChatConfiguration mimiConfig = ChatSettings.getMiMiConfigurations();
		LoginCredentials mimiCreds = ChatSettings.getMiMiCredentials();
		Log.i("mimi-creds:", mimiCreds + "");
		if (mimiConfig != null && mimiCreds != null) {
			mimiConnection = connect(mimiConfig, mimiCreds);
			Log.i("mimi-connected", "" + isMimiConnected());
		}
		if (mimiConnection != null) {
			listenToChatAttempts(mimiConnection);
		}

		if (gmailConnection != null) {
			listenToChatAttempts(gmailConnection);
		}
		if (fbConnection != null) {
			listenToChatAttempts(fbConnection);
		}
	}

	public boolean isGtalkConnected() {
		return gmailConnection != null && gmailConnection.isConnected();
	}

	public boolean isFacebbokConnected() {
		return fbConnection != null && fbConnection.isConnected();
	}

	public boolean isMimiConnected() {
		return mimiConnection != null && mimiConnection.isConnected();
	}

	public boolean isConnected() {
		return isGtalkConnected() || isFacebbokConnected() || isMimiConnected();
	}

	/*
	 * The user identifier should be the livelink id
	 */
	public ChatStatus getChatStatusMimi(String userIdentifier) {
		if (mimiConnection == null) {
			Log.i("mimiConnection", "mimiConnection is not connected");
			return null;
		}
		mimiRoster = mimiConnection.getRoster();
		String entryId = userIdentifier;
		if (!entryId.contains(serviceIdentifier)) {
			entryId = entryId.trim().concat(serviceIdentifier);
		}
		RosterEntry entry;
		try {
			Log.i("MIMichat manager","entry id = "+entryId);
			entry = mimiRoster.getEntry(entryId);
			Log.i("MIMichat manager","entry = "+entry);
			Presence presence;
			if (entry != null && (presence = mimiRoster.getPresence(entryId)) != null && presence.isAvailable()) {
				ChatStatus status = new ChatStatus();
				status.AccountType = ChatStatus.AccountType_MIMI;
				status.status = ChatStatus.STATUS_ONLINE;
				status.id = entryId;
				return status;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ChatStatus getChatStatusGmail(String userIdentifier) {
		if (gmailConnection == null) {
			Log.i("gmailConnection", "gmailConnection is not connected");
			return null;
		}
		gmailRoster = gmailConnection.getRoster();
		// must be email address
		String entryId = userIdentifier;
		RosterEntry entry;
		try {
			entry = gmailRoster.getEntry(entryId);
			if (entry != null && gmailRoster.getPresence(entryId).isAvailable()) {
				ChatStatus status = new ChatStatus();
				status.AccountType = ChatStatus.AccountType_GMAIL;
				status.status = ChatStatus.STATUS_ONLINE;
				status.id = entryId;
				return status;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	private Connection connect(ChatConfiguration chatConfig,
			LoginCredentials loginCreds) {
		try {
			Connection connection;
			ConnectionConfiguration config = new ConnectionConfiguration(
					chatConfig.host, chatConfig.port, chatConfig.service);
			connection = new XMPPConnection(config);
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.connect();
			connection
					.login(loginCreds.getUsername(), loginCreds.getPassword());
			Log.i(here, chatConfig.service + " login complete");
			connection.sendPacket(new Presence(Presence.Type.available));
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String here = MimiProtectChatManager.class.getName();

	public MimiProtectMessageListener getMimiProtectMessageListener(String id) {
		return chatMessageListeners.get(id);
	}

	public void logout() {
		this.chatMessageListeners.clear();
		// logout to MiMi, FB, and gmail if accounts have been set
		terminateConnection(fbConnection);
		terminateConnection(gmailConnection);
		terminateConnection(mimiConnection);
	}

	private void terminateConnection(Connection connection) {
		if (connection != null && connection.isConnected()) {
			try {
				connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection = null;
			}
		}
	}

	private void listenToChatAttempts(Connection connection) {
		ChatManager chatManager = connection.getChatManager();
		if (chatManager == null) {
			return;
		}
		chatManager.addChatListener(new MimiProtectChatManagerListener());
	}

	public Chat startChat(ChatStatus status, MessageListener listener) {
		Chat chat = null;
		try {
			if (status.AccountType == ChatStatus.AccountType_GMAIL
					&& gmailConnection != null) {
				chat = gmailConnection.getChatManager().createChat(status.id,
						listener);
				return chat;
			}
			if (status.AccountType == ChatStatus.AccountType_FACEBOOK
					&& fbConnection != null) {
				chat = fbConnection.getChatManager().createChat(status.id,
						listener);
				return chat;
			}
			if (status.AccountType == ChatStatus.AccountType_MIMI
					&& mimiConnection != null) {
				chat = mimiConnection.getChatManager().createChat(status.id,
						listener);
				return chat;
			}
		} finally {
			if (chat != null) {
				// register the chat.
				MimiProtectChatManagerListener.addChatThread(chat,
						chat.getParticipant());
			}
		}
		return null;
	}

}
