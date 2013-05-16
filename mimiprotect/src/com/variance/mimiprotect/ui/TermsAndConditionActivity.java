package com.variance.mimiprotect.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.task.SignUpTask;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;

public class TermsAndConditionActivity extends MimiProtectActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_termsandconditionsdocument);
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(
						"/com/variance/mimiprotect/docandpolicy.html")));
		String html = "";
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				html += line;
			}
			WebView webView = (WebView) findViewById(R.id.termsAndConditionsView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadData(html, "text/html", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void declinePolicy(View view) {
		finish();
	}

	public void acceptPolicy(View view) {
		Intent i = getIntent();
		String username = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_USERNAME);
		String password = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_PASSWORD);
		String confirmPassword = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_CONFIRM_PASSWORD);
		String email = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_EMAIL);
		SignUpTask task = new SignUpTask(username, password, confirmPassword,
				email, this);
		task.execute(new String[] { " " });
	}

}
