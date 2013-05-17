package com.variance.mimiprotect.ui.backuprestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.task.RestoreTask;
import com.variance.mimiprotect.internal.FileManagementActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;

public class RestoreActivity extends MimiProtectActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_backuprestore_options);
		// specify image
		ImageView imageView = (ImageView) findViewById(R.id.imgBackupRestoreTitle);
		if (imageView != null) {
			imageView
					.setBackgroundResource(R.drawable.mimi_connect_restoreimage);
		}
		TextView textView = (TextView) findViewById(R.id.txtBackRestoreTitle);
		if (textView != null) {
			textView.setText(R.string.txtRestoreOptions);
		}
	}

	public void backupAction(View view) {
		switch (view.getId()) {
		case R.id.llContactsAction:
		case R.id.llContactsActionRb:
			RestoreTask task = new RestoreTask(this);
			task.execute(new String[] { "" });
			break;
		case R.id.llDocumentsAction:
		case R.id.llMusicOrRingtonAction:
		case R.id.llPhotosAction:
		case R.id.llDocumentsActionRb:
		case R.id.llMusicOrRingtonActionRb:
		case R.id.llPhotosActionRb: {
			Intent restoreFilesIntent = new Intent(this,
					FileManagementActivity.class);
			restoreFilesIntent.putExtra(
					FileManagementActivity.BACKUP_RESTORE_CODE,
					FileManagementActivity.RESTORE_FILES_CODE);
			startActivity(restoreFilesIntent);
		}
			break;
		}
	}
}
