package fr.theorozier.aoc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day5 {
	
	public enum OpCodeType {
	
		ADD (1, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v1 = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				int v2 = getRealValue(machine,1, memory, modes[1], memory[pc + 2]);
				int at = memory[pc + 3];
				
				memory[at] = v1 + v2;
				pcAccess.set(pc + 4);
				
				machine.debug("  " + v1 + "+" + v2 + "=" + memory[at] + " ==> " + at);
				
			}
			
		},
		MULT (2, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v1 = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				int v2 = getRealValue(machine,1, memory, modes[1], memory[pc + 2]);
				int at = memory[pc + 3];
				
				memory[at] = v1 * v2;
				pcAccess.set(pc + 4);
				
				machine.debug("  " + v1 + "x" + v2 + "=" + memory[at] + " ==> " + at);
				
			}
			
		},
		INPUT (3, 0) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				memory[memory[pc + 1]] = machine.requestInput();
				pcAccess.set(pc + 2);
				
			}
			
		},
		OUTPUT (4, 1) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v = getRealValue(machine, 0, memory, modes[0], memory[pc + 1]);
				machine.requestOutput(v);
				pcAccess.set(pc + 2);
				
			}
			
		},
		JUMP_IF_TRUE (5, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
			
				int v = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				
				if (v != 0) {
					
					pcAccess.set(getRealValue(machine,1, memory, modes[1], memory[pc + 2]));
					machine.debug("  JUMP ==> " + pcAccess.get());
					
				} else {
					
					pcAccess.set(pc + 3);
					machine.debug("  NO JUMP");
					
				}
			
			}
			
		},
		JUMP_IF_FALSE (6, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				
				if (v == 0) {
					
					pcAccess.set(getRealValue(machine,1, memory, modes[1], memory[pc + 2]));
					machine.debug("  JUMP ==> " + pcAccess.get());
					
				} else {
					
					pcAccess.set(pc + 3);
					machine.debug("  NO JUMP");
					
				}
				
			}
			
		},
		LESS_THAN (7, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v1 = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				int v2 = getRealValue(machine,1, memory, modes[1], memory[pc + 2]);
				int at = memory[pc + 3];
				
				memory[at] = v1 < v2 ? 1 : 0;
				pcAccess.set(pc + 4);
				
				machine.debug("  (" + v1 + " < " + v2 + ") = " + memory[at] + " ==> " + at);
				
			}
			
		},
		EQUALS (8, 2) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				
				int v1 = getRealValue(machine,0, memory, modes[0], memory[pc + 1]);
				int v2 = getRealValue(machine,1, memory, modes[1], memory[pc + 2]);
				int at = memory[pc + 3];
				
				memory[at] = v1 == v2 ? 1 : 0;
				pcAccess.set(pc + 4);
				
				machine.debug("  (" + v1 + " == " + v2 + ") = " + memory[at] + " ==> " + at);
				
			}
			
		},
		END (99, 0) {
			
			@Override
			public void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException {
				throw new StopVirtualProgramException();
			}
			
		};
		
		public final int code;
		public final int modesCount;
		
		OpCodeType(int code, int modesCount) {
			
			this.code = code;
			this.modesCount = modesCount;
			
		}
		
		public abstract void execute(VirtualMachine machine, int[] memory, boolean[] modes, int pc, AtomicInteger pcAccess) throws RuntimeException;
	
		public static final Map<Integer, OpCodeType> INT_CODES = new HashMap<>(values().length);
		
		static {
			
			for (int i = 0; i < values().length; i++)
				INT_CODES.put(values()[i].code, values()[i]);
			
		}
		
		public static OpCodeType fromIntCode(int intCode) {
			return INT_CODES.get(intCode);
		}
		
		protected static int getRealValue(VirtualMachine machine, int param, int[] memory, boolean mode, int raw) {
			machine.debugParameter(param, mode, raw, mode ? 0 : memory[raw]);
			return mode ? raw : memory[raw];
		}
		
	}
	
	public static class StopVirtualProgramException extends RuntimeException {}
	public static class InvalidOpCodeException extends RuntimeException {}
	
	public static class VirtualMachine {
		
		private int[] memory = null;
		private boolean debug = false;
		
		public VirtualMachine() {}
		
		public void setDebug(boolean debug) {
			this.debug = debug;
		}
		
		public void debug(String message) {
			if (this.debug) System.out.println(message);
		}
		
		public void debugParameter(int i, boolean mode, int raw, int valueAtAddress) {
			this.debug("  Parameter " + i + " : " + (mode ? "value  " : "address") + " = " + raw + (mode ? "" : (" <" + valueAtAddress + ">")));
		}
		
		private void checkMemorySet() {
			
			if (this.memory == null)
				throw new RuntimeException("No memory set.");
			
		}
		
		public void setMemory(int[] memory) {
			this.memory = memory;
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
		
		public void requestOutput(int value) {
			System.out.println("Output : " + value);
		}
		
		public void run() {
			
			this.checkMemorySet();
			
			int current = -1;
			AtomicInteger pc = new AtomicInteger();
			OpCodeType opCodeType;
			boolean[] modes = new boolean[2];
			
			for (;;) {
				
				try {
					
					current = this.memory[pc.get()];
					opCodeType = OpCodeType.fromIntCode(current % 100);
					
					if (opCodeType == null)
						throw new InvalidOpCodeException();
					
					if (modes.length != opCodeType.modesCount)
						modes = new boolean[opCodeType.modesCount];
					
					for (int i = 0; i < modes.length; ++i)
						modes[i] = Utils.getNthDigit(current, 10, i + 2) == 1;
					
					this.debug("[" + pc.get() + "] " + opCodeType.name());
					opCodeType.execute(this, this.memory, modes, pc.get(), pc);
					this.debug("  PC now at " + pc.get());
					
				} catch (IndexOutOfBoundsException bounds) {
					
					System.out.println("Early End Of File.");
					bounds.printStackTrace();
					break;
					
				} catch (InvalidOpCodeException opcode) {
					
					System.out.println("Invalid Op Code at " + pc + " '" + current + "'.");
					break;
					
				} catch (StopVirtualProgramException stop) {
					break;
				}
				
			}
			
		}
		
	}
	
	public static void main(String[] args) throws IOException {
	
		String input = Utils.getFileContent("day5input");
		int[] memory = Utils.parseIntList(input);
		
		VirtualMachine machine = new VirtualMachine();
		machine.setDebug(false);
		machine.setMemory(memory.clone());
		machine.run();
	
	}
	
}
