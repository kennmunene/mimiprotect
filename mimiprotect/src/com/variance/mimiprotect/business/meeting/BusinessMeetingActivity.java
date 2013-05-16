package com.variance.mimiprotect.business.meeting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.business.meeting.Meeting.UserBasicInfo;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;
import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.android.VXMLBindingException;
import com.variance.vjax.android.VXMLMemberNotFoundException;
import com.variance.vjax.xml.VDocument;

public class BusinessMeetingActivity extends GeneralBusinessMeetingActivity {
	private Meeting meeting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_meeting_meetingwindow);
	}

	public void handleSearch(View view) {
		// get the id to search for
		final String id = ((EditText) findViewById(R.id.txtMeetingSearch))
				.getText().toString();
		loadMeeting(id);
	}

	private void loadMeeting(final String meetingCode) {
		if (!Utils.isNullStringOrEmpty(meetingCode)) {
			try {
				// do an sync search
				new AsyncTask<Void, Void, HttpResponseData>() {
					@Override
					protected void onPreExecute() {
						PersonalPhonebookActivity.showProgress(
								"Please wait, finding meeting...",
								BusinessMeetingActivity.this, this);
					}

					@Override
					protected void onPostExecute(HttpResponseData result) {
						PersonalPhonebookActivity.endProgress();
						// get the response data.
						if (result != null
								&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							String meetingInfo = result.getMessage();
							VDocument doc = VDocument
									.parseDocumentFromString(meetingInfo);
							try {
								meeting = new VObjectMarshaller<Meeting>(
										Meeting.class).unmarshall(doc);
								handleMeeting();
							} catch (VXMLMemberNotFoundException e) {
								e.printStackTrace();
							} catch (VXMLBindingException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(BusinessMeetingActivity.this,
									"Result: " + result, Toast.LENGTH_LONG)
									.show();
						}
					}

					@Override
					protected HttpResponseData doInBackground(Void... params) {
						return HttpRequestManager
								.doRequestWithResponseData(
										Settings.getBusinessMeetingUrl(),
										Settings.makeBusinessMeetingSearchParameter(meetingCode));
					}
				}.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this,
					"You must enter the correct meeting id to join a meeting.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void handleMeeting() {
		if (meeting != null) {
			// first update the action bars.
			View suspendMeeting = findViewById(R.id.businessSuspendMeeting);
			View startMeeting = findViewById(R.id.businessStartMeeting);
			View joinMeeting = findViewById(R.id.businessJoinMeeting);
			View leaveMeeting = findViewById(R.id.businessLeaveMeeting);
			if (meeting != null) {
				if (meeting.isCurrentMeetingAdministrator()
						&& meeting.getMeetingStatus() != MeetingStatus.CLOSED) {
					if (meeting.getMeetingStatus() != MeetingStatus.SUSPENDED) {
						suspendMeeting.setVisibility(View.VISIBLE);
					} else {
						suspendMeeting.setVisibility(View.GONE);
					}
					if (meeting.getMeetingStatus() != MeetingStatus.STARTED) {
						startMeeting.setVisibility(View.VISIBLE);
					} else {
						startMeeting.setVisibility(View.GONE);
					}
				}
				if (meeting.getMeetingStatus() == MeetingStatus.STARTED) {
					if (!meeting.isCurrentUserJoinedMeeting()) {
						joinMeeting.setVisibility(View.VISIBLE);
						leaveMeeting.setVisibility(View.GONE);
					} else {
						joinMeeting.setVisibility(View.GONE);
						leaveMeeting.setVisibility(View.VISIBLE);
					}
				} else {
					joinMeeting.setVisibility(View.GONE);
					leaveMeeting.setVisibility(View.GONE);
				}
			} else {
				suspendMeeting.setVisibility(View.GONE);
				startMeeting.setVisibility(View.GONE);
				joinMeeting.setVisibility(View.GONE);
				leaveMeeting.setVisibility(View.GONE);
			}
			// set data to the view.
			TextView meetingCode = (TextView) findViewById(R.id.meetingCode);
			TextView meetingDate = (TextView) findViewById(R.id.meetingDate);
			TextView meetingTitle = (TextView) findViewById(R.id.meetingTitle);
			TextView meetingNotes = (TextView) findViewById(R.id.meetingNotes);
			meetingCode.setText(meeting.getMeetingCode() + "");
			meetingDate.setText(Utils.toDateString(meeting.getMeetingDate()));
			meetingTitle.setText(meeting.getMeetingName());
			meetingNotes.setText(meeting.getMeetingNotes());
			Log.i("meeting attendants: ", meeting.getMeetingAttendants() + "");
			if (meeting.getMeetingAttendants() != null
					&& !meeting.getMeetingAttendants().isEmpty()) {
				// add an array adapter
				handleMeetingAttendants();
			}
		}
	}

	private void handleMeetingAttendants() {
		final List<UserBasicInfo> attendants = new ArrayList<UserBasicInfo>(
				meeting.getMeetingAttendants());
		ArrayAdapter<UserBasicInfo> adapter = new ArrayAdapter<Meeting.UserBasicInfo>(
				this, R.layout.business_meeting_singleattendantview, attendants) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(
						R.layout.business_meeting_singleattendantview, parent,
						false);
				final UserBasicInfo basicInfo = attendants.get(position);
				TextView nameView = (TextView) rowView
						.findViewById(R.id.userId);
				TextView orgView = (TextView) rowView
						.findViewById(R.id.userOrganization);
				TextView titleView = (TextView) rowView
						.findViewById(R.id.userTitle);
				if (!Utils.isNullStringOrEmpty(basicInfo.getOrganization())) {
					orgView.setText(basicInfo.getOrganization());
					orgView.setVisibility(View.VISIBLE);
				}
				if (!Utils.isNullStringOrEmpty(basicInfo.getTitle())) {
					titleView.setText(basicInfo.getTitle());
					titleView.setVisibility(View.VISIBLE);
				}
				nameView.setText(basicInfo.getName());
				Button saveHandler = (Button) rowView
						.findViewById(R.id.businessMeetingSaveBusinessCard);
				// is this basic info for current user? or have we added the
				// contact
				if (basicInfo.getId().equals(meeting.getCurrentUserId())
						|| meeting.getLinkedContactsId().contains(
								basicInfo.getId())) {
					saveHandler.setVisibility(View.GONE);
				} else {
					// We add
					saveHandler.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Log.e("user id", basicInfo.getId() + "");
							if (meeting.isCurrentUserJoinedMeeting()) {
								saveBusinessCard(basicInfo.getId());
							} else {
								Toast.makeText(
										BusinessMeetingActivity.this,
										"Sorry! You must join the meeting to share contact with others.",
										Toast.LENGTH_LONG).show();
							}
						}
					});
				}
				return rowView;
			}
		};
		ListView listView = (ListView) findViewById(R.id.meetingAttendants);
		listView.setAdapter(adapter);
	}

	public void doStartMeeting(View view) {
		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						BusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(BusinessMeetingActivity.this, result + "",
							Toast.LENGTH_LONG).show();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						loadMeeting(meeting.getMeetingCode() + "");
					}
				} else {
					Toast.makeText(BusinessMeetingActivity.this,
							"Error, Server Connection Error", Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingStartParameter(meeting),
						BusinessMeetingActivity.this);
			}
		}.execute();
	}

	public void doSuspendMeeting(View view) {
		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						BusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(BusinessMeetingActivity.this, result + "",
							Toast.LENGTH_LONG).show();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						loadMeeting(meeting.getMeetingCode() + "");
					}
				} else {
					Toast.makeText(BusinessMeetingActivity.this,
							"Error, Server Connection Error", Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingSuspendParameter(meeting),
						BusinessMeetingActivity.this);
			}
		}.execute();
	}

	public void joinMeeting(View view) {
		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						BusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(BusinessMeetingActivity.this, result + "",
							Toast.LENGTH_LONG).show();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						loadMeeting(meeting.getMeetingCode() + "");
					}
				} else {
					Toast.makeText(BusinessMeetingActivity.this,
							"Error, Server Connection Error", Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingJoinParameter(meeting),
						BusinessMeetingActivity.this);
			}
		}.execute();
	}

	public void leaveMeeting(View view) {
		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						BusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(BusinessMeetingActivity.this, result + "",
							Toast.LENGTH_LONG).show();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						loadMeeting(meeting.getMeetingCode() + "");
					}
				} else {
					Toast.makeText(BusinessMeetingActivity.this,
							"Error, Server Connection Error", Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingLeaveParameter(meeting),
						BusinessMeetingActivity.this);
			}
		}.execute();
	}

	public void saveBusinessCard(final long userId) {
		new AsyncTask<Void, Void, HttpResponseData>() {
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Please wait...",
						BusinessMeetingActivity.this, this);
			};

			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(BusinessMeetingActivity.this, result + "",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(BusinessMeetingActivity.this,
							"Error, Server Connection Error", Toast.LENGTH_LONG)
							.show();
				}
			};

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(
						Settings.getBusinessMeetingUrl(),
						Settings.makeBusinessMeetingSaveCardParameter(userId),
						BusinessMeetingActivity.this);
			}
		}.execute();
	}

	public void viewMeetingAttendants(View view) {
		showAttendantsView();
		View actionBar = findViewById(R.id.actionBar);
		actionBar.setVisibility(View.GONE);
	}

	private void showAttendantsView() {
		View mainWindow = findViewById(R.id.mainWindow);
		mainWindow.setVisibility(View.GONE);
		View attendantsWindow = findViewById(R.id.meetingAttendantsView);
		attendantsWindow.setVisibility(View.VISIBLE);
	}

	private void showMainWindowView() {
		View mainWindow = findViewById(R.id.mainWindow);
		mainWindow.setVisibility(View.VISIBLE);
		View attendantsWindow = findViewById(R.id.meetingAttendantsView);
		attendantsWindow.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		View actionBar = findViewById(R.id.actionBar);
		View mainWindow = findViewById(R.id.mainWindow);
		if (actionBar.getVisibility() == View.VISIBLE) {
			actionBar.setVisibility(View.GONE);
		} else if (mainWindow.getVisibility() == View.GONE) {
			showMainWindowView();
		} else {
			super.onBackPressed();
		}
	}
}
