package fr.theorozier.aoc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day14 {
	
	private static class Reaction {
		
		private static final Reaction ORE_DUMMY_REACTION = new Reaction("ORE", 1);
	
		private final HashMap<String, Integer> ingredients;
		private final HashMap<Reaction, Integer> ingredientsReactions;
		private final String result;
		private final int resultCount;
		
		Reaction(String result, int resultCount) {
			
			this.ingredients = new HashMap<>();
			this.ingredientsReactions = new HashMap<>();
			this.result = result;
			this.resultCount = resultCount;
			
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Reaction reaction = (Reaction) o;
			return resultCount == reaction.resultCount &&
					Objects.equals(result, reaction.result);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(result, resultCount);
		}
		
		@Override
		public String toString() {
			return "Reaction{" +
					"result='" + result + '\'' +
					", resultCount=" + resultCount +
					'}';
		}
		
	}
	
	public static void main(String[] args) throws IOException {
	
		HashMap<String, Reaction> reactions = new HashMap<>();
		
		String[] lines = Utils.getFileLines("day14input");
	
		for (String line : lines) {
			
			String[] parts = line.split("=>|,");
			
			Reaction reaction = null;
			
			for (int i = parts.length - 1; i >= 0; --i) {
				
				try {
					
					String[] elementParts = parts[i].trim().split(" ", 2);
					int count = Integer.parseInt(elementParts[0]);
					String element = elementParts[1];
					
					if (reaction == null) {
						
						reaction = new Reaction(element, count);
						reactions.put(element, reaction);
						
					} else {
						reaction.ingredients.put(element, count);
					}
					
				} catch (NumberFormatException ignored) {}
				
			}
			
		}
		
		reactions.forEach((elt, reaction) -> {
			reaction.ingredients.forEach((ingredient, count) -> {
				Reaction react = reactions.getOrDefault(ingredient, Reaction.ORE_DUMMY_REACTION);
				reaction.ingredientsReactions.put(react, count);
			});
		});
		
		List<List<Reaction>> levels = new ArrayList<>();
		List<Reaction> leveledReactions = new ArrayList<>();
		leveledReactions.add(Reaction.ORE_DUMMY_REACTION);
		levels.add(Collections.singletonList(leveledReactions.get(0)));
		
		List<Reaction> remainsReactions = new ArrayList<>(reactions.values());
		
		while (!remainsReactions.isEmpty()) {
			
			List<Reaction> levelReaction = new ArrayList<>();
			Iterator<Reaction> remainsIt = remainsReactions.iterator();
			
			while (remainsIt.hasNext()) {
				
				Reaction child = remainsIt.next();
				boolean valid = true;
				
				for (Reaction childIngredient : child.ingredientsReactions.keySet()) {
					if (!leveledReactions.contains(childIngredient)) {
						valid = false;
						break;
					}
				}
				
				if (valid) {
					levelReaction.add(child);
					remainsIt.remove();
				}
				
			}
			
			levels.add(levelReaction);
			leveledReactions.addAll(levelReaction);
			
		}
		
		long oreCount = 0;
		long fuelCount = 4000000L; // Starting from approximated high value
		Map<Reaction, Long> nextReactions = new HashMap<>();
		
		while (oreCount < 1000000000000L) {
			
			nextReactions.clear();
			nextReactions.put(reactions.get("FUEL"), fuelCount);
			
			for (int i = levels.size() - 1; i >= 0; --i) {
				
				List<Reaction> levelReactions = levels.get(i);
				System.out.println("Level " + i + " : " + levelReactions);
				System.out.println(" - " + nextReactions);
				
				for (Reaction levelReaction : levelReactions) {
					
					long countNeeded = nextReactions.getOrDefault(levelReaction, 0L);
					nextReactions.remove(levelReaction);
					
					if (countNeeded == 0)
						throw new IllegalStateException("Invalid needed count !");
					
					if (levelReaction == Reaction.ORE_DUMMY_REACTION) {
						
						oreCount = countNeeded;
						break;
						
					}
					
					long factor = (long) Math.ceil(countNeeded / (double) levelReaction.resultCount);
					
					levelReaction.ingredientsReactions.forEach((childReaction, childCount) -> {
						nextReactions.put(childReaction, nextReactions.getOrDefault(childReaction, 0L) + ((long) childCount * factor));
					});
					
				}
				
			}
			
			System.out.println("Ore need : " + oreCount + " with " + (fuelCount++) + " fuel.");
			
		}
		
		// Final answer (part 2) : 4076490
		
	}
	
}
