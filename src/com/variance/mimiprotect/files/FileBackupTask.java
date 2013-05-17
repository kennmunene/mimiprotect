package com.variance.mimiprotect.files;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.variance.mimiprotect.ui.PersonalPhonebookActivity;

public class FileBackupTask extends AsyncTask<String, Void, String> {
	private FileBackup fbackup = new FileBackup();
	private String path = null;
	private Activity activity;

	public FileBackupTask(String path, Activity activity) {
		this.path = path;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress(
				"Uploading files, Please wait...", activity);
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			return fbackup.upload(path);
		} catch (Exception e) {
			Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
			return "";
		}
	}

	@Override
	protected void onPostExecute(String result) {
		PersonalPhonebookActivity.endProgress();
		activity.finish();
		if (result.equals("")) {
			Toast.makeText(activity, "Sorry! There is no internet Connection.",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
		}
	}

}
