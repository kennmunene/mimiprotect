package com.variance.mimiprotect.ad;

import java.io.Serializable;

import com.variance.vjax.android.annotations.ArrayParented;

public class Ad implements Serializable {
	private static final long serialVersionUID = -4559108260855758783L;
	/**
	 * Required for add redirection
	 */
	private String adUrl;
	/**
	 * Required if ad is text
	 */
	private String adText;
	/**
	 * Image to display. Required for image ads.
	 */
	@ArrayParented
	private byte[] adImage;
	/**
	 * Ad Type. Currently TEXT_AD or IMAGE_AD
	 */
	private AdType adType;

	public String getAdUrl() {
		return adUrl;
	}

	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}

	public String getAdText() {
		return adText;
	}

	public void setAdText(String adText) {
		this.adText = adText;
	}

	public byte[] getAdImage() {
		return adImage;
	}

	public void setAdImage(byte[] adImage) {
		this.adImage = adImage;
	}

	public AdType getAdType() {
		return adType;
	}

	public void setAdType(AdType adType) {
		this.adType = adType;
	}

}
