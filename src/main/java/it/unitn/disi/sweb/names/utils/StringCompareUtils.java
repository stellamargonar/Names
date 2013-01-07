package it.unitn.disi.sweb.names.utils;

import java.text.Normalizer;

public final class StringCompareUtils {

	public static int lengthDifference(String string1, String string2) {
		if (string1 == null || string2 == null) {
			return Integer.MAX_VALUE;
		}
		return Math.abs(string1.length() - string2.length());
	}

	public static String normalize(String input) {
		if (input == null) {
			return null;
		}

		String inputLC = input.toLowerCase();
		if (!Normalizer.isNormalized(inputLC, Normalizer.Form.NFKD)) {
			String normalized = Normalizer.normalize(inputLC,
					Normalizer.Form.NFKD);
			normalized = normalized.replaceAll(
					"\\p{InCombiningDiacriticalMarks}+", "");
			return normalized;
		} else {
			return inputLC;
		}

	}
}
