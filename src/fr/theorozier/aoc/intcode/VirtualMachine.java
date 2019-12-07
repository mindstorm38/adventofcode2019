package fr.theorozier.aoc.intcode;

import fr.theorozier.aoc.Utils;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VirtualMachine {
	
	private static int manualInputSupplier() {
		
		System.out.print("Input : ");
		
		Scanner scanner = new Scanner(System.in);
		int ret = 0;
		
		for(;;) {
			
			try {
				ret = scanner.nextInt(10);
				break;
			} catch (InputMismatchException ignored) {
				System.out.print("Invalid input, please retry : ");
				scanner.next();
			}
			
		}
		
		return ret;
		
	}
	
	private static void manualOutputConsumer(int value) {
		System.out.println("Output : " + value);
	}
	
	// Class //
	
	private int[] memory = null;
	private boolean debug = false;
	
	private Supplier<Integer> inputSupplier;
	private Consumer<Integer> outputConsumer;
	
	private final AtomicInteger pc = new AtomicInteger();
	
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
	
	public void debugParameter(int i, boolean mode, int raw, int valueAtAddress) {
		this.debug("  Parameter " + i + " : " + (mode ? "value  " : "address") + " = " + raw + (mode ? "" : (" <" + valueAtAddress + ">")));
	}
	
	public void setInputSupplier(Supplier<Integer> inputSupplier) {
		this.inputSupplier = inputSupplier;
	}
	
	public void setOutputConsumer(Consumer<Integer> outputConsumer) {
		this.outputConsumer = outputConsumer;
	}
	
	private void checkMemorySet() {
		
		if (this.memory == null)
			throw new RuntimeException("No memory set.");
		
	}
	
	public void setMemory(int[] memory) {
		
		this.memory = memory;
		this.pc.set(0);
		
	}
	
	public int[] getMemory() {
		return this.memory;
	}
	
	public void setAt(int address, int num) {
		this.checkMemorySet();
		this.memory[address] = num;
	}
	
	public int getAt(int address) {
		this.checkMemorySet();
		return this.memory[address];
	}
	
	public int requestInput() {
		return this.inputSupplier.get();
	}
	
	public void requestOutput(int value) {
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
		
		int current = -1;
		OpCodeType opCodeType = null;
		boolean[] modes = new boolean[2];
		
		while (opCodeType == null || whileCondition.test(opCodeType)) {
			
			try {
				
				current = this.memory[this.pc.get()];
				opCodeType = OpCodeType.fromIntCode(current % 100);
				
				if (opCodeType == null)
					throw new InvalidOpCodeException();
				
				if (modes.length != opCodeType.modesCount)
					modes = new boolean[opCodeType.modesCount];
				
				for (int i = 0; i < modes.length; ++i)
					modes[i] = Utils.getNthDigit(current, 10, i + 2) == 1;
				
				this.debug("[" + this.pc.get() + "] " + opCodeType.name());
				opCodeType.execute(this, this.memory, modes, this.pc.get(), this.pc);
				this.debug("  PC now at " + this.pc.get());
				
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
	
	public boolean run() {
		return this.runWhile((opcode) -> true);
	}
	
}