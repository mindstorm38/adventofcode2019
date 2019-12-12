package fr.theorozier.aoc;

import java.util.Arrays;

public class Day12 {
	
	private static class Moon {
		
		private final int uid;
		
		private final int[] current;
		private final int[] initial;
		
		private final long[] cycles = new long[6];
		private byte cyclesOk = 0;
		
		Moon(int uid, int x, int y, int z) {
			
			this.uid = uid;
			
			this.current = new int[] {x, y, z, 0, 0, 0};
			this.initial = this.current.clone();
			
			Arrays.fill(this.cycles, -1);
			
		}
		
		void updateMotion(Moon otherMoon) {
			
			for (int i = 0; i < 3; ++i)
				this.current[i + 3] += Integer.signum(otherMoon.current[i] - this.current[i]);
			
		}
		
		void applyMotion() {
			
			for (int i = 0; i < 3; ++i)
				this.current[i] += current[i + 3];
			
		}
		
		boolean isCurrentSame(int axisIndex) {
			return this.current[axisIndex] == this.initial[axisIndex];
		}
		
		boolean checkSameAsLast(long currentCycle) {
			
			for (int i = 0; i < 6; ++i) {
				if (this.cycles[i] == -1 && this.current[i] == this.initial[i]) {
					
					this.cycles[i] = currentCycle;
					this.cyclesOk |= 1 << i;
					
					System.out.println("Sub Cycle " + this.uid + "/" + i);
					
				}
			}
			
			if (this.cyclesOk == 0b111111)
				System.out.println("Sub Cycles " + this.uid + " : " + Arrays.toString(this.cycles));
			
			return this.cyclesOk == 0b111111;
			
		}
		
		long getMoonCycle() {
			return Utils.lcm(this.cycles);
		}
		
		int getPotentialEnergy() {
			return Math.abs(this.current[0]) + Math.abs(this.current[1]) + Math.abs(this.current[2]);
		}
		
		int getKineticEnergy() {
			return Math.abs(this.current[3]) + Math.abs(this.current[4]) + Math.abs(this.current[5]);
		}
		
		int getTotalEnergy() {
			return this.getPotentialEnergy() * this.getKineticEnergy();
		}
		
		@Override
		public String toString() {
			return "Moon{" +
					"current=" + Arrays.toString(current) +
					'}';
		}
		
	}
	
	public static void main(String[] args) {
		
		Moon[] system = new Moon[4];
		system[0] = new Moon(0, 16, -11, 2);
		system[1] = new Moon(1, 0, -4, 7);
		system[2] = new Moon(2, 6, 4, -10);
		system[3] = new Moon(3, -3, -2, -4);
		
		
		//system[0] = new Moon(0, -8, -10, 0);
		//system[1] = new Moon(1, 5, 5, 10);
		//system[2] = new Moon(2, 2, -7, 3);
		//system[3] = new Moon(3, 9, -8, -3);
		
		boolean allCycles = true;
		long step = 0;
		
		long[] cycles = new long[3];
		int cyclesOk = 0;
		Arrays.fill(cycles, -1);
		
		boolean isSame;
		int i, j;
		
		while (allCycles) {
			
			step++;
			
			for (Moon moon : system) {
				for (Moon other : system) {
					if (moon != other) {
						moon.updateMotion(other);
					}
				}
			}
			
			for (Moon moon : system)
				moon.applyMotion();
			
			for (j = 0; j < 3; ++j) {
				
				if (cycles[j] != -1)
					continue;
				
				isSame = true;
				
				for (i = 0; i < system.length; ++i) {
					
					if (!system[i].isCurrentSame(j)) {
						
						isSame = false;
						break;
						
					}
				
				}
				
				if (isSame) {
					
					cycles[j] = step + 1;
					cyclesOk |= 1 << j;
					
					if (cyclesOk == 0b111) {
						
						allCycles = false;
						break;
						
					}
					
				}
				
			}
			
			if (step % 10000000 == 0) System.out.println("step " + step);
		
		}
		
		System.out.println(Arrays.toString(cycles));
		
		System.out.println(Utils.lcm(cycles));
		
		/*
		for (Moon moon : system)
			System.out.println(moon);
		
		int total = system.stream().mapToInt(Moon::getTotalEnergy).sum();
		
		System.out.println("Total energy : " + total);
		*/
	
	}
	
}
