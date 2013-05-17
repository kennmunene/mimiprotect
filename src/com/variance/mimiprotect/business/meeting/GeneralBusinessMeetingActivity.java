package com.variance.mimiprotect.business.meeting;

import android.view.View;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;

public class GeneralBusinessMeetingActivity extends MimiProtectActivity {

	public void joinMeeting(View view) {
	}

	public void leaveMeeting(View view) {
	}

	public void scheduleMeeting(View view) {
		PhonebookActivity.startGeneralActivity(this, "Schedule Meeting",
				ScheduleBusinessMeetingActivity.class);
	}

	public void suspendMeeting(View view) {
		PhonebookActivity.startGeneralActivity(this, "Suspend Meeting",
				ScheduleBusinessMeetingActivity.class);
	}

	public void viewMeetingAttendants(View view) {
	}

	public void showActionOptions(View view) {
		View actionBar = findViewById(R.id.actionBar);
		if (actionBar != null) {
			if (actionBar.getVisibility() == View.GONE) {
				actionBar.setVisibility(View.VISIBLE);
			} else {
				actionBar.setVisibility(View.GONE);
			}
		}
	}
}
