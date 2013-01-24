package it.unitn.disi.sweb.names.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class StringCompareUtils {
	private static final int NGRAM_DIFFERENCE = 366;

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

	public static int computeNGram(String name) {
		if (name.length() == 0) {
			return 0;
		}
		String nameNormalized = StringCompareUtils.normalize(name);
		String[] grams = genereteGrams(nameNormalized, 3);
		List<Integer> sums = new ArrayList<Integer>(grams.length);
		for (String g : grams) {
			for (char c : g.toCharArray()) {
				sums.add(c + 0);
			}
		}
		int tmp = sumAll(sums);
		return tmp;
	}
	public static int computeMaxDifference(String name) {
		double a = Math.log(name.length());
		double b = NGRAM_DIFFERENCE;
		return (int) (a * b);
		// return (int) Math.log((double) name.length() * NGRAM_DIFFERENCE)
		// * NGRAM_DIFFERENCE;
		// return (name.length() / 3 + 1) * NGRAM_DIFFERENCE;
	}

	private static int sumAll(List<Integer> sums) {
		int sum = 0;
		for (Integer i : sums) {
			sum += i;
		}

		return sum;
	}

	private static String[] genereteGrams(String name, int size) {
		if (name == null || name.length() == 0) {
			return null;
		}
		// name = " " + name + " ";

		// case of name shorter than gram size
		if (name.length() <= size) {
			String[] result = new String[1];
			result[0] = name;
			return result;
		}
		String[] grams = new String[name.length() - size + 1];
		for (int i = 0; i + size <= name.length(); i++) {
			grams[i] = name.substring(i, i + size);
		}
		return grams;
	}

	/**
	 * generate the tokens for the input strings, based on predefined rules
	 * (spacing, puntaction..)
	 *
	 * @param input
	 *            string
	 * @return array of strings representing the tokens
	 */
	public static String[] generateTokens(String input) {
		if (input != null) {
			// change tokenization
			// ora P.zza -> [P] [zza]
//			String token[] = input.split(" ");
//			for (int i=0; i<token.length; i++) {
//				String s = token[i];
//				if (s.endsWith(".")) {
//					token[i] = s.substring(0, s.length()-1);
//				}
//			}
//			return token;
//
			StringTokenizer tokenizer = new StringTokenizer(input, " .");
			List<String> tokens = new ArrayList<>();
			while (tokenizer.hasMoreTokens()) {
				tokens.add(tokenizer.nextToken());
			}
			String[] result = new String[tokens.size()];
			return tokens.toArray(result);
		} else {
			return null;
		}
	}

}
