package com.variance.mimiprotect.business.directory;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.business.directory.task.BusinessDirectorySearchTask;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class BusinessDirectoryActivity extends PhonebookActivity {
	public static BusinessDirectoryActivity BUSINESS_DIRECTORY_ACTIVITY;

	private EditText searchTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businessdirectory_search);
		BUSINESS_DIRECTORY_ACTIVITY = this;
		searchTxt = (EditText) findViewById(R.id.txtBusinessSearch);
		addSoftKeyEnterActionOnSearch();
		search("mimi");
	}

	public void addSoftKeyEnterActionOnSearch() {
		searchTxt.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
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

	public void search() {
		Log.i("Business Search: ", searchTxt.getText().toString().trim());
		search(searchTxt.getText().toString().trim());
	}

	public void search(String searchTerm) {
		searchParameter.setSearchTerm(searchTerm);
		Log.i("Search Parameters: ", searchParameter.toString());
		doSearch();
	}

	protected void doSearch() {
		new BusinessDirectorySearchTask(this)
				.execute(new SearchParameter[] { searchParameter });
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
		// TODO Auto-generated method stub

	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.PUBLIC;
	}
}
