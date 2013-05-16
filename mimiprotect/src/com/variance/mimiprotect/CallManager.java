package com.variance.mimiprotect;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.variance.mimiprotect.call.CallInformation;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.Settings;

public class CallManager extends BroadcastReceiver {
	/**
	 * The constants must be the same as the constants in
	 * com.variance.utils.CallInformationParser
	 */

	private CallInformation callInformation;
	/**
	 * This constant is a hack, it will need to be pushed through the intent.
	 */
	public static Calendar CURRENT_DIALLED_NUMBER_TIMESTAMPT;

	public CallManager() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
				TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			if (PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER != null) {
				CURRENT_DIALLED_NUMBER_TIMESTAMPT = Calendar.getInstance();
			}
			// Phone number
		} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
				TelephonyManager.EXTRA_STATE_IDLE)) {
			Log.i("CallManager", "Call disconnected");
			System.out.println("Outgoingnumber: "
					+ PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER);
			if (CURRENT_DIALLED_NUMBER_TIMESTAMPT != null) {
				TelephonyManager tManager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				callInformation = new CallInformation(null,
						PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER,
						CURRENT_DIALLED_NUMBER_TIMESTAMPT,
						Calendar.getInstance(), tManager.getCellLocation(),
						tManager.getLine1Number(), tManager.getDeviceId());
				logCallInformation();
			}
			// clear the data after the phone calls end
			PersonalPhonebookActivity.CURRENT_DIALLED_NUMBER = null;
			CURRENT_DIALLED_NUMBER_TIMESTAMPT = null;
		}
	}

	private void logCallInformation() {
		try {
			String result = HttpRequestManager.doRequest(
					Settings.getCallInformationUrl(),
					Settings.makeCallInformationParameters(callInformation));
			Log.i("log_tag", "Result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
