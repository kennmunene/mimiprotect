package com.variance.mimiprotect.response;

/**
 * Called by the request manager whenever the response is received.
 * 
 * @author marembo
 * 
 */
public interface HttpResponseHandler {
	/**
	 * Callback with the response data
	 * 
	 * @param httpResponseData
	 */
	void responseReceived(HttpResponseData httpResponseData);

	/**
	 * Callback to indicate that the response from the server is complete
	 */
	void responseComplete();
}
