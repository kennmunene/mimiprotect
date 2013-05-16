package com.variance.mimiprotect.business.meeting;

import java.util.Calendar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class ScheduleBusinessMeetingActivity extends
		GeneralBusinessMeetingActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_meeting_schedulemeetingwindow);
	}

	public void doScheduleMeeting(View view) {
		// get the data for the meeting.
		Meeting meeting = new Meeting();
		// get the date
		DatePicker datePicker = (DatePicker) findViewById(R.id.meetingDate);
		Calendar meetingDate = Calendar.getInstance();
		meetingDate.set(Calendar.YEAR, datePicker.getYear());
		meetingDate.set(Calendar.MONTH, datePicker.getMonth());
		meetingDate.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		meeting.setMeetingDate(meetingDate);
		// get title
		EditText title = (EditText) findViewById(R.id.meetingTitle);
		String meetingTitle = title.getText().toString();
		if (Utils.isNullStringOrEmpty(meetingTitle)) {
			Toast.makeText(this,
					"Sorry, you must specify the meeting title/name",
					Toast.LENGTH_LONG).show();
			return;
		}
		meeting.setMeetingName(meetingTitle);
		// meeting notes
		EditText notes = (EditText) findViewById(R.id.meetingNotes);
		meeting.setMeetingNotes(notes.getText().toString());
		// now send this to the server.
		// the server must return the unique generated meeting id.
		doScheduleMeeting(meeting);
	}

	private void doScheduleMeeting(final Meeting meeting) {

		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait, scheduling your meeting..",
						ScheduleBusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				// handle the result.
				if (result != null
						&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
					// get the id
					String meetingCode = result.getMessage();
					// find the component
					TextView code = (TextView) findViewById(R.id.meetingCode);
					code.setText(meetingCode);
				} else {
					Toast.makeText(ScheduleBusinessMeetingActivity.this,
							"Error: " + result, Toast.LENGTH_LONG).show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingScheduleParameter(meeting),
						ScheduleBusinessMeetingActivity.this);
			}

		}.execute();
	}
}
