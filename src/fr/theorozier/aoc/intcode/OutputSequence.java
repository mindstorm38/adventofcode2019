package fr.theorozier.aoc.intcode;

public class OutputSequence {
	
	private final long[] values;
	private int pos;
	
	public OutputSequence(int valuesCount) {
		
		if (valuesCount == 0)
			throw new IllegalArgumentException("Output sequence can't have no values.");
		
		this.values = new long[valuesCount];
		this.reset();
		
	}
	
	public void reset() {
		this.pos = 0;
	}
	
	public long getValue(int idx) {
		return this.values[idx];
	}
	
	public void handleMachineOutput(long v) {
		this.values[this.pos] = v;
		this.pos = (this.pos + 1) % this.values.length;
	}
	
}
