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
import javassist.bytecode.Opcode;
import cuchaz.m3l.util.BytecodeTools;

public class GtZeroReplacementHook extends BehaviorHook {
	
	private static final long serialVersionUID = -1079931895658437052L;
	
	public GtZeroReplacementHook(CtBehavior behavior, byte[] bytecode) {
		super(behavior, bytecode);
	}
	
	@Override
	protected void onApply(CtBehavior behavior, Bytecode bytecode)
	throws BadBytecode {
		
		bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
		
		// loop through the opcodes and change any GT/GE opcodes to ICMPGT/ICMPGE
		CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);
			switch (opcode) {
				case Opcode.IFGT:
					
					// overwrite the opcode
					iterator.writeByte(Opcode.IF_ICMPGT, index);
					
					// insert the method call
					iterator.insertAt(index, bytecode.get());
					behavior.getMethodInfo().getCodeAttribute().computeMaxStack();
				
				break;
				
				case Opcode.IFGE:
					
					// overwrite the opcode
					iterator.writeByte(Opcode.IF_ICMPGE, index);
					
					// insert the method call
					iterator.insertAt(index, bytecode.get());
					behavior.getMethodInfo().getCodeAttribute().computeMaxStack();
				
				break;
			}
		}
	}
}
