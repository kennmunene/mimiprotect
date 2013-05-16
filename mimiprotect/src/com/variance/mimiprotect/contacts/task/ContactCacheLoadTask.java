package com.variance.mimiprotect.contacts.task;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.managers.ContactViewManager;

public class ContactCacheLoadTask {
	private Context context;
	private ContactViewManager contactViewManager;

	public ContactCacheLoadTask(Context context) {
		super();
		this.context = context;
	}

	public ContactCacheLoadTask(Context context, boolean backgroundExecute) {
		super();
		this.context = context;
	}

	public void loadCachedView(List<Contact> contacts) {
		ArrayList<Contact> cachedContacts = new ArrayList<Contact>(contacts);
		contactViewManager = new ContactViewManager(cachedContacts,
				cachedContacts, (Activity) context);
		boolean demarcate = false;
		contactViewManager.initialize(R.id.personalContactView, true, false,
				demarcate);
	}
}
