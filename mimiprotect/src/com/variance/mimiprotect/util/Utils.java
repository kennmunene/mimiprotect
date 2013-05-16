package com.variance.mimiprotect.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import android.util.Log;

import com.variance.mimiprotect.contacts.Contact;

public final class Utils {
	public static String getDeviceIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = inetAddress.getHostAddress();
						Log.i("getDeviceIpAddress", "***** IP=" + ip);
						return ip;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("getLocalIpAddress", ex.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Vector<T> v) {
		if (v == null) {
			return null;
		}
		T[] a = (T[]) new Object[v.size()];
		int i = 0;
		for (T t : v) {
			a[i++] = t;
		}
		return a;
	}

	public static String[] contactsToString(Vector<Contact> contacts) {
		if (contacts == null) {
			return null;
		}
		String[] cs = new String[contacts.size()];
		int i = 0;
		for (Contact c : contacts) {
			if (c.getName() != null && !"".equals(c.getName())) {
				cs[i++] = c.getName();
			} else if (c.getEmails() != null && c.getEmails().length > 0) {
				cs[i++] = c.getEmails()[0];
			} else if (c.getPhones() != null && c.getPhones().length > 0) {
				cs[i++] = c.getPhones()[0];
			} else {
				cs[i++] = "Unknown";
			}
		}
		return cs;
	}

	public static String[] contactsToString(ArrayList<Contact> contacts) {
		if (contacts == null) {
			return null;
		}
		String[] cs = new String[contacts.size()];
		int i = 0;
		for (Contact c : contacts) {
			if (c.getName() != null
					&& (c.isDummyContac() || !"".equals(c.getName()))) {
				cs[i++] = c.getName();
			} else if (c.getEmails() != null && c.getEmails().length > 0) {
				cs[i++] = c.getEmails()[0];
			} else if (c.getPhones() != null && c.getPhones().length > 0) {
				cs[i++] = c.getPhones()[0];
			} else {
				cs[i++] = "Unknown";
			}
			if (c.getCompanyName() != null) {
				cs[i - 1] += " (" + c.getCompanyName() + ")";
			}
		}
		return cs;
	}

	public static <T> String toString(T[] a) {
		if (a == null)
			return null;
		String s = "";
		for (T t : a) {
			if ("".equals(s)) {
				s = t.toString();
			} else {
				s += "," + t;
			}
		}
		return "[" + s + "]";
	}

	public static String toIsoString(Calendar calendar) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			calendar.set(1972, 0, 1);
		}
		String year = calendar.get(Calendar.YEAR) + "", mon = (calendar
				.get(Calendar.MONTH) + 1) + "", day = calendar
				.get(Calendar.DATE) + "", hrs = calendar
				.get(Calendar.HOUR_OF_DAY) + "", mins = calendar
				.get(Calendar.MINUTE) + "", secs = calendar
				.get(Calendar.SECOND) + "";
		if (year.length() != 4) {
			year = "20" + year;
		}
		if (mon.length() != 2) {
			mon = "0" + mon;
		}
		if (day.length() != 2) {
			day = "0" + day;
		}
		if (hrs.length() != 2) {
			hrs = "0" + hrs;
		}
		if (mins.length() != 2) {
			mins = "0" + mins;
		}
		if (secs.length() != 2) {
			secs = "0" + secs;
		}
		String str = year + "-" + mon + "-" + day + " " + hrs + ":" + mins
				+ ":" + secs;
		return str;
	}

	public static String toDateString(Calendar calendar) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			calendar.set(1972, 0, 1);
		}
		String date = "";
		String year = calendar.get(Calendar.YEAR) + "";
		int mon = calendar.get(Calendar.MONTH);
		String day = calendar.get(Calendar.DATE) + "";
		Month m = Month.getInstance(mon);
		date += m.toString();
		date += " " + day;
		if (year.length() != 4) {
			year = "20" + year;
		}
		date += ", " + year;
		return date;
	}

	public static String toIsoTString(Calendar calendar) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			calendar.set(1972, 0, 1);
		}
		String year = calendar.get(Calendar.YEAR) + "", mon = (calendar
				.get(Calendar.MONTH) + 1) + "", day = calendar
				.get(Calendar.DATE) + "", hrs = calendar
				.get(Calendar.HOUR_OF_DAY) + "", mins = calendar
				.get(Calendar.MINUTE) + "", secs = calendar
				.get(Calendar.SECOND) + "";
		if (year.length() != 4) {
			year = "20" + year;
		}
		if (mon.length() != 2) {
			mon = "0" + mon;
		}
		if (day.length() != 2) {
			day = "0" + day;
		}
		if (hrs.length() != 2) {
			hrs = "0" + hrs;
		}
		if (mins.length() != 2) {
			mins = "0" + mins;
		}
		if (secs.length() != 2) {
			secs = "0" + secs;
		}
		String str = year + "-" + mon + "-" + day + "T" + hrs + ":" + mins
				+ ":" + secs + "z";
		return str;
	}

	public static byte[] fromString(String str) {
		str = str.trim();
		if (str.indexOf('[') == 0 && str.lastIndexOf(']') == str.length() - 1) {
			String trimStr = str.substring(1, str.length() - 1);
			String[] values = trimStr.split(",");
			byte[] bb = new byte[values.length];
			int i = 0;
			for (String s : values) {
				s = s.trim();
				byte b = Byte.parseByte(s);
				bb[i++] = b;
			}
			return bb;
		}
		return null;
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().equals("");
	}

	/**
	 * Checks if this string is null reference, empty or contains the string
	 * null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullStringOrEmpty(String str) {
		return str == null || str.trim().equals("")
				|| "null".equals(str.trim());
	}

	public static Calendar parseISODate(String value) {
		// iso date
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		String ss[] = (value.contains("T") || value.contains("t")) ? value
				.trim().toUpperCase().split("T") : value.trim().toUpperCase()
				.split(" ");
		String date = ss[0];
		String parts[] = date.trim().split("-");
		if (parts.length != 3) {
			throw new IllegalArgumentException("ISO date wrongly formatted");
		}
		cal.set(Calendar.DATE, Integer.parseInt(parts[2]));
		cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
		cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
		if (ss.length > 1) {
			// do time
			String time = ss[1];
			String pps[] = time.trim().split(":");
			if (pps.length != 3) {
				throw new IllegalArgumentException("ISO time wrongly formatted");
			}
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pps[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(pps[1]));
			if (!pps[2].contains("+")) {
				String secs = pps[2];
				if (secs.toLowerCase().contains("z")) {
					secs = secs.substring(0, secs.toLowerCase()
							.lastIndexOf("z"));
				}
				cal.set(Calendar.SECOND, Integer.parseInt(secs));
			} else {
				cal.set(Calendar.SECOND,
						Integer.parseInt(pps[2].substring(0,
								pps[2].indexOf('+'))));
				cal.set(Calendar.MILLISECOND, Integer.parseInt(pps[2]
						.substring(pps[2].indexOf('+') + 1)));
			}
		}
		return cal;
	}
}
