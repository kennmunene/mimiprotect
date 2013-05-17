package com.variance.mimiprotect.files;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.UploadedContent;

public class FileRestoreTask extends AsyncTask<String, Void, String> {
	private FileBackup fbackup = new FileBackup();
	private List<UploadedContent> selectedFilesForDownload = null;
	private String directoryPath;
	private Activity activity;

	public FileRestoreTask(List<UploadedContent> selectedFilesForDownload,
			String directoryPath, Activity activity) {
		this.selectedFilesForDownload = selectedFilesForDownload;
		this.directoryPath = directoryPath;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress(
				"Restoring files. Please wait...", activity);
	}

	@Override
	protected String doInBackground(String... params) {
		Iterator<UploadedContent> it = selectedFilesForDownload.iterator();
		while (it.hasNext()) {
			try {
				UploadedContent uc = it.next();
				String fileName = uc.getFileName();
				String filePath = directoryPath.concat("/").concat(fileName);
				Log.e("Files List", uc.getFileID() + " " + fileName);
				fbackup.restoreFile(uc.getFileID() + "", filePath);
			} catch (Exception e) {
			}
		}
		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		PersonalPhonebookActivity.endProgress();
		Toast.makeText(activity, "Restoring Complete!", Toast.LENGTH_LONG)
				.show();
		activity.finish();
	}
}
