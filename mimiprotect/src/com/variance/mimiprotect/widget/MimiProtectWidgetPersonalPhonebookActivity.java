package com.variance.mimiprotect.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.SplashScreenActivity;

public class MimiProtectWidgetPersonalPhonebookActivity extends
		MimiProtectActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// From here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadMimiProtect(this, getIntent(),
				PersonalPhonebookActivity.class, "My Phonebook",
				new HashMap<String, String>(), false);
	}
}
