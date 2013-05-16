package com.variance.mimiprotect.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.backuprestore.BackupRestoreActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.Settings;

public class MimiProtectActivity extends Activity {
	private static final Set<MimiProtectActivity> activities = new HashSet<MimiProtectActivity>();
	private static volatile boolean loggedOut = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_about);
		activities.add(this);
	}

	@Override
	public void finish() {
		try {
			activities.remove(this);
			Log.i("Finishing:", getClass().getName());
			Log.i("Finishing(loggedout):", loggedOut + "");
			Log.i("Finishing(activites):", activities.size() + "");
			if (!loggedOut && activities.isEmpty()) {
				doLogoutInbackground(this, false);
			}
			super.finish();
		} finally {
			loggedOut = false;
		}
	}

	private void addLogoutOptions(Menu menu) {
		MenuItem logoutMenuItem = menu.add("Log out");
		logoutMenuItem.setIcon(R.drawable.mimi_connect_logout);
		logoutMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						logout(MimiProtectActivity.this, true);
						return true;
					}
				});
	}

	private void addUserSettingMenu(Menu menu) {
		MenuItem userSettingMenuItem = menu.add("Settings");
		userSettingMenuItem.setIcon(R.drawable.mimi_connect_settings);
		userSettingMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								MimiProtectActivity.this, "Settings",
								UserSettingActivity.class,
								R.layout.usercontact_tabview, false);
						return true;
					}
				});
	}

	private void addAboutMimiProtectMenu(Menu menu) {
		MenuItem userSettingMenuItem = menu.add("About");
		userSettingMenuItem.setIcon(R.drawable.mimi_connect_about);
		userSettingMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								MimiProtectActivity.this, "About mimi protect",
								AboutMimiprotectActivity.class,
								R.layout.usercontact_tabview);
						return true;
					}
				});
	}

	private void addBackupRestoreOptionsMenu(Menu menu) {
		MenuItem backupRestoreMenuItem = menu.add("Backup/Restore");
		backupRestoreMenuItem.setIcon(R.drawable.mimi_connect_restoreimage);
		backupRestoreMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								MimiProtectActivity.this, "Backup/Restore",
								BackupRestoreActivity.class,
								R.layout.usercontact_tabview, false);
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		addBackupRestoreOptionsMenu(menu);
		addUserSettingMenu(menu);
		addAboutMimiProtectMenu(menu);
		addLogoutOptions(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private static void doLogout(final Activity context, final boolean finish) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					PersonalPhonebookActivity.endProgress();
					Settings.releaseSessionOnLogout();
					MimiProtectGeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// To avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<MimiProtectActivity> actTmps = new ArrayList<MimiProtectActivity>(
							activities);
					for (MimiProtectActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait, logging out.....", context);
			}

		}.execute();
	}

	private static void doLogout(final Activity context, final boolean finish,
			final OnRequestComplete<Boolean> requestComplete) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					PersonalPhonebookActivity.endProgress();
					Settings.releaseSessionOnLogout();
					MimiProtectGeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// To avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<MimiProtectActivity> actTmps = new ArrayList<MimiProtectActivity>(
							activities);
					for (MimiProtectActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
					if (requestComplete != null) {
						requestComplete.requestComplete(true);
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait, logging out.....", context);
			}

		}.execute();
	}

	private static void doLogoutInbackground(final Activity context,
			final boolean finish) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					Settings.releaseSessionOnLogout();
					MimiProtectGeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// to avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<MimiProtectActivity> actTmps = new ArrayList<MimiProtectActivity>(
							activities);
					for (MimiProtectActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
				}
			}

		}.execute();
	}

	public static void logout(final Activity context, final boolean finish) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirm logout");
		builder.setMessage("Are you sure, you want to log out?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loggedOut = true;
				if (DashBoardActivity.DASH_BOARD_ACTIVITY != null
						&& DashBoardActivity.DASH_BOARD_ACTIVITY
								.getChatManager() != null) {
					DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager()
							.logout();
				}
				doLogout(context, finish);
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void logout(final Activity context, final boolean finish,
			final OnRequestComplete<Boolean> requestComplete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirm logout");
		builder.setMessage("Are you sure, you want to log out?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loggedOut = true;
				if (DashBoardActivity.DASH_BOARD_ACTIVITY != null
						&& DashBoardActivity.DASH_BOARD_ACTIVITY
								.getChatManager() != null) {
					DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager()
							.logout();
				}
				doLogout(context, finish, requestComplete);
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showYesOrNoOption(final Activity context,
			final String message, final String title,
			final OnRequestComplete<Boolean> onRequestComplete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onRequestComplete.requestComplete(true);
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onRequestComplete.requestComplete(false);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static <T> void showSelectOption(final String title,
			final Activity context, final List<T> selectionData,
			final OnRequestComplete<T> onRequestComplete,
			final StringConverter<T> converter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
				R.layout.usercontact_singlecontact_simpleview, selectionData) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = null;
				T data = selectionData.get(position);
				String dataStr = converter.toString(data);
				rowView = inflater.inflate(
						R.layout.usercontact_singlecontact_simpleview, parent,
						false);
				TextView textView = (TextView) rowView
						.findViewById(R.id.viewContact);
				textView.setText(dataStr);
				return rowView;
			}
		};
		builder.setAdapter(adapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onRequestComplete.requestComplete(selectionData.get(which));
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static interface OnRequestComplete<Result> {
		void requestComplete(Result result);
	}

	public static interface StringConverter<Ob> {
		String toString(Ob ob);
	}
}
