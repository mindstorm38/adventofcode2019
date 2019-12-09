package fr.theorozier.aoc;

import fr.theorozier.aoc.intcode.VirtualMachine;

import java.io.IOException;

public class Day9 {
	
	public static void main(String[] args) throws IOException {
	
		String content = Utils.getFileContent("day9input");
		long[] memory = Utils.parseLongList(content);
		
		VirtualMachine machine = new VirtualMachine();
		machine.setMemory(memory);
		machine.setDebug(false);
		
		machine.run();
	
	}
	
}
