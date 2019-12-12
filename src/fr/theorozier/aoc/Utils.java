package fr.theorozier.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class Utils {
	
	public static InputStream getFileStream(String name) {
		return Utils.class.getResourceAsStream("files/" + name);
	}
	
	public static String getFileContent(String name) throws IOException {
		
		StringBuilder builder = new StringBuilder();
		InputStream stream = getFileStream(name);
		
		byte[] bytes = new byte[2048];
		int len;
		
		while ((len = stream.read(bytes)) != -1) {
			builder.append(new String(bytes, 0, len));
		}
		
		return builder.toString();
		
	}
	
	public static String[] getFileLines(String name) throws IOException {
		return getFileContent(name).split("\r?\n");
	}
	
	public static void safeParseInt(String str, IntConsumer consumer) {
		try {
			int i = Integer.parseInt(str);
			consumer.accept(i);
		} catch (NumberFormatException ignored) {}
	}
	
	public static void safeParseLong(String str, LongConsumer consumer) {
		try {
			long l = Long.parseLong(str);
			consumer.accept(l);
		} catch (NumberFormatException ignored) {}
	}
	
	public static int getNthDigit(long number, int base, int nth) {
		return (int) ((number / Math.pow(base, nth)) % base);
	}
	
	public static int[] parseIntList(String content) {
		
		String[] raw = content.split(",");
		int[] arr = new int[raw.length];
		
		for (int i = 0; i < arr.length; i++) {
			int j = i;
			Utils.safeParseInt(raw[i], n -> arr[j] = n);
		}
		
		return arr;
		
	}
	
	public static long[] parseLongList(String content) {
		
		String[] raw = content.split(",");
		long[] arr = new long[raw.length];
		
		for (int i = 0; i < arr.length; i++) {
			int j = i;
			Utils.safeParseLong(raw[i], n -> arr[j] = n);
		}
		
		return arr;
		
	}
	
	public static long gcd(long a, long b) {
		while (b > 0) {
			long temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}
	
	public static long gcd(long[] input) {
		long result = input[0];
		for(int i = 1; i < input.length; i++) result = gcd(result, input[i]);
		return result;
	}
	
	public static long lcm(long a, long b) {
		return a * (b / gcd(a, b));
	}
	
	public static long lcm(long[] input) {
		long result = input[0];
		for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
		return result;
	}
	
}
