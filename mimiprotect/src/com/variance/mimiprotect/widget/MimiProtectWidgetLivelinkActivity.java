package com.variance.mimiprotect.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.SplashScreenActivity;

public class MimiProtectWidgetLivelinkActivity extends MimiProtectActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// from here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadMimiProtect(this, getIntent(),
				LiveLinkRequestsActivity.class, "Livelinks",
				new HashMap<String, String>(), true);
	}

}
