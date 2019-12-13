package fr.theorozier.aoc;

import fr.theorozier.aoc.intcode.OpCodeType;
import fr.theorozier.aoc.intcode.OutputSequence;
import fr.theorozier.aoc.intcode.VirtualMachine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Day13 {
	
	private static class ScreenTile {
		
		final int x, y;
		
		ScreenTile(int x, int y) {
			
			this.x = x;
			this.y = y;
			
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ScreenTile that = (ScreenTile) o;
			return x == that.x &&
					y == that.y;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}
		
	}
	
	private enum ScreenTileType {
		EMPTY (' '),
		WALL ('█'),
		BLOCK ('B'),
		HORIZONTAL_PADDLE ('-'),
		BALL ('●');
		final char c;
		ScreenTileType(char c) { this.c = c; }
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String content = Utils.getFileContent("day13input");
		long[] memory = Utils.parseLongList(content);
		memory[0] = 2; // Play for Free EZ
		
		VirtualMachine machine = new VirtualMachine();
		machine.setMemory(memory);
		
		AtomicInteger outputCounter = new AtomicInteger();
		AtomicInteger nextInput = new AtomicInteger();
		
		OutputSequence sequence = new OutputSequence(3);
		machine.setOutputConsumer(sequence::handleMachineOutput);
		
		machine.setInputSupplier(() -> (long) nextInput.get());
		
		Map<ScreenTile, ScreenTileType> tiles = new HashMap<>();
		
		int maxX = 0;
		int maxY = 0;
		
		int ballX = 0;
		int paddleX = 0;
		
		int score = 0;
		
		while (true) {
			
			outputCounter.set(0);
			sequence.reset();
			
			if (machine.runWhile(oct -> {
				
				if (oct == OpCodeType.OUTPUT) {
					return outputCounter.incrementAndGet() < 3;
				} else {
					return true;
				}
				
			})) break;
			
			int x = (int) sequence.getValue(0);
			int y = (int) sequence.getValue(1);
			
			if (x == -1 && y == 0) { // Display score
				
				score = (int) sequence.getValue(2);
			
			} else {
				
				ScreenTileType type = ScreenTileType.values()[(int) sequence.getValue(2)];
				
				tiles.put(new ScreenTile(x, y), type);
				
				if (type == ScreenTileType.BALL) {
					ballX = x;
				} else if (type == ScreenTileType.HORIZONTAL_PADDLE) {
					paddleX = x;
				}
				
				if (x > maxX) maxX = x;
				if (y > maxY) maxY = y;
				
			}
			
			nextInput.set(Integer.signum(ballX - paddleX));
			
			for (int dy = 0; dy <= maxY; ++dy) {
				
				for (int dx = 0; dx <= maxX; ++dx) {
					
					ScreenTileType type = tiles.getOrDefault(new ScreenTile(dx, dy), ScreenTileType.EMPTY);
					System.out.print(type.c);
					
				}
				
				if (dy == 2)
					System.out.print("   Score : " + score);
				
				System.out.println();
				
			}
			
		}
		
	}
	
}
