package com.variance.mimiprotect.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.User;
import com.variance.mimiprotect.contacts.Users;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class MyLivelinkActivity extends MimiProtectActivity {
	private ListView listView;
	private List<User> selectedUsers;
	private List<CheckBox> selectionCb;
	private Users users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_mylivelinks);
		listView = (ListView) findViewById(R.id.listMylivelinks);
		selectedUsers = new ArrayList<User>();
		selectionCb = new ArrayList<CheckBox>();
		checkAllOptions();
		loadMyLivelinksIfAny();
	}

	private void checkAllOptions() {
		final CheckBox allCb = (CheckBox) findViewById(R.id.cbSelectAll);
		allCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				selectedUsers.clear();
				if (isChecked) {
					selectedUsers.addAll(users.getUsers());
				}
				for (CheckBox c : selectionCb) {
					c.setChecked(isChecked);
				}
			}
		});
	}

	private void loadMyLivelinksIfAny() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				PersonalPhonebookActivity.endProgress();
				users = DataParser.getUsersFrom(result);
				showLivelinkedUsers();
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						MyLivelinkActivity.this, this);
			}

			@Override
			protected String doInBackground(Void... params) {
				return HttpRequestManager.doRequest(
						Settings.getProfileRequestUrl(),
						Settings.getLoadMyLivelinksParameter(),
						MyLivelinkActivity.this);
			}

		}.execute();
	}

	private void showLivelinkedUsers() {

		View noLivelinks = findViewById(R.id.noLivelinks);
		if (users != null && users.getUsers() != null
				&& !users.getUsers().isEmpty()) {
			noLivelinks.setVisibility(View.GONE);
		} else {
			noLivelinks.setVisibility(View.VISIBLE);
		}
		ArrayAdapter<User> adapter = new ArrayAdapter<User>(
				MyLivelinkActivity.this,
				R.layout.usercontact_mylivelink_singleview, users.getUsers()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = null;
				User user = users.getUsers().get(position);
				rowView = inflater.inflate(
						R.layout.usercontact_mylivelink_singleview, parent,
						false);
				CheckBox usercontact_selectBtn = (CheckBox) rowView
						.findViewById(R.id.usercontact_selectBtn);
				addCheckBoxListener(usercontact_selectBtn, position);
				usercontact_selectBtn.setChecked(selectedUsers.contains(user));
				TextView usercontact_contactTextView = (TextView) rowView
						.findViewById(R.id.usercontact_contactTextView);
				String name = "";
				if (!Utils.isNullStringOrEmpty(user.getSurname())) {
					name += user.getSurname() + " ";
				}
				if (!Utils.isNullStringOrEmpty(user.getOtherNames())) {
					name += user.getOtherNames();
				}
				usercontact_contactTextView.setText(name);
				return rowView;
			}

			private void addCheckBoxListener(final CheckBox cb,
					final int position) {
				selectionCb.add(cb);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						User user = users.getUsers().get(position);
						if (isChecked) {
							selectedUsers.add(user);
						} else {
							selectedUsers.remove(user);
						}
					}
				});
			}

		};
		listView.setAdapter(adapter);
	}

	public void handleRevokeLivelinks(View view) {
		if (selectedUsers.isEmpty()) {
			Toast.makeText(this,
					"You must select at least one user to revoke livelink on.",
					Toast.LENGTH_LONG).show();
			return;
		}

		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(MyLivelinkActivity.this, result,
							Toast.LENGTH_LONG).show();
				}
				users.getUsers().removeAll(selectedUsers);
				selectedUsers.clear();
				selectionCb.clear();
				showLivelinkedUsers();
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait. Revoking...", MyLivelinkActivity.this,
						this);
			}

			@Override
			protected String doInBackground(Void... params) {
				return HttpRequestManager.doRequest(Settings
						.getProfileRequestUrl(),
						Settings.getRevokeMyLivelinksParameter(new Users(
								selectedUsers)), MyLivelinkActivity.this);
			}

		}.execute();
	}
}
