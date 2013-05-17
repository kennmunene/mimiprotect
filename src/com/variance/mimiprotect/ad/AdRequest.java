package com.variance.mimiprotect.ad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdRequest implements Iterable<Ad> {
	private List<Ad> ads;

	public AdRequest() {
		ads = new ArrayList<Ad>();
	}

	public List<Ad> getAds() {
		return ads;
	}

	public void setAds(List<Ad> ads) {
		if (ads != null) {
			this.ads = ads;
		}
	}

	public Iterator<Ad> iterator() {
		return ads.iterator();
	}

}
