/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.mimiprotect.data.sync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.variance.vjax.android.annotations.GenericCollectionType;

/**
 * 
 * @author marembo
 */
public class ContactSyncStateValues implements Iterable<ContactSyncStateValue> {
	@GenericCollectionType(ContactSyncStateValue.class)
	private List<ContactSyncStateValue> hashes;

	public ContactSyncStateValues() {
		hashes = new ArrayList<ContactSyncStateValue>();
	}

	public List<ContactSyncStateValue> getHashes() {
		return hashes;
	}

	public void setHashes(List<ContactSyncStateValue> hashes) {
		this.hashes = hashes;
	}

	public Iterator<ContactSyncStateValue> iterator() {
		return hashes.iterator();
	}
}
