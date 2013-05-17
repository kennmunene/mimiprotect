/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.mimiprotect.data.sync;


/**
 *
 * @author marembo
 */
public class ContactSyncStateValue {
  private int hashId;
  private String contactVcard;
  private ContactSyncStateType syncState = ContactSyncStateType.CONTACT_SYNCED;

  public ContactSyncStateValue() {
  }

  public ContactSyncStateType getSyncState() {
    return syncState;
  }

  public void setSyncState(ContactSyncStateType syncState) {
    this.syncState = syncState;
  }

  public String getContactVcard() {
    return contactVcard;
  }

  public void setContactVcard(String contactVcard) {
    this.contactVcard = contactVcard;
  }

  public int getHashId() {
    return hashId;
  }

  public void setHashId(int hashId) {
    this.hashId = hashId;
  }

}
