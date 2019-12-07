package fr.theorozier.aoc;

import java.io.IOException;

public class Day1 {
	
	private static int computeFuel(int mass) {
		return mass / 3 - 2;
	}
	
	public static void main(String[] args) throws IOException {
		
		int total = 0;
		int tempTotal;
		int temp;
		
		for (String line : Utils.getFileLines("day1input")) {
			
			try {
				
				tempTotal = computeFuel(Integer.parseInt(line));
				
				temp = tempTotal;
				
				for (;;) {
					
					temp = computeFuel(temp);
					
					if (temp <= 0)
						break;
					
					tempTotal += temp;
					
				}
				
				total += tempTotal;
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println(total);
		
	}
	
}
