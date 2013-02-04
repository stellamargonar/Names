package it.unitn.disi.sweb.names.utils;

public class RomanArabicNumberConverter {

	public static int RomanToArabic(String s) {
		s = s.toUpperCase();
		if (isValidRoman(s)) { // Check if the number is a valid Roman Number

			int Arabic = 0; // This will be our result.
			int last_digit = 0;
			int current_digit = 0;

			for (int i = 0; i < s.length(); i++) {

				if (s.charAt(i) == 'I') {
					current_digit = 1;
				}
				if (s.charAt(i) == 'V') {
					current_digit = 5;
				}
				if (s.charAt(i) == 'X') {
					current_digit = 10;
				}
				if (s.charAt(i) == 'L') {
					current_digit = 50;
				}
				if (s.charAt(i) == 'C') {
					current_digit = 100;
				}
				if (s.charAt(i) == 'D') {
					current_digit = 500;
				}
				if (s.charAt(i) == 'M') {
					current_digit = 1000;
				}

				// This is the tricky part.
				// If the last number is smaller than the curren number,
				// subtract the last number from the current number
				// Otherwise, just add the current number. We must also skip the
				// first number from this rule simply because
				// e.g. someone enters 1799 in which case it would subtract 1
				// from 7

				if (last_digit < current_digit && last_digit != 0) {
					current_digit -= last_digit;
					Arabic -= last_digit;
					Arabic += current_digit;
					last_digit = current_digit;
					current_digit = 0;
				} else {
					last_digit = current_digit;
					Arabic += current_digit;
					current_digit = 0;
				}
			}

			return Arabic;

		} else {
			return 0; // Return null if the number entered is not a proper
						// roman number.
		}
	}

	// Checks whether the entered Roman numeral is valid
	public static boolean isValidRoman(String num) {

		// Checks each character if it is one of I, V, , L, C, D, M (Roman
		// characters)
		num = num.toUpperCase();

		for (int k = 0; k < num.length(); k++) {
			if (num.charAt(k) != 'I' && num.charAt(k) != 'V'
					&& num.charAt(k) != 'X' && num.charAt(k) != 'L'
					&& num.charAt(k) != 'C' && num.charAt(k) != 'D'
					&& num.charAt(k) != 'M') {
				return false;
			}
		}
		return true;
	}

	// Returns a string containing the entered Arabic numeral in Roman numeral
	// form.
	public static String ArabicToRoman(int num) {

		String Roman = ""; // This will be our result string.

		// Declare and Initiate our Arrays
		String onesArray[] = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII",
				"IX"};
		String tensArray[] = {"X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX",
				"XC"};
		String hundredsArray[] = {"C", "CC", "CCC", "CD", "D", "DC", "DCC",
				"DCCC", "CM"};

		// Get the ones in the number
		int ones = num % 10;

		// Get the tens
		num = (num - ones) / 10;
		int tens = num % 10;

		// Get the hundreds
		num = (num - tens) / 10;
		int hundreds = num % 10;

		// Get and write the thousands in the number to our string
		num = (num - hundreds) / 10;
		for (int i = 0; i < num; i++) {
			Roman += "M";
		}

		// Write the hundreds
		if (hundreds >= 1) {
			Roman += hundredsArray[hundreds - 1];
		}

		// Write the tens
		if (tens >= 1) {
			Roman += tensArray[tens - 1];
		}

		// And finally, write the ones
		if (ones >= 1) {
			Roman += onesArray[ones - 1];
		}

		// Return our string.
		return String.valueOf(Roman);

	}

}
