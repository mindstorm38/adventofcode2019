package fr.theorozier.aoc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class Day10 {
	
	private static class AsteroidMap {
	
		private final int width;
		private final int height;
		private final HashSet<StationPos> stations;
		private final Map<StationPos, Map<StationPos, DeltaAngle>> stationsAngles;
		private final Map<StationPos, HashSet<StationPos>> stationsConnections;
		
		public AsteroidMap(int width, int height) {
			
			this.width = width;
			this.height = height;
			this.stations = new HashSet<>();
			this.stationsAngles = new HashMap<>();
			this.stationsConnections = new HashMap<>();
			
		}
		
		public void addStation(int x, int y) {
			this.stations.add(new StationPos(x, y));
		}
		
		public void computeAngles() {
			
			this.stationsAngles.clear();
			
			for (StationPos from : this.stations) {
				
				this.stationsAngles.put(from, new HashMap<>());
				
				for (StationPos to : this.stations) {
					if (from != to) {
						this.stationsAngles.get(from).put(to, new DeltaAngle(from, to));
					}
				}
				
			}
			
		}
		
		public void computeConnections() {
			
			this.stationsConnections.clear();
			
			this.stationsAngles.forEach((from, toAngle) -> {
				
				this.stationsConnections.put(from, new HashSet<>());
				
				toAngle.forEach((to, angle) -> {
					
					int x = from.getX();
					int y = from.getY();
					StationPos pos;
					
					for (int i = 0; i < angle.getMult(); ++i) {
						
						x += angle.getX();
						y += angle.getY();
						pos = new StationPos(x, y);
						
						if (pos.equals(to)) {
							this.stationsConnections.get(from).add(to);
						} else if (this.stations.contains(pos)) {
							break;
						}
						
					}
					
				});
				
			});
			
		}
		
		public List<StationPos> computeLaserVaporization(StationPos pos) {
		
			if (!this.stationsAngles.containsKey(pos))
				throw new IllegalArgumentException("Invalid station pos, not in this map.");
			
			TreeMap<DeltaAngle, TreeSet<DeltaAngleToAsteroid>> angles = new TreeMap<>(DeltaAngle.buildAngleComparator());
			
			this.stationsAngles.get(pos).forEach((asteroid, angle) -> {
				
				if (!angles.containsKey(angle)) {
					angles.put(angle, new TreeSet<>(DeltaAngleToAsteroid.buildDistComparator()));
				}
				
				angles.get(angle).add(new DeltaAngleToAsteroid(angle, asteroid));
				
			});
			
			List<TreeSet<DeltaAngleToAsteroid>> anglesList = new ArrayList<>();
			angles.forEach((angle, set) -> anglesList.add(set));
			
			List<StationPos> vaporizeOrder = new ArrayList<>();
			
			TreeSet<DeltaAngleToAsteroid> angleAsteroids;
			DeltaAngleToAsteroid angleToAsteroid;
			
			while (!anglesList.isEmpty()) {
				
				Iterator<TreeSet<DeltaAngleToAsteroid>> it = anglesList.iterator();
				
				while (it.hasNext()) {
					
					angleAsteroids = it.next();
					angleToAsteroid = angleAsteroids.stream().findFirst().orElse(null); // Should never be null
					
					angleAsteroids.remove(angleToAsteroid);
					vaporizeOrder.add(angleToAsteroid.getPos());
					
					if (angleAsteroids.isEmpty())
						it.remove();
					
				}
				
			}
			
			// Animation
			for (StationPos toVaporize : vaporizeOrder) {
				
				this.showMap();
				System.out.println("\n");
				this.stations.remove(toVaporize);
				
				/*
				try {
					System.in.read();
				} catch (IOException e) { }
				*/
				
			}
			
			this.showMap();
			
			return vaporizeOrder;
			
		}
		
		public Map<StationPos, HashSet<StationPos>> getStationsConnections() {
			return this.stationsConnections;
		}
		
		public int getMaxConnectionCount() {
			
			return this.stationsConnections.values().stream()
					.mapToInt(HashSet::size)
					.max()
					.orElse(0);
			
		}
		
		public StationPos getMaxConnectionPos() {
			
			int max = this.getMaxConnectionCount();
			
			for (Map.Entry<StationPos, HashSet<StationPos>> pos : this.stationsConnections.entrySet())
				if (pos.getValue().size() == max)
					return pos.getKey();
				
			return null;
			
		}
		
		public void showMap(Function<HashSet<StationPos>, String> stationRenderer) {
			
			StationPos pos;
			for (int y = 0; y < this.height; ++y) {
				
				for (int x = 0; x < this.width; ++x) {
					
					pos = new StationPos(x, y);
					
					if (this.stations.contains(pos)) {
						System.out.print(stationRenderer.apply(this.stationsConnections.get(pos)));
					} else {
						System.out.print('.');
					}
				}
				
				System.out.println();
				
			}
			
		}
		
		public void showMap() {
			
			int count = this.getMaxConnectionCount();
			this.showMap(connections -> connections.size() == count ? "&" : "#");
			
		}
	
	}
	
	private static class StationPos {
		
		private final int x, y;
		
		public StationPos(int x, int y) {
			
			this.x = x;
			this.y = y;
			
		}
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			StationPos that = (StationPos) o;
			return x == that.x && y == that.y;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}
		
	}
	
	private static class DeltaAngle {
		
		private final int x, y;
		private final int mult;
		private final double angle;
		
		public DeltaAngle(int rawDx, int rawDy) {
			
			if (rawDx == 0) {
				
				this.x = 0;
				this.y = Integer.signum(rawDy);
				this.mult = Math.abs(rawDy);
				
			} else if (rawDy == 0) {
				
				this.x = Integer.signum(rawDx);
				this.y = 0;
				this.mult = Math.abs(rawDx);
				
			} else {
				
				this.mult = BigInteger.valueOf(rawDx).gcd(BigInteger.valueOf(rawDy)).intValue();
				this.x = rawDx / this.mult;
				this.y = rawDy / this.mult;
				
			}
			
			double angle = Math.toDegrees(Math.atan2(this.y, this.x)) + 90.0;
			this.angle = angle + (angle < 0 ? 360.0 : angle >= 360 ? angle - 360.0 : 0.0);
			
		}
		
		public DeltaAngle(StationPos from, StationPos to) {
			this(to.x - from.x, to.y - from.y);
		}
		
		protected DeltaAngle(DeltaAngle angle) {
			
			this.x = angle.x;
			this.y = angle.y;
			this.mult = angle.mult;
			this.angle = angle.angle;
			
		}
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		public int getMult() {
			return this.mult;
		}
		
		public double getAngle() {
			return this.angle;
		}
		
		public static Comparator<DeltaAngle> buildAngleComparator() {
			return Comparator.comparingDouble(DeltaAngle::getAngle);
		}
		
	}
	
	private static class DeltaAngleToAsteroid extends DeltaAngle {
		
		private final StationPos pos;
		
		public DeltaAngleToAsteroid(DeltaAngle angle, StationPos pos) {
			
			super(angle);
			this.pos = pos;
			
		}
		
		public StationPos getPos() {
			return this.pos;
		}
		
		public static Comparator<DeltaAngleToAsteroid> buildDistComparator() {
			return Comparator.comparingInt(DeltaAngle::getMult);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
	
		String[] lines = Utils.getFileLines("day10input");
		
		int width = lines[0].length();
		int height = 0;
		
		for (String line : lines)
			if (!line.isEmpty())
				++height;
		
		AsteroidMap map = new AsteroidMap(width, height);
		
		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x)
				if (lines[y].charAt(x) == '#')
					map.addStation(x, y);
		
		map.computeAngles();
		map.computeConnections();
		
		int count = map.getMaxConnectionCount();
		StationPos pos = map.getMaxConnectionPos();
		
		System.out.println();
		System.out.println("Max count : " + count);
		System.out.println();
		
		List<StationPos> vaporizeOrder = map.computeLaserVaporization(pos);
		StationPos vaporizedAsteroid = vaporizeOrder.get(199);
		
		System.out.println(vaporizedAsteroid.getX() + "/" + vaporizedAsteroid.getY());
		
	}
	
}
