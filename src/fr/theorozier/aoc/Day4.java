package fr.theorozier.aoc;

import java.util.Arrays;

public class Day4 {
	
	private static boolean checkPasswordValidity(int[] digits) {
	
		if (digits.length != 6)
			return false;
		
		int repeatCount = 0;
		boolean adjacent = false;
		
		for (int i = 1; i < 6; i++) {
		
			if (digits[i - 1] > digits[i])
				return false;
			
			if (digits[i - 1] == digits[i]) {
				repeatCount++;
			} else {
				
				if (repeatCount == 1)
					adjacent = true;
				
				repeatCount = 0;
				
			}
		
		}
		
		if (repeatCount == 1)
			adjacent = true;
		
		return adjacent;
	
	}
	
	private static void intToDigits(int n, int[] digits) {
		
		char[] chars = Integer.toString(n, 10).toCharArray();
		
		if (chars.length > 6)
			return;
		
		for (int i = 0; i < 6; ++i)
			digits[i] = Integer.parseInt(String.valueOf(chars[i]), 10);
		
	}
	
	public static void main(String[] args) {
		
		int[] digits = new int[6];
		
		int inf = 158126;
		int sup = 624574;
		
		int count = 0;
		
		for (int i = inf; i <= sup; ++i) {
			
			intToDigits(i, digits);
			
			if (checkPasswordValidity(digits)) {
				count++;
			}
			
		}
		
		System.out.println("Total count : " + count);
	
	}
	
}
