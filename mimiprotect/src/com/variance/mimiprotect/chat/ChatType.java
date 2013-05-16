package com.variance.mimiprotect.chat;

public enum ChatType {
	RECEIPT, SENT;
	public static ChatType fromOrdinal(int ordinal) {
		for (ChatType c : values()) {
			if (c.ordinal() == ordinal)
				return c;
		}
		return null;
	}
}
