package com.variance.mimiprotect.contacts.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactsStoreHelper extends SQLiteOpenHelper {

	public ContactsStoreHelper(Context context) {
		super(context, ContactsTable.DATABASE_NAME, null,
				ContactsStore.DATABASE_CONTACTS_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("CREate", "creating");
		try {
			db.execSQL(ContactsStore.DATABASE_CONTACTS_CREATE);
			db.execSQL(ChatTable.CHAT_TABLE_CREATE_QUERY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.i("Drooping contacts table:", " Contact table: "
				+ ContactsTable.DATABASE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ContactsTable.DATABASE_NAME);
		onCreate(db);
	}

}
