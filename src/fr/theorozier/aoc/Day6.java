package fr.theorozier.aoc;

import java.io.IOException;
import java.util.*;

public class Day6 {
	
	private static class Orbit {
		
		final Orbit parent;
		final String center;
		final Set<Orbit> children;
		final int level;
		
		Orbit sanOrbit;
		Orbit youOrbit;
		
		Orbit(Orbit parent, String center, int level) {
			
			this.parent = parent;
			this.center = center;
			this.children = new HashSet<>();
			this.level = level;
			
		}
		
		boolean isSan() {
			return this.sanOrbit == this;
		}
		
		boolean isYou() {
			return this.youOrbit == this;
		}
		
		void backSan(Orbit child) {
			
			this.sanOrbit = child;
			
			if (this.parent != null)
				this.parent.backSan(this);
			
		}
		
		void backYou(Orbit child) {
			
			this.youOrbit = child;
			
			if (this.parent != null)
				this.parent.backYou(this);
			
		}
		
		void updateChildren(Map<String, Orbit> all, Map<String, Set<String>> rawOrbits) {
			
			if (this.center.equals("SAN")) {
				this.backSan(this);
			} else if (this.center.equals("YOU")) {
				this.backYou(this);
			}
			
			Set<String> children = rawOrbits.get(this.center);
			
			if (children == null)
				return;
			
			Orbit orbit;
			for (String child : children) {
				
				orbit = new Orbit(this, child, this.level + 1);
				this.children.add(orbit);
				all.put(child, orbit);
				
			}
			
			this.children.forEach(child -> child.updateChildren(all, rawOrbits));
			
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		Map<String, Set<String>> rawOrbits = new HashMap<>();
		
		String[] lines = Utils.getFileLines("day6input");
		String[] orbitParts;
		Set<String> children;
		int orbitsCount = 0;
		
		for (String line : lines) {
			
			orbitParts = line.split("\\)");
			
			if (orbitParts.length == 2) {
				
				children = rawOrbits.computeIfAbsent(orbitParts[0], orbit -> new HashSet<>());
				children.add(orbitParts[1]);
				orbitsCount++;
				
			}
		
		}
		
		if (!rawOrbits.containsKey("COM")) {
			
			System.out.println("No COM orbit.");
			return;
			
		}
		
		Map<String, Orbit> all = new HashMap<>();
		
		Orbit com = new Orbit(null, "COM", 0);
		com.updateChildren(all, rawOrbits);
		
		int total = 0;
		
		for (Orbit orbit : all.values())
			total += orbit.level;
		
		System.out.println("Total number of orbits : " + total);
		
		Orbit current = all.get("YOU").parent;
		Orbit last = null;
		int transfersCount = 0;
		
		while (!current.isSan()) {
			
			last = current;
			
			if (current.sanOrbit != null) {
				current = current.sanOrbit;
			} else {
				current = current.parent;
			}
			
			transfersCount++;
			
			System.out.println(last.center + " ==> " + current.center);
			
		}
		
		if (last != null) {
			current = last;
			transfersCount--;
		}
		
		System.out.println("Number of transfers : " + transfersCount);
		
	}
	
}
