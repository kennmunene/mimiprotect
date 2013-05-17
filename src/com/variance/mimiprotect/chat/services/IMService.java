/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.variance.mimiprotect.chat.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.chat.ChatManagerActivity;
import com.variance.mimiprotect.chat.communication.SocketOperator;
import com.variance.mimiprotect.chat.interfaces.ISocketOperator;
import com.variance.mimiprotect.chat.interfaces.IUpdateData;
import com.variance.mimiprotect.chat.interfaces.MessageReceiver;
import com.variance.mimiprotect.chat.tools.FriendController;
import com.variance.mimiprotect.chat.tools.XMLHandler;
import com.variance.mimiprotect.chat.types.FriendInfo;
import com.variance.mimiprotect.util.Settings;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The {@link LocalServiceController}
 * and {@link LocalServiceBinding} classes show how to interact with the
 * service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class IMService extends Service implements MessageReceiver, IUpdateData {
	// private NotificationManager mNM;

	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public ConnectivityManager conManager = null;
	private String rawFriendList = new String();

	ISocketOperator socketOperator = new SocketOperator(this);

	private final IBinder mBinder = new IMBinder();
	private String username;
	private String password;
	private String userKey;
	private boolean authenticatedUser = false;

	// timer to take the updated data from server
	// private Timer timer;
	@Override
	public void socketClosed() {
		// TODO Auto-generated method stub
	}

	private NotificationManager mNM;

	public class IMBinder extends Binder {
		public IMService getService() {
			Log.i("Binder.getService()", IMService.this.hashCode() + "");
			return IMService.this;
		}

	}

	@Override
	public void onCreate() {
		Log.i("IMService", "imservice oncreate");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting. We put an icon in the
		// status bar.
		// showNotification();
		conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		Thread thread = new Thread() {
			@Override
			public void run() {
				// socketOperator.startListening(LISTENING_PORT_NO);
				Random random = new Random();
				int tryCount = 0;
				int listeningPort = 10000 + random.nextInt(20000);
				boolean connected = true;
				while (socketOperator.startListening(listeningPort) == 0) {
					tryCount++;
					listeningPort = 10000 + random.nextInt(20000);
					if (tryCount > 10) {
						// if it can't listen a port after trying 10 times, give
						// up...
						connected = false;
						break;
					}
				}
				if (connected) {
					Settings.setChatPort(listeningPort);
				}
			}
		};
		thread.start();
	}

	/*
	 * @Override public void onDestroy() { // Cancel the persistent
	 * notification. mNM.cancel(R.string.local_service_started);
	 * 
	 * // Tell the user we stopped. Toast.makeText(this,
	 * R.string.local_service_stopped, Toast.LENGTH_SHORT).show(); }
	 */

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("IMService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	/**
	 * Show a notification while this service is running.
	 * 
	 * @param msg
	 **/
	private void showNotification(String username, String msg) {
		// Set the icon, scrolling text and timestamp
		String title = username + ": "
				+ ((msg.length() < 5) ? msg : msg.substring(0, 5) + "...");
		Notification notification = new Notification(
				R.drawable.mimi_connect_logo16x16, title,
				System.currentTimeMillis());
		Intent i = new Intent(this, ChatManagerActivity.class);
		i.putExtra(FriendInfo.ID, username);
		i.putExtra(FriendInfo.MESSAGE, msg);
		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
		// Set the info for the views that show in the notification panel.
		// msg.length()>15 ? msg : msg.substring(0, 15);
		notification.setLatestEventInfo(this, "New message from " + username,
				msg, contentIntent);
		mNM.notify((username + msg).hashCode(), notification);
	}

	public String getUsername() {
		return username;
	}

	public boolean sendMessage(String username, String message) {
		FriendInfo friendInfo = FriendController.getFriendInfo(username);
		String IP = friendInfo.ip;
		// IP = "10.0.2.2";
		int port = Integer.parseInt(friendInfo.port);

		String msg = FriendInfo.ID + "=" + URLEncoder.encode(this.username)
				+ "&" + FriendInfo.USER_KEY + "=" + URLEncoder.encode(userKey)
				+ "&" + FriendInfo.MESSAGE + "=" + URLEncoder.encode(message)
				+ "&";

		return socketOperator.sendMessage(msg, IP, port);
	}

	public void messageReceived(String message) {
		String[] params = message.split("&");
		String username = new String();
		String userKey = new String();
		String msg = new String();
		for (int i = 0; i < params.length; i++) {
			String[] localpar = params[i].split("=");
			if (localpar[0].equals(FriendInfo.ID)) {
				username = URLDecoder.decode(localpar[1]);
			} else if (localpar[0].equals(FriendInfo.USER_KEY)) {
				userKey = URLDecoder.decode(localpar[1]);
			} else if (localpar[0].equals(FriendInfo.MESSAGE)) {
				msg = URLDecoder.decode(localpar[1]);
			}
		}
		Log.i("Message received in service", message);
		FriendInfo friend = FriendController.checkFriend(username, userKey);
		if (friend != null) {
			Intent i = new Intent(TAKE_MESSAGE);
			i.putExtra(FriendInfo.ID, friend.id);
			i.putExtra(FriendInfo.MESSAGE, msg);
			sendBroadcast(i);
			String activeFriend = FriendController.getActiveFriend();
			if (activeFriend == null || activeFriend.equals(username)) {
				// for a friend other than current chat partner, show new
				// notification
				showNotification(username, msg);
			}
			Log.i("TAKE_MESSAGE broadcast sent by im service", "");
		}

	}

	public void setUserKey(String value) {
		this.userKey = value;
	}

	public boolean isNetworkConnected() {
		return conManager.getActiveNetworkInfo().isConnected();
	}

	public boolean isUserAuthenticated() {
		return authenticatedUser;
	}

	public String getLastRawFriendList() {
		return this.rawFriendList;
	}

	@Override
	public void onDestroy() {
		Log.i("IMService is being destroyed", "...");
		super.onDestroy();
	}

	public void exit() {
		socketOperator.exit();
		socketOperator = null;
		this.stopSelf();
	}

	public String signUpUser(String usernameText, String passwordText,
			String emailText) {
		String params = "username=" + usernameText + "&password="
				+ passwordText + "&action=" + "signUpUser" + "&email="
				+ emailText + "&";

		String result = socketOperator.sendHttpRequest(params);

		return result;
	}

	public String addNewFriendRequest(String friendUsername) {
		String params = "username=" + this.username + "&password="
				+ this.password + "&action=" + "addNewFriend"
				+ "&friendUserName=" + friendUsername + "&";

		String result = socketOperator.sendHttpRequest(params);

		return result;
	}

	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames) {
		String params = "username=" + this.username + "&password="
				+ this.password + "&action=" + "responseOfFriendReqs"
				+ "&approvedFriends=" + approvedFriendNames
				+ "&discardedFriends=" + discardedFriendNames + "&";

		String result = socketOperator.sendHttpRequest(params);

		return result;

	}

	private void parseFriendInfo(String xml) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(
					IMService.this));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateData(FriendInfo[] friends,
			FriendInfo[] unApprovedFriends, String userKey) {
		this.setUserKey(userKey);
		// FriendController.
		FriendController.setFriendsInfo(friends);
		FriendController.setUnapprovedFriendsInfo(unApprovedFriends);

	}

	public String authenticateUser(String usernameText, String passwordText) {
		// TODO Auto-generated method stub
		return null;
	}

}