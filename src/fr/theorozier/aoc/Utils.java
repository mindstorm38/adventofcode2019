package fr.theorozier.aoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntConsumer;

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
	
	public static int getNthDigit(int number, int base, int nth) {
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

}
