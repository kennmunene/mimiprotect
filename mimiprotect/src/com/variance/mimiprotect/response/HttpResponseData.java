package com.variance.mimiprotect.response;

import java.util.HashMap;
import java.util.Map;

import com.variance.mimiprotect.util.Utils;

public class HttpResponseData {

	private HttpResponseStatus responseStatus;
	private String message;
	private Map<String, String> extras;

	public HttpResponseData(HttpResponseStatus responseStatus, String message,
			Map<String, String> extras) {
		this.responseStatus = responseStatus;
		this.message = message;
		this.extras = extras;
	}

	public HttpResponseData(HttpResponseStatus responseStatus, String message) {
		this.responseStatus = responseStatus;
		this.message = message;
		this.extras = new HashMap<String, String>();
	}

	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(HttpResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getMessage() {
		if(!Utils.isNullStringOrEmpty(message)){
			message = message.replaceAll("&lt;", "<");
			message = message.replaceAll("&gt;", ">");
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void putExtra(String id, String value) {
		extras.put(id, value);
	}

	public String getExtra(String id) {
		return extras.get(id);
	}

	public boolean getBooleanExtra(String id) {
		String extra = getExtra(id);
		if (!Utils.isNullStringOrEmpty(extra)) {
			return extra.trim().equalsIgnoreCase("true");
		}
		return false;
	}

	public Map<String, String> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, String> extras) {
		this.extras = extras;
	}

	public String toString() {
		return responseStatus.name() + ": " + message;
	}

	public int getIntExtra(String id) {
		String extra = getExtra(id);
		if (!Utils.isNullStringOrEmpty(extra)) {
			return Integer.parseInt(extra);
		}
		return 0;
	}
}