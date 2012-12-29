package it.unitn.disi.sweb.names.utils;


public class StringCompareUtils {

	public static int lengthDifference(String string1, String string2) {
		if (string1 == null || string2 == null) {
			return Integer.MAX_VALUE;
		}
		return Math.abs(string1.length() - string2.length());
	}
}
