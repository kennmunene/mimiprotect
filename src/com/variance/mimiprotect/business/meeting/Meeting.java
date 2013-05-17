package com.variance.mimiprotect.business.meeting;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.variance.mimiprotect.util.Utils;
import com.variance.vjax.android.annotations.Converter;
import com.variance.vjax.android.annotations.GenericCollectionType;
import com.variance.vjax.android.converter.impl.CalendarConverter;

/**
 * 
 * @author marembo
 */
public class Meeting implements Serializable {

	private static final long serialVersionUID = 6663988158348545595L;

	public static class UserBasicInfo {

		private Long id;
		private String name;
		private String organization;
		private String title;

		public UserBasicInfo() {
		}

		public UserBasicInfo(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public UserBasicInfo(Long id, String name, String organization,
				String title) {
			this.id = id;
			this.name = name;
			this.organization = organization;
			this.title = title;
		}

		public String getOrganization() {
			return organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "UserBasicInfo [id=" + id + ", name=" + name + "]";
		}

	}

	private Long meetingId;
	private Long meetingCode;
	private String meetingName;
	private String meetingNotes;
	@Converter(CalendarConverter.class)
	private Calendar meetingDate;
	private UserBasicInfo meetingScheduler;
	@GenericCollectionType(UserBasicInfo.class)
	private Set<UserBasicInfo> meetingAttendants;
	private boolean currentMeetingAdministrator;
	private boolean currentUserJoinedMeeting;
	private MeetingStatus meetingStatus;
	/**
	 * Used to mark the current user, so that he does not add himself as a new
	 * contact.
	 */
	private Long currentUserId;
	/**
	 * Ids of the linked contacts, so that we do not linked to them again.
	 */
	@GenericCollectionType(Long.class)
	private Set<Long> linkedContactsId;

	public Meeting(Long meetingCode, String meetingName) {
		this();
		this.meetingCode = meetingCode;
		this.meetingName = meetingName;
	}

	public Meeting(Long meetingCode, String meetingName, String meetingNotes) {
		this();
		this.meetingCode = meetingCode;
		this.meetingName = meetingName;
		this.meetingNotes = meetingNotes;
	}

	public Meeting() {
		meetingDate = Calendar.getInstance();
		meetingAttendants = new HashSet<UserBasicInfo>();
	}

	public void setLinkedContactsId(Set<Long> linkedContactsId) {
		this.linkedContactsId = linkedContactsId;
	}

	public Set<Long> getLinkedContactsId() {
		if (linkedContactsId == null) {
			linkedContactsId = new HashSet<Long>();
		}
		return linkedContactsId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Long getCurrentUserId() {
		return currentUserId;
	}

	public UserBasicInfo getMeetingScheduler() {
		return meetingScheduler;
	}

	public boolean isCurrentUserJoinedMeeting() {
		return currentUserJoinedMeeting;
	}

	public void setCurrentUserJoinedMeeting(boolean currentUserJoinedMeeting) {
		this.currentUserJoinedMeeting = currentUserJoinedMeeting;
	}

	public void setMeetingScheduler(UserBasicInfo meetingScheduler) {
		this.meetingScheduler = meetingScheduler;
	}

	public Long getMeetingCode() {
		return meetingCode;
	}

	public String getMeetingNotes() {
		return meetingNotes;
	}

	public MeetingStatus getMeetingStatus() {
		return meetingStatus;
	}

	public void setMeetingStatus(MeetingStatus meetingStatus) {
		this.meetingStatus = meetingStatus;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public boolean isCurrentMeetingAdministrator() {
		return currentMeetingAdministrator;
	}

	public void setCurrentMeetingAdministrator(
			boolean currentMeetingAdministrator) {
		this.currentMeetingAdministrator = currentMeetingAdministrator;
	}

	public void setMeetingNotes(String meetingNotes) {
		this.meetingNotes = meetingNotes;
	}

	public void setMeetingCode(Long meetingCode) {
		this.meetingCode = meetingCode;
	}

	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}

	public Calendar getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Calendar meetingDate) {
		this.meetingDate = meetingDate;
	}

	public Set<UserBasicInfo> getMeetingAttendants() {
		return meetingAttendants;
	}

	public void setMeetingAttendants(Set<UserBasicInfo> meetingAttendants) {
		this.meetingAttendants = meetingAttendants;
	}

	@Override
	public String toString() {
		return "Meeting{meetingCode=" + meetingCode + ", " + "meetingName="
				+ meetingName + ", meetingDate="
				+ Utils.toIsoString(meetingDate) + '}';
	}
}
