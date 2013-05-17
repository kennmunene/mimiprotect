package com.variance.mimiprotect.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;

@SuppressLint("SetJavaScriptEnabled")
public class SignupActivity extends Activity {
	private EditText txtUsername;
	private EditText txtPassword;
	private EditText txtConfirmPassword;
	private EditText txtEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_signup);
		txtUsername = (EditText) findViewById(R.id.username);
		txtPassword = (EditText) findViewById(R.id.password);
		txtConfirmPassword = (EditText) findViewById(R.id.confirmpassword);
		txtEmail = (EditText) findViewById(R.id.emailAddress);
		TextView t2 = (TextView) findViewById(R.id.txtTermsAndConditions);
		t2.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void myNewAccountHandler(View view) {
		switch (view.getId()) {
		case R.id.btnSignUp:
			createAccount();
			break;
		}
	}

	public void cancelSignup(View view) {
		PhonebookActivity.startGeneralActivity(this, "mimi",
				LoginActivity.class, R.layout.usercontact_whitetitled_tabview);
	}

	private boolean ensureNotNullAndNotEmpty(String... vals) {
		for (String s : vals) {
			if (s == null || "".equals(s.trim())) {
				return false;
			}
		}
		return true;
	}

	private boolean ensurePasswordIsCorrect(String password,
			String confirmPassword) {
		return (password != null && confirmPassword != null && password
				.equals(confirmPassword)) && (password.length() >= 4);
	}

	private void acceptTermsAndPolicyBeforeSignup(String username,
			String password, String confirmpassword, String email) {
		finish();
		Map<String, Serializable> m = new HashMap<String, Serializable>();
		m.put(MimiProtectIntentConstants.MIMI_PROTECT_USERNAME, username);
		m.put(MimiProtectIntentConstants.MIMI_PROTECT_PASSWORD, password);
		m.put(MimiProtectIntentConstants.MIMI_PROTECT_CONFIRM_PASSWORD,
				confirmpassword);
		m.put(MimiProtectIntentConstants.MIMI_PROTECT_EMAIL, email);
		PhonebookActivity.startGeneralActivity(this, "TERMS AND CONDITIONS",
				TermsAndConditionActivity.class, R.layout.usercontact_tabview,
				true, m);
	}

	private void createAccount() {
		String username = txtUsername.getText().toString().trim();
		String password = txtPassword.getText().toString().trim();
		String confirmPassword = txtConfirmPassword.getText().toString().trim();
		String email = txtEmail.getText().toString().trim();
		Log.i("Credentials: ", username + " " + password + " "
				+ confirmPassword + " " + email);
		if (ensureNotNullAndNotEmpty(username, password, confirmPassword, email)) {
			if (!ensurePasswordIsCorrect(password, confirmPassword)) {
				Toast.makeText(this, "Passwords do not match",
						Toast.LENGTH_LONG).show();
			} else {
				acceptTermsAndPolicyBeforeSignup(username, password,
						confirmPassword, email);
			}
		} else {
			Toast.makeText(this, "All fields must be set", Toast.LENGTH_LONG)
					.show();
		}
	}
}
