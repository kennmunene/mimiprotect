package com.variance.mimiprotect.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.integration.IntentIntegrator;
import com.variance.mimiprotect.integration.IntentResult;
import com.variance.mimiprotect.util.ContactVCardHandler;

public class ScanCardActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_newcontact);
		scanBusinessCard();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	public void scanBusinessCard(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
	}

	public void scanBusinessCard() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		try {
			IntentResult result = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (result != null) {
				String format = result.getFormatName();
				if (format.contains("QR")) {
					String vcard = result.getContents();
					Toast.makeText(this, vcard, Toast.LENGTH_SHORT).show();
					new ContactVCardHandler(this).createContact(vcard, true,
							true);
				}
			} else {
				Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Error:" + e.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

}
