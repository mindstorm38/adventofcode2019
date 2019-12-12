package fr.theorozier.aoc;

import fr.theorozier.aoc.intcode.OpCodeType;
import fr.theorozier.aoc.intcode.VirtualMachine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Day11 {
	
	private static class PanelMap {
		
		private final HashMap<Panel, Color> panels = new HashMap<>();
		private int minX, minY, maxX, maxY;
		
		PanelMap() {
			
			this.minX = this.minY = 0;
			this.maxX = this.maxY = 0;
			
		}
		
		Color getPanelColor(int x, int y) {
			return this.panels.getOrDefault(new Panel(x, y), Color.BLACK);
		}
		
		void setPanelColor(int x, int y, Color color) {
			
			this.panels.put(new Panel(x, y), color);
			
			if (x < minX) {
				minX = x;
			} else if (x > maxX) {
				maxX = x;
			}
			
			if (y < minY) {
				minY = y;
			} else if (y > maxY) {
				maxY = y;
			}
			
		}
	
	}
	
	private static class Panel {
		
		private final int x, y;
		
		Panel(int x, int y) {
			
			this.x = x;
			this.y = y;
			
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Panel panel = (Panel) o;
			return x == panel.x && y == panel.y;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}
		
	}
	
	private enum Color { BLACK, WHITE }
	
	public static void main(String[] args) throws IOException {
	
		String content = Utils.getFileContent("day11input");
		long[] memory = Utils.parseLongList(content);
		
		PanelMap panelMap = new PanelMap();
		panelMap.setPanelColor(0, 0, Color.WHITE);
		
		int x = 0;
		int y = 0;
		int facing = 0;
		
		VirtualMachine machine = new VirtualMachine();
		machine.setDebug(false);
		machine.setMemory(memory);
		
		AtomicLong input = new AtomicLong();
		AtomicInteger outputType = new AtomicInteger();
		AtomicInteger outputColor = new AtomicInteger();
		AtomicInteger outputRot = new AtomicInteger();
		
		machine.setInputSupplier(input::get);
		machine.setOutputConsumer((v) -> {
			
			if (outputType.getAndIncrement() == 0) {
				outputColor.set(Math.toIntExact(v));
			} else {
				outputRot.set(Math.toIntExact(v));
			}
			
		});
		
		Color currentColor;
		
		while (true) {
			
			currentColor = panelMap.getPanelColor(x, y);
			input.set(currentColor.ordinal());
			
			outputType.set(0);
			
			if (machine.runWhile((oct) -> oct != OpCodeType.OUTPUT))
				break;
			
			if (machine.runWhile((oct) -> oct != OpCodeType.OUTPUT))
				break;
			
			panelMap.setPanelColor(x, y, Color.values()[outputColor.get()]);
			facing = (outputRot.get() == 0 ? (facing - 1) : (facing + 1)) % 4;
			
			if (facing < 0)
				facing += 4;
			
			switch (facing) {
				case 0:
					--y;
					break;
				case 1:
					++x;
					break;
				case 2:
					++y;
					break;
				case 3:
					--x;
					break;
			}
			
		}
		
		int xOffset = panelMap.minX;
		int yOffset = panelMap.minY;
		int xWidth = panelMap.maxX - panelMap.minX;
		int yWidth = panelMap.maxY - panelMap.minY;
		
		for (int yDraw = 0; yDraw <= yWidth; ++yDraw) {
			for (int xDraw = 0; xDraw <= xWidth; ++xDraw) {
				
				Color color = panelMap.getPanelColor(xOffset + xDraw, yOffset + yDraw);
				
				if (color == Color.BLACK) {
					System.out.print(' ');
				} else {
					System.out.print('█');
				}
			
			}
			System.out.println();
		}
		
		System.out.println("Panels painted : " + panelMap.panels.size());
		
	}
	
}
