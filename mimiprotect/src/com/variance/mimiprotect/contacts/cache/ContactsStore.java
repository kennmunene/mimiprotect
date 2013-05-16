package com.variance.mimiprotect.contacts.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.variance.mimiprotect.chat.ChatMessage;
import com.variance.mimiprotect.chat.ChatType;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.util.Utils;

public class ContactsStore {
	public static final String DATABASE_CONTACTS_CREATE = "CREATE TABLE "
			+ ContactsTable.DATABASE_NAME + " ( " + ContactsTable.column_autoid
			+ " integer primary key , " + ContactsTable.column_id
			+ "  TEXT UNIQUE , " + ContactsTable.column_type + "  integer , "
			+ ContactsTable.column_name + "  TEXT , "
			+ ContactsTable.column_phones + "  TEXT, "
			+ ContactsTable.column_emails + "  TEXT, "
			+ ContactsTable.column_website + "  TEXT, "
			+ ContactsTable.column_address + "  TEXT, "
			+ ContactsTable.column_livelinkid + "  TEXT, "
			+ ContactsTable.column_country + "  TEXT, "
			+ ContactsTable.column_group + "  TEXT, "
			+ ContactsTable.column_message + "  TEXT, "
			+ ContactsTable.column_companyName + "  TEXT, "
			+ ContactsTable.column_organization + "  TEXT, "
			+ ContactsTable.column_title + "  TEXT, "
			+ ContactsTable.column_corporateContact + "  integer, "
			+ ContactsTable.column_businessContact + "  integer, "
			+ ContactsTable.column_businessHeader + "  integer, "
			+ ContactsTable.column_personalContactHeader + "  integer, "
			+ ContactsTable.column_personalContactGroupHeaderStart
			+ "  integer, " + ContactsTable.column_nocontactFound + "  integer"
			+ ");";
	public static final int DATABASE_CONTACTS_VERSION = 6;
	private ContactsStoreHelper helper;
	private SQLiteDatabase contactsDB;
	private static ContactsStore store;

	public boolean savePersonalContact(Contact contact) {
		contact.setContactType(ContactsTable.CONTACT_TYPE_PERSONAL);
		return saveContact(contact);
	}

	public boolean saveOfficeContact(Contact contact) {
		contact.setContactType(ContactsTable.CONTACT_TYPE_OFFICE);
		return saveContact(contact);
	}

	public boolean savePublicContact(Contact contact) {
		contact.setContactType(ContactsTable.CONTACT_TYPE_PUBLIC);
		return saveContact(contact);
	}

	private boolean saveContact(Contact contact) {
		if (contactExists(contact)) {
			return updateContact(contact);
		}
		try {
			contactsDB = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			// values.put(ContactsTable.column_autoid, contact.getAutoid());
			values.put(ContactsTable.column_id, contact.getId());
			values.put(ContactsTable.column_type, contact.getContactType());
			values.put(ContactsTable.column_address, contact.getAddress());
			values.put(ContactsTable.column_businessContact,
					CacheUtils.getIntValue(contact.isBusinessContact()));
			values.put(ContactsTable.column_businessHeader, CacheUtils
					.getIntValue(contact.isBusinessContactHeaderStart()));
			values.put(ContactsTable.column_companyName,
					contact.getCompanyName());
			values.put(ContactsTable.column_corporateContact,
					CacheUtils.getIntValue(contact.isCorporateContact()));
			values.put(ContactsTable.column_country, contact.getCountry());
			values.put(ContactsTable.column_emails,
					CacheUtils.convertArrayToString(contact.getEmails()));
			values.put(ContactsTable.column_group, contact.getGroup());
			values.put(ContactsTable.column_livelinkid, contact.getLivelinkId());
			values.put(ContactsTable.column_message, contact.getMessage());
			values.put(ContactsTable.column_name, contact.getName());
			values.put(ContactsTable.column_nocontactFound,
					CacheUtils.getIntValue(contact.isNoContactFound()));
			values.put(ContactsTable.column_organization,
					contact.getOrganization());
			values.put(ContactsTable.column_personalContactGroupHeaderStart,
					CacheUtils.getIntValue(contact
							.isPersonalContactGroupHeaderStart()));
			values.put(ContactsTable.column_personalContactHeader, CacheUtils
					.getIntValue(contact.isPersonalContactHeaderStart()));
			values.put(ContactsTable.column_phones,
					CacheUtils.convertArrayToString(contact.getPhones()));
			values.put(ContactsTable.column_title, contact.getTitle());
			long res = contactsDB.insert(ContactsTable.DATABASE_NAME, null,
					values);
			return res == -1 ? false : true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return false;
	}

	public boolean deleteContact(Contact contact) {
		try {
			String where = ContactsTable.column_id + " = " + contact.getId();
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB.delete(ContactsTable.DATABASE_NAME, where,
					null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	public boolean deleteContacts(List<Contact> contacts) {
		try {
			String where = "";
			for (Contact contact : contacts) {
				if (!Utils.isNullOrEmpty(where)) {
					where += " OR ";
				}
				where += ContactsTable.column_id + " = " + contact.getId();
			}
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB.delete(ContactsTable.DATABASE_NAME, where,
					null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	public boolean clearCache() {
		try {
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB
					.delete(ContactsTable.DATABASE_NAME, null, null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	public boolean clearDatabase() {
		try {
			contactsDB = helper.getWritableDatabase();
			String sql = "DROP TABLE IF EXISTS " + ContactsTable.DATABASE_NAME;
			contactsDB.execSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			contactsDB.close();
		}
	}

	public boolean replaceContact(Contact oldContact, Contact newContact) {
		return (deleteContact(oldContact) && saveContact(newContact));
	}

	public boolean updateContact(Contact contact) {
		try {
			contactsDB = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			// values.put(ContactsTable.column_autoid, contact.getAutoid());
			values.put(ContactsTable.column_id, contact.getId());
			values.put(ContactsTable.column_type, contact.getContactType());
			values.put(ContactsTable.column_address, contact.getAddress());
			values.put(ContactsTable.column_businessContact,
					CacheUtils.getIntValue(contact.isBusinessContact()));
			values.put(ContactsTable.column_businessHeader, CacheUtils
					.getIntValue(contact.isBusinessContactHeaderStart()));
			values.put(ContactsTable.column_companyName,
					contact.getCompanyName());
			values.put(ContactsTable.column_corporateContact,
					CacheUtils.getIntValue(contact.isCorporateContact()));
			values.put(ContactsTable.column_country, contact.getCountry());
			values.put(ContactsTable.column_emails,
					CacheUtils.convertArrayToString(contact.getEmails()));
			values.put(ContactsTable.column_group, contact.getGroup());
			values.put(ContactsTable.column_livelinkid, contact.getLivelinkId());
			values.put(ContactsTable.column_message, contact.getMessage());
			values.put(ContactsTable.column_name, contact.getName());
			values.put(ContactsTable.column_nocontactFound,
					CacheUtils.getIntValue(contact.isNoContactFound()));
			values.put(ContactsTable.column_organization,
					contact.getOrganization());
			values.put(ContactsTable.column_personalContactGroupHeaderStart,
					CacheUtils.getIntValue(contact
							.isPersonalContactGroupHeaderStart()));
			values.put(ContactsTable.column_personalContactHeader, CacheUtils
					.getIntValue(contact.isPersonalContactHeaderStart()));
			values.put(ContactsTable.column_phones,
					CacheUtils.convertArrayToString(contact.getPhones()));
			values.put(ContactsTable.column_title, contact.getTitle());
			String whereClause = ContactsTable.column_id + "='"
					+ contact.getId() + "'";
			long res = contactsDB.update(ContactsTable.DATABASE_NAME, values,
					whereClause, null);
			return res <= 0 ? false : true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return false;
	}

	public boolean contactExists(Contact contact) {
		String where = ContactsTable.column_id + " = ? ";
		List<Contact> contacts = getContacts(where,
				new String[] { contact.getId() });
		return contacts.size() == 1
				&& contacts.get(0).getId().equals(contact.getId());
	}

	public ArrayList<Contact> getOfficeContacts() {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_OFFICE;
		return getContacts(where);
	}

	public ArrayList<Contact> getOfficeContacts(String name) {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_OFFICE + " AND ("
				+ ContactsTable.column_name + " LIKE '" + name + "%' OR "
				+ ContactsTable.column_name + " LIKE '% " + name + "%')";
		return getContacts(where);
	}

	public ArrayList<Contact> getPublicContacts() {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_PUBLIC;
		return getContacts(where);
	}

	public ArrayList<Contact> getPersonalContacts() {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_PERSONAL;
		return getContacts(where);
	}

	public ArrayList<Contact> getPersonalContacts(String name) {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_PERSONAL + " AND ("
				+ ContactsTable.column_name + " LIKE '" + name + "%' OR "
				+ ContactsTable.column_name + " LIKE '% " + name + "%')";
		return getContacts(where);
	}

	private ArrayList<Contact> getContacts(String where) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			contactsDB = helper.getReadableDatabase();
			String order = ContactsTable.column_name;
			Cursor cursor = contactsDB.query(ContactsTable.DATABASE_NAME,
					ContactsTable.allcolumns, where, null, null, null, order);
			Contact contact;
			while (cursor.moveToNext()) {
				try {
					contact = cursorToContact(cursor);
					contacts.add(contact);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return contacts;
	}

	public boolean saveChatMessage(ChatMessage message) {
		return saveChatMessage0(message);
	}

	public boolean deleteChatMessage(String messageId) {
		return deleteChatMessage0(messageId);
	}

	public boolean deleteAllChatMessages() {
		return deleteAllChatMessage0();
	}

	public boolean deleteChatMessage(List<ChatMessage> messages) {
		return deleteChatMessage0(messages);
	}

	public boolean deleteAllChatMessage0() {
		try {
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB.delete(ChatTable.CHAT_TABLE_NAME, null, null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	private boolean deleteChatMessage0(List<ChatMessage> messages) {
		try {
			String where = "";
			for (ChatMessage m : messages) {
				if (!Utils.isNullOrEmpty(where)) {
					where += " OR ";
				}
				where += ChatTable.COLUMN_MESSAGE_ID + " = '"
						+ m.getChatMessageId() + "'";
			}
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB.delete(ChatTable.CHAT_TABLE_NAME, where, null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	public boolean deleteChatMessage0(String messageId) {
		try {
			String where = ChatTable.COLUMN_MESSAGE_ID + "='" + messageId + "'";
			contactsDB = helper.getWritableDatabase();
			int res = contactsDB.delete(ChatTable.CHAT_TABLE_NAME, where, null);
			return res == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			contactsDB.close();
		}
	}

	private boolean saveChatMessage0(ChatMessage message) {
		contactsDB = helper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(ChatTable.COLUMN_MESSAGE_ID, message.getChatMessageId());
			values.put(ChatTable.COLUMN_MESSAGE, message.getMessage());
			values.put(ChatTable.COLUMN_CHAT_TYPE, message.getChatType()
					.ordinal());
			values.put(ChatTable.COLUMN_CONTACT_ID, message.getContactId());
			long res = contactsDB.insert(ChatTable.CHAT_TABLE_NAME, null,
					values);
			return res == -1 ? false : true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return false;
	}

	public ArrayList<ChatMessage> getContactChatMessages(String contactId) {
		String where = ChatTable.COLUMN_CONTACT_ID + " = '" + contactId + "'";
		return getChatMessages(where);
	}

	private ArrayList<ChatMessage> getChatMessages(String where) {
		ArrayList<ChatMessage> chats = new ArrayList<ChatMessage>();
		try {
			contactsDB = helper.getReadableDatabase();
			Cursor cursor = contactsDB.query(ChatTable.CHAT_TABLE_NAME,
					ChatTable.CHAT_COLUMNS, where, null, null, null, null);
			ChatMessage chatMessage;
			while (cursor.moveToNext()) {
				try {
					chatMessage = cursorToMessage(cursor);
					chats.add(chatMessage);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return chats;
	}

	public ArrayList<ChatMessage> getChatMessages(String where, String[] args) {
		ArrayList<ChatMessage> chats = new ArrayList<ChatMessage>();
		try {
			contactsDB = helper.getReadableDatabase();
			Cursor cursor = contactsDB.query(ChatTable.CHAT_TABLE_NAME,
					ChatTable.CHAT_COLUMNS, where, args, null, null, null);
			ChatMessage chatMessage;
			while (cursor.moveToNext()) {
				try {
					chatMessage = cursorToMessage(cursor);
					chats.add(chatMessage);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return chats;
	}

	private ArrayList<Contact> getContacts(String where, String[] args) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			contactsDB = helper.getReadableDatabase();
			String order = ContactsTable.column_name;
			Cursor cursor = contactsDB.query(ContactsTable.DATABASE_NAME,
					ContactsTable.allcolumns, where, args, null, null, order);
			Contact contact;
			while (cursor.moveToNext()) {
				try {
					contact = cursorToContact(cursor);
					contacts.add(contact);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contactsDB.close();
		}
		return contacts;
	}

	public void close() {
		try {
			helper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			store = null;
			helper = null;
		}
	}

	public static ContactsStore getInstance(Context context) {
		if (store == null) {
			store = new ContactsStore(context);
		}
		return store;
	}

	private ContactsStore(Context context) {
		helper = new ContactsStoreHelper(context);
	}

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact();
		contact.setAutoid(cursor.getInt(ContactsTable.index_autoid));
		contact.setAddress(cursor.getString(ContactsTable.index_address));
		contact.setBusinessContact(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_businessContact)));
		contact.setBusinessContactHeaderStart(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_businessHeader)));
		contact.setCompanyName(cursor
				.getString(ContactsTable.index_companyName));
		contact.setContactType(cursor.getInt(ContactsTable.index_type));
		contact.setCorporateContact(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_corporateContact)));
		contact.setCountry(cursor.getString(ContactsTable.index_country));
		contact.setEmails(CacheUtils.convertStringToArray(cursor
				.getString(ContactsTable.index_emails)));
		contact.setGroup(cursor.getString(ContactsTable.index_group));
		contact.setId(cursor.getString(ContactsTable.index_id));
		contact.setLivelinkId(cursor.getString(ContactsTable.index_livelinkid));
		contact.setMessage(cursor.getString(ContactsTable.index_message));
		contact.setName(cursor.getString(ContactsTable.index_name));
		contact.setNoContactFound(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_nocontactFound)));
		contact.setOrganization(cursor
				.getString(ContactsTable.index_organization));
		contact.setPersonalContactGroupHeaderStart(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_personalContactGroupHeaderStart)));
		contact.setPersonalContactHeaderStart(CacheUtils.getBooleanValue(cursor
				.getInt(ContactsTable.index_personalContactHeader)));
		contact.setPhones(CacheUtils.convertStringToArray(cursor
				.getString(ContactsTable.index_phones)));
		contact.setTitle(cursor.getString(ContactsTable.index_title));
		contact.setWebsite(cursor.getString(ContactsTable.index_website));
		return contact;
	}

	private ChatMessage cursorToMessage(Cursor cursor) {
		ChatMessage message = new ChatMessage();
		message.setChatMessageId(cursor
				.getString(ChatTable.COLUMN_INDEX_MESSAGE_ID));
		message.setMessage(cursor.getString(ChatTable.COLUMN_INDEX_MESSAGE));
		message.setContactId(cursor
				.getString(ChatTable.COLUMN_INDEX_CONTACT_ID));
		int chatType = cursor.getInt(ChatTable.COLUMN_INDEX_CHAT_TYPE);
		ChatType type = ChatType.fromOrdinal(chatType);
		message.setChatType(type);
		return message;
	}

	public Contact getPersonalContact(String id) {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_PERSONAL + " AND ("
				+ ContactsTable.column_id + " ='" + id + "')";
		ArrayList<Contact> contacts = getContacts(where);
		if (contacts != null && contacts.size() == 1) {
			return contacts.get(0);
		}
		return null;
	}

	public Contact getLivelinkedPersonalContact(String livelinkedId) {
		String where = ContactsTable.column_type + " = "
				+ ContactsTable.CONTACT_TYPE_PERSONAL + " AND ("
				+ ContactsTable.column_livelinkid + " ='" + livelinkedId + "')";
		ArrayList<Contact> contacts = getContacts(where);
		if (contacts != null && !contacts.isEmpty()) {
			return contacts.get(0);
		}
		return null;
	}
}
