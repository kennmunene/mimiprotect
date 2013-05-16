package com.variance.mimiprotect.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseHandler;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;

public final class HttpRequestManager {
	public static final String NETWORK_CONNECTION_UNAVAILABLE = "35472384329423CCFBAFA";
	private static final Map<String, String> HTTP_REQUEST_CODE = new HashMap<String, String>();
	static {
		HTTP_REQUEST_CODE.put(NETWORK_CONNECTION_UNAVAILABLE,
				"Network Connection not available");
	}

	public static String getRequestCodeValue(String code) {
		return HTTP_REQUEST_CODE.get(code);
	}

	public static int lookupHost(String hostName) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			Log.e("UnknownHostException", e.toString());
			return -1;
		}
		byte[] addrBytes;
		int addr;
		addrBytes = inetAddress.getAddress();
		addr = ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16)
				| ((addrBytes[1] & 0xff) << 8) | (addrBytes[0] & 0xff);
		Log.i("Address: ", addr + "");
		return addr;
	}

	public static boolean hasNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}

	public static HttpClient getHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 50000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		return new DefaultHttpClient(httpParameters);
	}

	public static boolean isOnline(Context context) {
		return hasNetworkConnection(context);
	}

	private static HttpPost currentHttpPost;

	public static HttpPost createHttpPost(String url) {
		currentHttpPost = new HttpPost(url);
		return currentHttpPost;
	}

	public static void abortRequest() {
		if (currentHttpPost != null && !currentHttpPost.isAborted()) {
			currentHttpPost.abort();
		}
	}

	public static String doRequest(String url, Map<String, String> nameValue,
			Context context) {
		if (!isOnline(context)) {
			return NETWORK_CONNECTION_UNAVAILABLE;
		}
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null) {
			return "No session is associated with current user";
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			if (result != null) {
				return result.trim();
			}
			return result;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return "";
		}
	}

	public static String doRequest(String url, Map<String, String> nameValue,
			boolean loginRequest, Context context) {
		if (!isOnline(context)) {
			return NETWORK_CONNECTION_UNAVAILABLE;
		}
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null
				&& !loginRequest) {
			return "No session is associated with current user";
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			if (result != null) {
				return result.trim();
			}
			return result;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return "";
		}
	}

	public static HttpResponseData doRequestWithResponseData(String url,
			Map<String, String> nameValue, Context context) {
		if (!isOnline(context)) {
			return new HttpResponseData(HttpResponseStatus.UNAVAILABLE,
					NETWORK_CONNECTION_UNAVAILABLE);
		}
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null) {
			return new HttpResponseData(HttpResponseStatus.UNAVAILABLE,
					"SESSION ID not Specified");
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			Log.i("Response String", result);
			if (result != null) {
				return DataParser.getHttpResponseData(result.trim());
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("HttpRequestManager", e.toString());
			return null;
		}
	}

	public static void doRequestWithResponseData(final String url,
			final Map<String, String> nameValue, final Context context,
			final HttpResponseHandler handler) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (!isOnline(context)) {
					HttpResponseData data = new HttpResponseData(
							HttpResponseStatus.UNAVAILABLE,
							NETWORK_CONNECTION_UNAVAILABLE);
					handler.responseReceived(data);
					handler.responseComplete();
				}
				if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null) {
					HttpResponseData data = new HttpResponseData(
							HttpResponseStatus.UNAVAILABLE,
							"SESSION ID not Specified");
					handler.responseReceived(data);
					handler.responseComplete();
				}
				try {
					HttpClient httpclient = getHttpClient();
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					HttpPost httppost = createHttpPost(url);
					nameValuePairs.clear();
					for (String n : nameValue.keySet()) {
						nameValuePairs.add(new BasicNameValuePair(n, nameValue
								.get(n)));
					}
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					InputStream is = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					String result = null;
					while ((result = reader.readLine()) != null) {
						try {
							Log.i("Response String", result);
							if (result != null && !"".equals(result.trim())) {
								HttpResponseData responseData = DataParser
										.getHttpResponseData(result.trim());
								if (responseData != null) {
									String requestProgress = responseData
											.getExtra(HttpRequestSetting.REQUEST_PROGRESS_STATUS);
									if (requestProgress != null) {
										HttpRequestProgressStatus progressStatus = HttpRequestProgressStatus
												.valueOf(requestProgress.trim()
														.toUpperCase());
										if (progressStatus != null) {
											switch (progressStatus) {
											case COMPLETE:
												handler.responseComplete();
												break;
											case IN_PROGRESS:
												handler.responseReceived(responseData);
												break;
											}
										}
									}
								}
							}
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("HttpRequestManager", e.toString());
				}
				return null;
			}
		}.execute();
	}

	public static HttpResponse doRequestWithResponse(String url,
			Map<String, String> nameValue, Context context) {
		if (!isOnline(context)) {
			return null;
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			return response;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return null;
		}
	}

	public static String doRequest(String url, Map<String, String> nameValue) {
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null) {
			return "No session is associated with current user";
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			if (result != null) {
				return result.trim();
			}
			return result;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return "";
		}
	}

	public static String doRequest(String url, Map<String, String> nameValue,
			boolean loginRequest) {
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null
				&& !loginRequest) {
			return "No session is associated with current user";
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			if (result != null) {
				return result.trim();
			}
			return result;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return "";
		}
	}

	public static HttpResponseData doRequestWithResponseData(String url,
			Map<String, String> nameValue) {
		if (nameValue.get(Settings.SESSION_ID_PARAMETER) == null) {
			return new HttpResponseData(HttpResponseStatus.UNAVAILABLE,
					"SESSION ID not Specified");
		}
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			Log.i("Response String", result);
			if (result != null) {
				return DataParser.getHttpResponseData(result.trim());
			}
			return null;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return null;
		}
	}

	public static HttpResponseData doSessionlessRequestWithResponseData(
			String url, Map<String, String> nameValue) {
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			char[] data = new char[1024];
			int c;
			while ((c = reader.read(data)) != -1) {
				String ns = new String(data, 0, c);
				sb.append(ns);
			}
			is.close();
			String result = sb.toString();
			Log.i("Response String", result);
			if (result != null) {
				return DataParser.getHttpResponseData(result.trim());
			}
			return null;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return null;
		}
	}

	public static HttpResponse doRequestWithResponse(String url,
			Map<String, String> nameValue) {
		try {
			HttpClient httpclient = getHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpPost httppost = createHttpPost(url);
			nameValuePairs.clear();
			for (String n : nameValue.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(n, nameValue.get(n)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			return response;
		} catch (Exception e) {
			Log.e("HttpRequestManager", e.toString());
			return null;
		}
	}
}
