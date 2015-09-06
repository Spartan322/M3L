/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import javassist.CtBehavior;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;
import cuchaz.enigma.bytecode.ConstPoolEditor;
import cuchaz.enigma.bytecode.InfoType;
import cuchaz.m3l.util.BytecodeTools;

public class IntReplacementHook extends BehaviorHook {
	
	private static final long serialVersionUID = -1079931895658437052L;
	
	private int m_val;
	
	public IntReplacementHook(CtBehavior behavior, int val, byte[] bytecode) {
		super(behavior, bytecode);
		m_val = val;
	}
	
	@Override
	protected void onApply(CtBehavior behavior, Bytecode bytecode)
	throws BadBytecode {
		
		bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
		ConstPool constPool = behavior.getDeclaringClass().getClassFile().getConstPool();
		ConstPoolEditor editor = new ConstPoolEditor(constPool);
		
		// loop through the opcodes and insert our snippet on top of the constant load call
		CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);
			int numBytes = 0;
			int oldVal = 0;
			switch (opcode) {
				case Opcode.BIPUSH:
					numBytes = 2;
					oldVal = iterator.byteAt(index + 1);
				break;
				
				case Opcode.SIPUSH:
					numBytes = 3;
					oldVal = iterator.byteAt(index + 1) << 8 | iterator.byteAt(index + 2);
				break;
				
				case Opcode.DCONST_0:
					numBytes = 1;
					oldVal = 0;
				break;
				
				case Opcode.DCONST_1:
					numBytes = 1;
					oldVal = 1;
				break;
				
				case Opcode.LDC: {
					// is this an int constant?
					int constIndex = iterator.byteAt(index + 1);
					if (editor.getItem(constIndex).getType() == InfoType.IntegerInfo) {
						numBytes = 2;
						oldVal = constPool.getIntegerInfo(constIndex);
					}
				}
				break;
				
				case Opcode.LDC_W: {
					// is this an int constant?
					int constIndex = iterator.byteAt(index + 1) << 8 | iterator.byteAt(index + 2);
					if (editor.getItem(constIndex).getType() == InfoType.IntegerInfo) {
						numBytes = 3;
						oldVal = constPool.getIntegerInfo(constIndex);
					}
				}
				break;
			}
			
			// did we find constant info?
			if (numBytes > 0) {
				
				// should we replace this const?
				if (oldVal == m_val) {
					
					// the opcode is 1-3 bytes, invokestatic is at least 3 bytes (with loads), so create space if needed
					int spaceNeeded = bytecode.getSize() - numBytes;
					if (spaceNeeded > 0) {
						iterator.insertGap(spaceNeeded);
					}
					
					// now we have a few bytes we can write to safely
					iterator.write(bytecode.get(), index);
					behavior.getMethodInfo().getCodeAttribute().computeMaxStack();
				}
			}
		}
	}
}
