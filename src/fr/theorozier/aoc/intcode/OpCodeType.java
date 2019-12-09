package fr.theorozier.aoc.intcode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum OpCodeType {

	ADD (1, 3) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v1 = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			long v2 = getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess);
			int at = getRealPosition(machine, modes[2], machine.getAt(pc + 3), rbAccess);
			
			machine.setAt(at, v1 + v2);
			pcAccess.set(pc + 4);
			
			machine.debug("  " + v1 + "+" + v2 + "=" + machine.getAt(at) + " ==> " + at);
			
		}
		
	},
	MULT (2, 3) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v1 = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			long v2 = getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess);
			int at = getRealPosition(machine, modes[2], machine.getAt(pc + 3), rbAccess);
			
			machine.setAt(at, v1 * v2);
			pcAccess.set(pc + 4);
			
			machine.debug("  " + v1 + "x" + v2 + "=" + machine.getAt(at) + " ==> " + at);
			
		}
		
	},
	INPUT (3, 1) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			int at = getRealPosition(machine, modes[0], machine.getAt(pc + 1), rbAccess);
			
			machine.setAt(at, machine.requestInput());
			pcAccess.set(pc + 2);
			
			machine.debug("  " + machine.getAt(at) + " ==> " + at);
			
		}
		
	},
	OUTPUT (4, 1) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v = getRealValue(machine, 0, modes[0], machine.getAt(pc + 1), rbAccess);
			machine.requestOutput(v);
			pcAccess.set(pc + 2);
			
		}
		
	},
	JUMP_IF_TRUE (5, 2) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			
			if (v != 0) {
				
				pcAccess.set((int) getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess));
				machine.debug("  JUMP ==> " + pcAccess.get());
				
			} else {
				
				pcAccess.set(pc + 3);
				machine.debug("  NO JUMP");
				
			}
		
		}
		
	},
	JUMP_IF_FALSE (6, 2) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			
			if (v == 0) {
				
				pcAccess.set((int) getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess));
				machine.debug("  JUMP ==> " + pcAccess.get());
				
			} else {
				
				pcAccess.set(pc + 3);
				machine.debug("  NO JUMP");
				
			}
			
		}
		
	},
	LESS_THAN (7, 3) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v1 = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			long v2 = getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess);
			int at = getRealPosition(machine, modes[2], machine.getAt(pc + 3), rbAccess);
			
			machine.setAt(at, v1 < v2 ? 1 : 0);
			pcAccess.set(pc + 4);
			
			machine.debug("  (" + v1 + " < " + v2 + ") = " + machine.getAt(at) + " ==> " + at);
			
		}
		
	},
	EQUALS (8, 3) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long v1 = getRealValue(machine,0, modes[0], machine.getAt(pc + 1), rbAccess);
			long v2 = getRealValue(machine,1, modes[1], machine.getAt(pc + 2), rbAccess);
			int at = getRealPosition(machine, modes[2], machine.getAt(pc + 3), rbAccess);
			
			machine.setAt(at, v1 == v2 ? 1 : 0);
			pcAccess.set(pc + 4);
			
			machine.debug("  (" + v1 + " == " + v2 + ") = " + machine.getAt(at) + " ==> " + at);
			
		}
		
	},
	RELATIVE_BASE_OFFSET(9, 1) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			
			long offset = getRealValue(machine, 0, modes[0], machine.getAt(pc + 1), rbAccess);
			int at = (int) (rbAccess.get() + offset);
			rbAccess.set(at);
			pcAccess.set(pc + 2);
			
			machine.debug("  RB += " + offset + " ==> " + at);
			
		}
		
	},
	END (99, 0) {
		
		@Override
		public void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException {
			throw new StopVirtualProgramException();
		}
		
	};
	
	public final int code;
	public final int modesCount;
	
	OpCodeType(int code, int modesCount) {
		
		this.code = code;
		this.modesCount = modesCount;
		
	}
	
	public abstract void execute(VirtualMachine machine, ParameterMode[] modes, int pc, AtomicInteger pcAccess, AtomicInteger rbAccess) throws RuntimeException;

	public static final Map<Integer, OpCodeType> INT_CODES = new HashMap<>(values().length);
	
	static {
		
		for (int i = 0; i < values().length; i++)
			INT_CODES.put(values()[i].code, values()[i]);
		
	}
	
	public static OpCodeType fromIntCode(int intCode) {
		return INT_CODES.get(intCode);
	}
	
	protected static long getRealValue(VirtualMachine machine, int param, ParameterMode mode, long raw, AtomicInteger rbAccess) {
		
		if (mode != ParameterMode.IMMEDIATE) {
			
			long position = (mode == ParameterMode.POSITION ? raw : rbAccess.get() + raw);
			long value = machine.getAt((int) position);
			machine.debugParameter(param, mode, raw, value);
			return value;
			
		} else {
			
			machine.debugParameter(param, mode, raw, 0);
			return raw;
			
		}
		
	}
	
	protected static int getRealPosition(VirtualMachine machine, ParameterMode mode, long raw, AtomicInteger rbAccess) {
		
		if (mode == ParameterMode.IMMEDIATE)
			return -1;
		
		return (int) (mode == ParameterMode.POSITION ? raw : rbAccess.get() + raw);
		
	}
	
}