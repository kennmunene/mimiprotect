package com.variance.mimiprotect.chat.types;

public class FriendInfo {

	public static final String FRIEND_LIST = "friendList";
	public static final String ID = "username";
	public static final String IP = "IP";
	public static final String PORT = "port";
	public static final String USER_KEY = "userKey";
	public static final String MESSAGE = "message"; // this should not be in
													// here
	public static final String STATUS = "status";

	public STATUS status;
	public String id;
	public String ip;
	public String port;
	public String userKey;
	public String expire;

	public FriendInfo() {
		super();
	}

	public FriendInfo(String id, String ip, String port) {
		super();
		this.id = id;
		this.ip = ip;
		this.port = port;
	}

};
