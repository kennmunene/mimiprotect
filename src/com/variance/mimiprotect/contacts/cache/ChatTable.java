package com.variance.mimiprotect.contacts.cache;

public class ChatTable {
	/**
	 * Chat table name
	 */
	public static final String CHAT_TABLE_NAME = "mimi_protect_chat_001";
	/**
	 * Chat message columns.
	 */
	public static final String COLUMN_MESSAGE_ID = "message_id";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_CHAT_TYPE = "type";
	public static final String COLUMN_CONTACT_ID = "id";
	/**
	 * Chat table create sql
	 */
	public static final String CHAT_TABLE_CREATE_QUERY = "CREATE TABLE "
			+ CHAT_TABLE_NAME + " ( " + COLUMN_MESSAGE_ID + " TEXT,"
			+ COLUMN_MESSAGE + " TEXT, " + COLUMN_CHAT_TYPE + " INTEGER, "
			+ COLUMN_CONTACT_ID + " TEXT );";
	public static final String CHAT_COLUMNS[] = { COLUMN_MESSAGE_ID,
			COLUMN_MESSAGE, COLUMN_CHAT_TYPE, COLUMN_CONTACT_ID };
	/**
	 * Column indeces as they are retrieved from the database.
	 */
	public static final int COLUMN_INDEX_MESSAGE_ID = 0;
	public static final int COLUMN_INDEX_MESSAGE = 1;
	public static final int COLUMN_INDEX_CHAT_TYPE = 2;
	public static final int COLUMN_INDEX_CONTACT_ID = 3;
}
