package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.MisspellingsComparator;

public class LevenstheinDamearau implements MisspellingsComparator {

	private static LevenstheinDamearau instance;

	private LevenstheinDamearau() {
	}

	public static LevenstheinDamearau getInstance() {
		if (instance == null) {
			instance = new LevenstheinDamearau();
		}
		return instance;
	}

	@Override
	public double getSimilarity(String string1, String string2) {
		int len = string2.length();
		int dist = computeLevenshteinDistance(string1, string2);
		return (double)(len - dist) / len;
	}

	private int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	private int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++) {
			distance[i][0] = i;
		}
		for (int j = 1; j <= str2.length(); j++) {
			distance[0][j] = j;
		}

		for (int i = 1; i <= str1.length(); i++) {
			for (int j = 1; j <= str2.length(); j++) {
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0
										: 1));
			}
		}

		return distance[str1.length()][str2.length()];
	}
}
