package com.variance.mimiprotect.contacts.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ListView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.ui.PhonebookActivity;

public class ContactViewManager {

	private ArrayList<Contact> businessContacts;
	private ArrayList<Contact> personalContacts;
	private Activity parentActivity;
	private ContactListAdapter listAdapter;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();

	public ContactViewManager() {
	}

	public ContactViewManager(ArrayList<Contact> businessContacts,
			ArrayList<Contact> personalContacts, Activity parentActivity) {
		this.businessContacts = businessContacts;
		this.personalContacts = personalContacts;
		this.parentActivity = parentActivity;
	}

	private Comparator<Contact> getPersonalContactComparator() {
		return new Comparator<Contact>() {

			public int compare(Contact lhs, Contact rhs) {
				String name0 = lhs.getName();
				String name1 = rhs.getName();
				if (parentActivity instanceof PhonebookActivity) {
					PhonebookActivity act = (PhonebookActivity) parentActivity;
					String searchTerm = act.getSearchParameter()
							.getSearchTerm();
					Log.e("getPersonalContactComparator", searchTerm+"");
					boolean name0Starts = name0.startsWith(searchTerm);
					boolean name1Starts = name1.startsWith(searchTerm);
					return ((name0Starts && name1Starts) || (!name0Starts && !name1Starts)) ? name0
							.compareToIgnoreCase(name1) : (name0Starts) ? -1
							: 1;
				}
				return lhs.compareTo(rhs);
			}
		};
	}

	public void initialize(int listViewId, boolean showPersonalContacts,
			boolean showBusinessListings, boolean demarcateView) {
		ListView contactView = (ListView) parentActivity
				.findViewById(listViewId);
		if (this.personalContacts != null && !this.personalContacts.isEmpty()) {
			if (showPersonalContacts) {
				// demarcate the groups if any
				Collections.sort(personalContacts,
						getPersonalContactComparator());
				demarcateGroups();
				contacts.addAll(personalContacts);
			}
		}
		if (this.businessContacts != null && !this.businessContacts.isEmpty()) {
			if (showBusinessListings) {
				contacts.addAll(businessContacts);
			}
		}
		normalizeContacts(contacts, showPersonalContacts, showBusinessListings);
		listAdapter = new ContactListAdapter(parentActivity,
				R.layout.usercontact_singlecontactview, contacts,
				showPersonalContacts, showBusinessListings);
		contactView.setAdapter(listAdapter);
	}

	public synchronized void onNextContactsReceived(Collection<Contact> contacts) {
		if (this.listAdapter != null) {
			for (Contact c : contacts) {
				this.listAdapter.add(c);
			}
			this.listAdapter.notifyDataSetChanged();
		}
	}

	private void normalizeContacts(ArrayList<Contact> contacts,
			boolean showPersonalContacts, boolean showBusinessContacts) {
		for (ListIterator<Contact> it = contacts.listIterator(); it.hasNext();) {
			Contact c = it.next();
			if ((c.isCorporateContact() && !showBusinessContacts)
					|| (c.isBusinessContactHeaderStart() && !showBusinessContacts)) {
				it.remove();
			} else if ((c.isPersonalContactGroupHeaderStart() && !showPersonalContacts)
					|| (c.isPersonalContactHeaderStart() && !showPersonalContacts)) {
				it.remove();
			} else if (c.isDummyContac() && contacts.size() > 2) {
				it.remove();
			}
		}
	}

	private void demarcateGroups() {
		String prevGroup = null;
		SparseArray<Contact> list = new SparseArray<Contact>();
		int index = 0;
		for (ListIterator<Contact> it = personalContacts.listIterator(); it
				.hasNext();) {
			Contact c = it.next();
			if ((prevGroup == null && c.getGroup() != null)
					|| (prevGroup != null && c.getGroup() != null && !prevGroup
							.equalsIgnoreCase(c.getGroup()))) {
				Contact dummy = new Contact();
				dummy.setName(c.getGroup());
				dummy.setPersonalContactGroupHeaderStart(true);
				list.put(index, dummy);
				Log.i("Index", index + "");
				prevGroup = c.getGroup();
			} else if (prevGroup != null && c.getGroup() == null) {
				Log.i("Default Group", prevGroup);
				Log.i("Index", index + "");
				Contact dummy = new Contact();
				dummy.setName("");
				dummy.setPersonalContactGroupHeaderStart(true);
				list.put(index, dummy);
				prevGroup = null;
			}
			index++;
		}
		int pos = 0;
		for (int i = 0; i < list.size(); i++) {
			int key = list.keyAt(i);
			Log.i("i" + key, (pos + key) + ":" + list.get(key));
			personalContacts.add(pos + key, list.get(key));
			pos++;
		}
	}
}
