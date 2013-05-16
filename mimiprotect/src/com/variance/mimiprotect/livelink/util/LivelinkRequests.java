package com.variance.mimiprotect.livelink.util;

import java.util.List;

import com.variance.vjax.android.annotations.GenericCollectionType;

public class LivelinkRequests {
	@GenericCollectionType(LiveLinkRequest.class)
	private List<LiveLinkRequest> livelinkRequests;

	public List<LiveLinkRequest> getLivelinkRequests() {
		return livelinkRequests;
	}

	public void setLivelinkRequests(List<LiveLinkRequest> livelinkRequests) {
		this.livelinkRequests = livelinkRequests;
	}

}
