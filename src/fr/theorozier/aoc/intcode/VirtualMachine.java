package fr.theorozier.aoc.intcode;

import fr.theorozier.aoc.Utils;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VirtualMachine {
	
	private static long manualInputSupplier() {
		
		System.out.print("Input : ");
		
		Scanner scanner = new Scanner(System.in);
		long ret = 0;
		
		for(;;) {
			
			try {
				ret = scanner.nextLong(10);
				break;
			} catch (InputMismatchException ignored) {
				System.out.print("Invalid input, please retry : ");
				scanner.next();
			}
			
		}
		
		return ret;
		
	}
	
	private static void manualOutputConsumer(long value) {
		System.out.println("Output : " + value);
	}
	
	private static final Predicate<OpCodeType> TRUE_PREDICATE = (oct) -> true;
	private static final Predicate<OpCodeType> FALSE_PREDICATE = (oct) -> false;
	
	// Class //
	
	private long[] memory = null;
	private boolean debug = false;
	
	private Supplier<Long> inputSupplier;
	private Consumer<Long> outputConsumer;
	
	private final AtomicInteger pc = new AtomicInteger();
	private final AtomicInteger rb = new AtomicInteger(); // Relative Base Position
	
	public VirtualMachine() {
		
		this.setInputSupplier(VirtualMachine::manualInputSupplier);
		this.setOutputConsumer(VirtualMachine::manualOutputConsumer);
		
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void debug(String message) {
		if (this.debug) System.out.println(message);
	}
	
	public void debugParameter(int i, ParameterMode mode, long raw, long valueAtAddress) {
		this.debug("  Parameter " + i + " : " + mode.name() + " = " + raw + (mode == ParameterMode.IMMEDIATE ? "" : (" <" + valueAtAddress + ">")));
	}
	
	public void setInputSupplier(Supplier<Long> inputSupplier) {
		this.inputSupplier = inputSupplier;
	}
	
	public void setOutputConsumer(Consumer<Long> outputConsumer) {
		this.outputConsumer = outputConsumer;
	}
	
	private void checkMemorySet() {
		
		if (this.memory == null)
			throw new RuntimeException("No memory set.");
		
	}
	
	public void setMemory(long[] memory) {
		
		
		
		this.memory = memory;
		this.pc.set(0);
		this.rb.set(0);
		
	}
	
	public AtomicInteger getPc() {
		return this.pc;
	}
	
	public AtomicInteger getRb() {
		return this.rb;
	}
	
	public long[] getMemory() {
		return this.memory;
	}
	
	public void setAt(int address, long num) {
		this.checkMemorySet();
		this.upgradeMemoryForAddress(address);
		this.memory[address] = num;
	}
	
	public long getAt(int address) {
		this.checkMemorySet();
		this.upgradeMemoryForAddress(address);
		return this.memory[address];
	}
	
	private void upgradeMemoryForAddress(int address) {
		
		if (address >= this.memory.length) {
			
			long[] newMemory = new long[address + 1];
			System.arraycopy(this.memory, 0, newMemory, 0, this.memory.length);
			this.memory = newMemory;
			
		}
		
	}
	
	public long requestInput() {
		return this.inputSupplier.get();
	}
	
	public void requestOutput(long value) {
		this.outputConsumer.accept(value);
	}
	
	/**
	 * Run the program while.
	 * @param whileCondition The while condition.
	 * @return True if the program stopped from an HALT instruction,
	 * or False if stopped because of while condition.
	 */
	public boolean runWhile(Predicate<OpCodeType> whileCondition) {
		
		this.checkMemorySet();
		
		long current = -1;
		OpCodeType opCodeType = null;
		ParameterMode[] modes = new ParameterMode[2];
		
		while (opCodeType == null || whileCondition.test(opCodeType)) {
			
			try {
				
				current = this.memory[this.pc.get()];
				opCodeType = OpCodeType.fromIntCode((int) (current % 100L));
				
				if (opCodeType == null)
					throw new InvalidOpCodeException();
				
				if (modes.length != opCodeType.modesCount)
					modes = new ParameterMode[opCodeType.modesCount];
				
				for (int i = 0; i < modes.length; ++i)
					modes[i] = ParameterMode.values()[Utils.getNthDigit(current, 10, i + 2)];
				
				this.debug("[" + this.pc.get() + "] " + opCodeType.name() + " (" + current + ")");
				opCodeType.execute(this, modes, this.pc.get(), this.pc, this.rb);
				this.debug("  PC now at " + this.pc.get());
				this.debug("  RB now at " + this.rb.get());
				
			} catch (IndexOutOfBoundsException bounds) {
				
				System.out.println("Early End Of File.");
				bounds.printStackTrace();
				break;
				
			} catch (InvalidOpCodeException opcode) {
				
				System.out.println("Invalid Op Code at " + pc + " '" + current + "'.");
				break;
				
			} catch (StopVirtualProgramException stop) {
				return true;
			}
			
		}
		
		return false;
		
	}
	
	public boolean runOneStep() {
		return this.runWhile(FALSE_PREDICATE);
	}
	
	public boolean run() {
		return this.runWhile(TRUE_PREDICATE);
	}
	
}