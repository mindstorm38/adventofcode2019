package fr.theorozier.aoc;

import java.io.IOException;

public class Day2 {
	
	public static int run(int[] memory, int noun, int verb) {
	
		if (memory.length < 3)
			return -1;
		
		memory[1] = noun;
		memory[2] = verb;
		
		int current, src1, src2, dst;
		
		for (int pc = 0; pc < memory.length; pc += 4) {
			
			current = memory[pc];
			
			if (current == 99)
				break;
			
			src1 = memory[pc + 1];
			src2 = memory[pc + 2];
			dst = memory[pc + 3];
			
			try {
				
				switch (current) {
					case 1:
						memory[dst] = memory[src1] + memory[src2];
						break;
					case 2:
						memory[dst] = memory[src1] * memory[src2];
						break;
					default:
						System.out.println("Invalid intcode '" + current + "'.");
				}
				
			} catch (IndexOutOfBoundsException e) {
				
				System.out.println("Stack overflow.");
				break;
				
			}
			
		}
		
		return memory[0];
	
	}
	
	public static void main(String[] args) throws IOException {
	
		String input = Utils.getFileContent("day2input");
		String[] rawCodes = input.split(",");
		int[] memory = new int[rawCodes.length];
		
		for (int i = 0; i < memory.length; i++) {
			int j = i;
			Utils.safeParseInt(rawCodes[i], code -> memory[j] = code);
		}
		
		for (int noun = 0; noun < 100; ++noun) {
			for (int verb = 0; verb < 100; ++verb) {
				
				if (run(memory.clone(), noun, verb) == 19690720) {
					
					System.out.println("Found !");
					System.out.println("- Noun : " + noun);
					System.out.println("- Verb : " + verb);
					break;
					
				}
				
			}
		}
	
	}
	
}
