package com.variance.mimiprotect.ui.contact;

import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.ui.contact.task.DiscoveryTask;
import com.variance.mimiprotect.util.Settings;

public class DiscoveryActivity extends PhonebookActivity {
	private EditText searchTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_discoversearch);
		searchTxt = (EditText) findViewById(R.id.txtSearchContacts);
		addSoftKeyEnterActionOnSearch();
	}

	public void addSoftKeyEnterActionOnSearch() {
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						searchParameter.setSearchTerm(searchTxt.getText()
								.toString());
						searchParameter.setCurrentPage(0);
						search();
						return true;
					default:
						break;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DEL:
						String txt = searchTxt.getText().toString();
						if ("".equals(txt.trim())) {
							searchParameter
									.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
							searchParameter.setCurrentPage(0);
							doBackgroundSearch();
						}
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void updateSearchParameter() {
		// TODO Auto-generated method stub
		super.updateSearchParameter();
		/**
		 * We are searching from different contacts, it is hard to determine the
		 * actual number of maximum records, without spending to much energy
		 */
		searchParameter.setMaxRecords(1000000);
	}

	@Override
	protected boolean doLoadFromCache() {
		return false;
	}

	@Override
	protected void doSearch() {
		// TODO Auto-generated method stub
		super.doSearch();
		search();
	}

	@Override
	public void search() {
		new DiscoveryTask(this, searchParameter).execute(searchParameter);
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.PUBLIC;
	}

}
