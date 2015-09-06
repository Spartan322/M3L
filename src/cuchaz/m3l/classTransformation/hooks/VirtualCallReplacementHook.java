/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import java.util.ArrayList;
import java.util.List;

import javassist.CtBehavior;
import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.MethodEntry;
import cuchaz.m3l.util.BytecodeTools;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.Util;

public class VirtualCallReplacementHook extends BehaviorHook {
	
	private static final long serialVersionUID = -8461173566843804486L;
	
	private BehaviorEntry m_targetBehaviorCall;
	private boolean m_includeArguments;
	
	public VirtualCallReplacementHook(CtBehavior behavior, byte[] bytecode, BehaviorEntry targetBehaviorCall, boolean includeArguments) {
		super(behavior, bytecode);
		
		m_targetBehaviorCall = targetBehaviorCall;
		m_includeArguments = includeArguments;
	}
	
	@Override
	protected void onApply(CtBehavior behavior, Bytecode bytecode)
	throws BadBytecode {
		
		bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
		byte[] code = bytecode.get();
		
		ConstPool pool = behavior.getDeclaringClass().getClassFile().getConstPool();
		
		// get the list of extra arguments we want to pass
		List<String> extraArgs = new ArrayList<String>();
		if (m_includeArguments) {
			if (!Modifier.isStatic(behavior.getModifiers())) {
				extraArgs.add(Util.getClassDesc(behavior.getDeclaringClass().getName()));
			}
			extraArgs.addAll(Util.getSignatureArguments(behavior.getMethodInfo().getDescriptor()));
		}
		
		// build the instructions to load the arguments
		byte[] loadOps = null;
		if (!extraArgs.isEmpty()) {
			loadOps = createLoadOps(extraArgs);
		}
		
		// look for the invoke virtual instruction
		CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);
			switch (opcode) {
				case Opcode.INVOKEVIRTUAL:
					
					// read the method call info
					int methodRefIndex = iterator.byteAt(index + 1) << 8 | iterator.byteAt(index + 2);
					MethodEntry methodCall = EntryFactory.getMethodEntry(pool, methodRefIndex);
					
					// is this the right method?
					if (methodCall.equals(m_targetBehaviorCall)) {
						// replace the invoke virtual with an invoke static
						iterator.writeByte(code[0], index + 0);
						iterator.writeByte(code[1], index + 1);
						iterator.writeByte(code[2], index + 2);
						
						// load arguments
						if (loadOps != null) {
							iterator.insertAt(index, loadOps);
						}
					}
				
				break;
			}
		}
		
		if (extraArgs.size() > 0) {
			// increase the stack size for the method
			CodeAttribute attribute = behavior.getMethodInfo().getCodeAttribute();
			attribute.computeMaxStack();
			// TEMP
			//attribute.setMaxStack(attribute.getMaxStack() + extraArgs.size());
		}
	}
	
	private byte[] createLoadOps(List<String> extraArgs) {
		
		// build the ops to load the arguments
		List<Byte> ops = new ArrayList<Byte>();
		int pos = 0;
		for (String arg : extraArgs) {
			pos += addArgLoadOps(ops, arg, pos);
		}
		
		// convert the list to an array
		byte[] loadOps = new byte[ops.size()];
		for (int i = 0; i < loadOps.length; i++) {
			loadOps[i] = ops.get(i);
		}
		return loadOps;
	}
	
	private int addArgLoadOps(List<Byte> ops, String arg, int i) {
		switch (arg.charAt(0)) {
			case '[':
			case 'L':
				
				switch (i) {
					case 0:
						ops.add((byte)Opcode.ALOAD_0);
					break;
					case 1:
						ops.add((byte)Opcode.ALOAD_1);
					break;
					case 2:
						ops.add((byte)Opcode.ALOAD_2);
					break;
					case 3:
						ops.add((byte)Opcode.ALOAD_3);
					break;
					default:
						ops.add((byte)Opcode.ALOAD);
						ops.add((byte)i);
					break;
				}
				
				return 1;
				
			case 'D':
				
				switch (i) {
					case 0:
						ops.add((byte)Opcode.DLOAD_0);
					break;
					case 1:
						ops.add((byte)Opcode.DLOAD_1);
					break;
					case 2:
						ops.add((byte)Opcode.DLOAD_2);
					break;
					case 3:
						ops.add((byte)Opcode.DLOAD_3);
					break;
					default:
						ops.add((byte)Opcode.DALOAD);
						ops.add((byte)i);
					break;
				}
				
				return 2;
				
			case 'F':
				
				switch (i) {
					case 0:
						ops.add((byte)Opcode.FLOAD_0);
					break;
					case 1:
						ops.add((byte)Opcode.FLOAD_1);
					break;
					case 2:
						ops.add((byte)Opcode.FLOAD_2);
					break;
					case 3:
						ops.add((byte)Opcode.FLOAD_3);
					break;
					default:
						ops.add((byte)Opcode.FALOAD);
						ops.add((byte)i);
					break;
				}
				
				return 1;
				
			case 'I':
			case 'B':
			case 'S':
			case 'C':
			case 'Z':
				
				switch (i) {
					case 0:
						ops.add((byte)Opcode.ILOAD_0);
					break;
					case 1:
						ops.add((byte)Opcode.ILOAD_1);
					break;
					case 2:
						ops.add((byte)Opcode.ILOAD_2);
					break;
					case 3:
						ops.add((byte)Opcode.ILOAD_3);
					break;
					default:
						ops.add((byte)Opcode.IALOAD);
						ops.add((byte)i);
					break;
				}
				
				return 1;
				
			case 'J':
				
				switch (i) {
					case 0:
						ops.add((byte)Opcode.LLOAD_0);
					break;
					case 1:
						ops.add((byte)Opcode.LLOAD_1);
					break;
					case 2:
						ops.add((byte)Opcode.LLOAD_2);
					break;
					case 3:
						ops.add((byte)Opcode.LLOAD_3);
					break;
					default:
						ops.add((byte)Opcode.LALOAD);
						ops.add((byte)i);
					break;
				}
				
				return 2;
		}
		
		throw new Error("Unknown type: " + arg);
	}
}
