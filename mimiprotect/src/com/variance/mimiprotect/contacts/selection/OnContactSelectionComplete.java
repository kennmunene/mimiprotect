package com.variance.mimiprotect.contacts.selection;

import java.util.List;

import com.variance.mimiprotect.contacts.Contact;

public interface OnContactSelectionComplete {
	void contactSelected(List<Contact> selectedContacts);
}
