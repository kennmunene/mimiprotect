package com.variance.mimiprotect.contacts.cache;

public class CacheUtils {
	public static String convertArrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			// Do not append comma at the end of last element
			if (i < array.length - 1) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	public static String[] convertStringToArray(String str) {
		return str.split("-");
	}

	public static boolean getBooleanValue(int i) {
		return i == 0 ? false : true;
	}

	public static int getIntValue(boolean b) {
		if (b) {
			return 1;
		}
		return 0;
	}
}
