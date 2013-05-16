package com.variance.mimiprotect.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.SplashScreenActivity;
import com.variance.mimiprotect.ui.contact.NewContactActivity;

public class MimiProtectWidgetNewContactActivity extends MimiProtectActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// from here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadMimiProtect(this, getIntent(),
				NewContactActivity.class, "New Contact",
				new HashMap<String, String>(), true);
	}

}
