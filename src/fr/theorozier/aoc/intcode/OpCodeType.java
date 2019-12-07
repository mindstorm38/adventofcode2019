package fr.theorozier.aoc.intcode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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