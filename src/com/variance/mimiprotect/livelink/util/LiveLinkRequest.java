package com.variance.mimiprotect.livelink.util;

public class LiveLinkRequest {
	public static class FromUser {
		private String id;
		private String name;
		private String organization;
		private String title;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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

	}

	private String id;
	private String name;
	private String livelinkMessage;
	private FromUser fromUser;

	public LiveLinkRequest(String livelinkid, String userName, String message) {
		super();
		this.id = livelinkid;
		this.name = userName;
		this.livelinkMessage = message;
	}

	public LiveLinkRequest(String livelinkid, String userName) {
		super();
		this.id = livelinkid;
		this.name = userName;
	}

	public LiveLinkRequest() {
		super();
	}

	public FromUser getFromUser() {
		return fromUser;
	}

	public void setFromUser(FromUser fromUser) {
		this.fromUser = fromUser;
	}

	public String getLiveLinkID() {
		return id;
	}

	public String getMessage() {
		return livelinkMessage;
	}

	public void setMessage(String message) {
		this.livelinkMessage = message;
	}

	public String getUserName() {
		return name;
	}

}
