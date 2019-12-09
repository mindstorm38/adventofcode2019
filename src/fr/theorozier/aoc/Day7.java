package fr.theorozier.aoc;

import fr.theorozier.aoc.intcode.OpCodeType;
import fr.theorozier.aoc.intcode.VirtualMachine;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Day7 {
	
	/*
	public static void main(String[] args) throws IOException {
		
		String content = Utils.getFileContent("day7input");
		int[] baseMemory = Utils.parseIntList(content);
		
		VirtualMachine[] amplifiers = new VirtualMachine[5];
		
		AtomicInteger lastOutput = new AtomicInteger();
		AtomicInteger inputCount = new AtomicInteger();
		AtomicReference<int[]> setting = new AtomicReference<>();
		
		for (int ampl = 0; ampl < 5; ++ampl) {
			
			int finalAmpl = ampl;
			
			amplifiers[ampl] = new VirtualMachine();
			
			amplifiers[ampl].setOutputConsumer(lastOutput::set);
			amplifiers[ampl].setInputSupplier(() -> {
				
				if (inputCount.getAndIncrement() == 1) {
					return lastOutput.get();
				} else {
					return setting.get()[finalAmpl];
				}
				
			});
			
		}
		
		int max = 0;
		
		for (int i = 0; i < 5; ++i) {
			for (int a = 0; a < 4; ++a) {
				for (int b = 0; b < 3; ++b) {
					for (int c = 0; c < 2; ++c) {
						
						lastOutput.set(0);
						setting.set(generateSetting(5, 0, i, a, b, c));
						
						for (int ampl = 0; ampl < 5; ++ampl) {
							
							inputCount.set(0);
							amplifiers[ampl].setMemory(baseMemory.clone());
							amplifiers[ampl].run();
							
						}
						
						if (lastOutput.get() > max)
							max = lastOutput.get();
						
					}
				}
			}
		}
		
		System.out.println("Max output : " + max);
		
	}
	*/
	
	public static void main(String[] args) throws IOException {
		
		String content = Utils.getFileContent("day7input");
		long[] baseMemory = Utils.parseLongList(content);
		
		VirtualMachine[] amplifiers = new VirtualMachine[5];
		
		AtomicLong lastOutput = new AtomicLong();
		AtomicReference<int[]> setting = new AtomicReference<>();
		AtomicReference<int[]> inputCounts = new AtomicReference<>(new int[5]);
		
		for (int ampl = 0; ampl < 5; ++ampl) {
			
			int finalAmpl = ampl;
			
			amplifiers[ampl] = new VirtualMachine();
			
			amplifiers[ampl].setOutputConsumer(lastOutput::set);
			amplifiers[ampl].setInputSupplier(() -> {
				
				if (inputCounts.get()[finalAmpl]++ >= 1) {
					return lastOutput.get();
				} else {
					return (long) setting.get()[finalAmpl];
				}
				
			});
			
		}
		
		long max = 0;
		boolean halted;
		
		for (int i = 0; i < 5; ++i) {
			for (int a = 0; a < 4; ++a) {
				for (int b = 0; b < 3; ++b) {
					for (int c = 0; c < 2; ++c) {
						
						lastOutput.set(0);
						setting.set(generateSetting(5, 5, i, a, b, c));
						
						System.out.println("-------------------");
						System.out.println("setting: " + Arrays.toString(setting.get()));
						
						for (int ampl = 0; ampl < 5; ++ampl) {
							
							amplifiers[ampl].setMemory(baseMemory.clone());
							inputCounts.get()[ampl] = 0;
							
						}
						
						halted = false;
						
						while (!halted) {
							for (int ampl = 0; ampl < 5; ++ampl) {
								
								halted = amplifiers[ampl].runWhile(opcode -> opcode != OpCodeType.OUTPUT);
								System.out.println("ampl: " + ampl + ", halted: " + halted + ", output: " + lastOutput.get());
								
							}
						}
						
						if (lastOutput.get() > max)
							max = lastOutput.get();
						
					}
				}
			}
		}
		
		System.out.println("Max output : " + max);
		
	}
	
	private static final List<Integer> available = new ArrayList<>();
	
	private static int[] generateSetting(int len, int offset, int base, int...choices) {
		
		if (len != choices.length + 2 || len < 1)
			throw new IllegalArgumentException("Invalid number of choices or len.");
		
		available.clear();
		for (int i = 0; i < len; ++i)
			available.add(offset + i);
		
		int[] setting = new int[len];
		setting[0] = offset + base;
		available.remove((Integer) setting[0]);
		
		for (int i = 1; i < len - 1; ++i) {
			
			setting[i] = available.get(choices[i - 1]);
			available.remove((Integer) setting[i]);
			
		}
		
		setting[len - 1] = available.get(0);
		
		return setting;
	
	}
	
}
