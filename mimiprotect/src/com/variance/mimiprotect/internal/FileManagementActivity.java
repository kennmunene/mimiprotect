package com.variance.mimiprotect.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.files.FileBackup;
import com.variance.mimiprotect.files.FileBackupTask;
import com.variance.mimiprotect.files.FileDownloadListAdapter;
import com.variance.mimiprotect.files.FileRestoreTask;
import com.variance.mimiprotect.integration.OIIntentIntegrator;
import com.variance.mimiprotect.util.UploadedContent;

public class FileManagementActivity extends Activity {
	public static final String BACKUP_RESTORE_CODE = "1122";
	public static final int BACKUP_FILES_CODE = 0x777;
	public static final int RESTORE_FILES_CODE = 0x666;

	public static final int REQUEST_CODE_PICK_FILE_OR_DIRECTORY = 1;
	public static final int REQUEST_CODE_GET_CONTENT = 2;
	public static final int REQUEST_CODE_PICK_DIRECTORY = 3;
	public static final int REQUEST_CODE_SELECT_DOWNLOAD_FILES = 4;

	public static final int REQUEST_CODE_SAVEVCF = 0;

	private Set<UploadedContent> selectedFilesForDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent callingIntent = getIntent();
		int code = callingIntent.getIntExtra(BACKUP_RESTORE_CODE, 0);
		switch (code) {
		case BACKUP_FILES_CODE:
			selectFile();
			break;
		case RESTORE_FILES_CODE:
			showFilesForDownload();
			break;
		}
	}

	private void showFilesForDownload() {
		final List<UploadedContent> contents = new FileBackup()
				.getBackedUpFilesList();
		if (!contents.isEmpty()) {
			final Dialog filesDownloadDlg = new Dialog(this);
			filesDownloadDlg.setTitle("Select Files to Download");
			filesDownloadDlg.setCancelable(true);
			filesDownloadDlg.setContentView(R.layout.mimi_connect_selectfiles);
			final FileDownloadListAdapter adapter = new FileDownloadListAdapter(
					this, R.layout.mimi_connect_singleselectfile, contents);
			ListView listView = (ListView) filesDownloadDlg
					.findViewById(R.id.downloadFilesView);
			listView.setAdapter(adapter);
			Button btnDownload = (Button) filesDownloadDlg
					.findViewById(R.id.btnDownloadSelectedFiles);
			btnDownload.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					selectedFilesForDownload = adapter.getSelectedFiles();
					if (selectedFilesForDownload != null
							&& !selectedFilesForDownload.isEmpty()) {
						selectFileRestoreDirectory();
					}
					filesDownloadDlg.dismiss();
				}
			});
			filesDownloadDlg.show();
		} else {
			Toast.makeText(this, "Sorry you do not have files for download",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void selectFileRestoreDirectory() {
		OIIntentIntegrator integrator = new OIIntentIntegrator(
				FileManagementActivity.this);
		Map<String, String> args = new HashMap<String, String>();
		args.put("Forg.openintents.extra.TITLE", "Save Here");
		args.put("org.openintents.extra.BUTTON_TEXT", "Save Here");
		args.put("org.openintents.extra.WRITEABLE_ONLY", "1");
		String action = "org.openintents.action.PICK_DIRECTORY";
		int requestCode = REQUEST_CODE_PICK_DIRECTORY;
		integrator.initiateFileSelection(action, requestCode, args);
	}

	private void restoreSelectedFiles(String directoryPath) {
		FileRestoreTask fileRestoreTask = new FileRestoreTask(
				new ArrayList<UploadedContent>(selectedFilesForDownload),
				directoryPath, this);
		fileRestoreTask.execute();
		Log.e("FilesListDownloadActivity", "finished downloading file");
	}

	public void restoreFiles(View view) {
		showFilesForDownload();
	}

	public void backupFiles(View view) {
		selectFile();
	}

	public void upload(final String path) {
		Log.e("Extrator", "file upload dialog to be up");
		try {
			FileBackupTask backupTask = new FileBackupTask(path, this);
			backupTask.execute(new String[] { "" });
		} catch (Exception e) {
			Log.e("Extrator", e.toString());
		}
	}

	public void selectFile() {
		OIIntentIntegrator integrator = new OIIntentIntegrator(this);
		Map<String, String> args = new HashMap<String, String>();
		args.put("Forg.openintents.extra.TITLE", "Select File");
		args.put("org.openintents.extra.BUTTON_TEXT", "UPLOAD FILE");
		String action = "org.openintents.action.PICK_FILE";
		int requestCode = REQUEST_CODE_PICK_FILE_OR_DIRECTORY;
		integrator.initiateFileSelection(action, requestCode, args);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_PICK_FILE_OR_DIRECTORY:
			if (resultCode == RESULT_OK && data != null) {
				// obtain the filename
				Uri fileUri = data.getData();
				if (fileUri != null) {
					String filePath = fileUri.getPath();
					if (filePath != null) {
						Log.e("FileManagementActivity", "uploading " + filePath);
						upload(filePath);
					}
				}
			}
			break;
		case REQUEST_CODE_PICK_DIRECTORY:
			Log.e("Files List", "directory picked ");
			if (resultCode == RESULT_OK && data != null) {
				Uri fileUri = data.getData();
				if (fileUri != null) {
					String directoryPath = fileUri.getPath();
					if (directoryPath != null) {
						Log.e("FileManagementActivity", "downloading to "
								+ directoryPath);
						restoreSelectedFiles(directoryPath);
					}
				}
			}
			break;
		}
	}

}