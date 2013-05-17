package com.variance.mimiprotect.chat.tools;

import android.content.Context;

import com.variance.mimiprotect.chat.types.FriendInfo;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.cache.ContactsStore;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
public class FriendController {

	private static FriendInfo[] friendsInfo = null;
	private static FriendInfo[] unapprovedFriendsInfo = null;
	private static String activeFriend;

	public static void setFriendsInfo(FriendInfo[] friendInfo) {
		FriendController.friendsInfo = friendInfo;
	}

	public static FriendInfo checkFriend(String id, String userKey) {
		FriendInfo result = null;
		if (friendsInfo != null) {
			for (int i = 0; i < friendsInfo.length; i++) {
				if (friendsInfo[i].id.equals(id)
						&& friendsInfo[i].userKey.equals(userKey)) {
					result = friendsInfo[i];
					break;
				}
			}
		}
		return result;
	}

	public static FriendInfo checkFriend(String id, String userKey,
			Context context) {
		FriendInfo result = null;
		if (friendsInfo != null) {
			for (int i = 0; i < friendsInfo.length; i++) {
				if (friendsInfo[i].id.equals(id)
						&& friendsInfo[i].userKey.equals(userKey)) {
					result = friendsInfo[i];
					break;
				}
			}
		}
		if (result == null) {
			ContactsStore store = ContactsStore.getInstance(context);
			if (store != null) {
				Contact contact = store.getPersonalContact(id);
				if (contact != null) {
					result = new FriendInfo(contact.getId(), null, null);
				}
			}
		}
		return result;
	}

	public static void setActiveFriend(String friendName) {
		activeFriend = friendName;
	}

	public static String getActiveFriend() {
		return activeFriend;
	}

	public static FriendInfo getFriendInfo(String username) {
		FriendInfo result = null;
		if (friendsInfo != null) {
			for (int i = 0; i < friendsInfo.length; i++) {
				if (friendsInfo[i].id.equals(username)) {
					result = friendsInfo[i];
					break;
				}
			}
		}
		return result;
	}

	public static void setUnapprovedFriendsInfo(FriendInfo[] unapprovedFriends) {
		unapprovedFriendsInfo = unapprovedFriends;
	}

	public static FriendInfo[] getFriendsInfo() {
		return friendsInfo;
	}

	public static FriendInfo[] getUnapprovedFriendsInfo() {
		return unapprovedFriendsInfo;
	}

}
