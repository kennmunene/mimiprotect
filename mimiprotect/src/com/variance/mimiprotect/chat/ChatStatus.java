package com.variance.mimiprotect.chat;

import java.io.Serializable;

public class ChatStatus implements Serializable {
	private static final long serialVersionUID = -2391181547298604662L;
	int AccountType;
	public static final int AccountType_FACEBOOK = 0;
	public static final int AccountType_GMAIL = 1;
	public static final int AccountType_MIMI = 2;
	int status;
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_OFFLINE = 0;
	String id;

	public ChatStatus() {

	}

	public String getId() {
		return id;
	}

	public int getStatus() {
		return status;
	};

	@Override
	public String toString() {
		return "Id: " + id + ", status: "
				+ ((status == STATUS_OFFLINE) ? "offline" : "online");
	}
}
