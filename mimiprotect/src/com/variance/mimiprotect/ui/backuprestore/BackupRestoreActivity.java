package com.variance.mimiprotect.ui.backuprestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;

public class BackupRestoreActivity extends MimiProtectActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_backuprestore);
	}

	public void backupRestoreOption(View view) {
		switch (view.getId()) {
		case R.id.btnBackupOption: {
			Intent intent = new Intent(this, GeneralTabActivity.class);
			Intent callingintent = this.getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
					BackupActivity.class.getName());
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
					"mimi");
			this.startActivity(intent);
		}
			break;
		case R.id.btnRestoreOption: {
			Intent intent = new Intent(this, GeneralTabActivity.class);
			Intent callingintent = this.getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
					RestoreActivity.class.getName());
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
					"mimi");
			this.startActivity(intent);
		}
			break;
		}
	}
}
