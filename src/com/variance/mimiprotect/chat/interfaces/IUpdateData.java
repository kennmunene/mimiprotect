package com.variance.mimiprotect.chat.interfaces;

import com.variance.mimiprotect.chat.types.FriendInfo;


public interface IUpdateData {
	public void updateData(FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
