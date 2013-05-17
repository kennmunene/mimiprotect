package com.variance.mimiprotect.contacts.business;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.task.BusinessContactLoadFromPersonalContactTask;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class SelectBusinessContactFromPersonalContactActivity extends
		PhonebookActivity {
	private EditText searchTxt;
	private BusinessContactLoadFromPersonalContactTask contactTask;
	private List<Contact> selectedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscontact_selectfrompersonalcontacts);
		searchTxt = (EditText) findViewById(R.id.txtSearchPersonalContact);
		selectedContacts = new ArrayList<Contact>();
		initSoftKey();
		searchParameter.setSearchTerm(Settings.INIT_ANDROID_LOAD_TXT);
		doSearch();
	}

	@Override
	protected void doSearch() {
		updateSearchParameter();
		contactTask = new BusinessContactLoadFromPersonalContactTask(this,
				selectedContacts);
		contactTask.execute(searchParameter);
	}

	public void search() {
		String search = searchTxt.getText().toString();
		if (search != null) {
			String searchName = search.trim();
			if (searchParameter != null) {
				searchParameter.setSearchTerm(searchName);
				searchParameter.setCurrentPage(0);
			} else {
				searchParameter = new SearchParameter(searchName, 0,
						PersonalPhonebookActivity.getMaximumListRows(this, 30));
			}
			doSearch();
		}
	}

	public void handleAddBusinessContactsFromPersonalContacts(View view) {
		contactTask.addBusinessContacts();
	}

	private void initSoftKey() {
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						String searchText = searchTxt.getText().toString();
						Log.i("Search Personal Contact: ", searchText);
						searchParameter.setSearchTerm(searchText);
						searchParameter.setCurrentPage(0);
						selectedContacts.clear();
						doSearch();
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.OFFICE;
	}
}
