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

public class InsertAfterVoidBehaviorHook extends BehaviorHook {
	
	private static final long serialVersionUID = -2465140513982825483L;
	
	public InsertAfterVoidBehaviorHook(CtBehavior behavior, byte[] bytes) {
		super(behavior, bytes);
	}
	
	@Override
	protected void onApply(CtBehavior behavior, Bytecode bytecode) throws BadBytecode {
		bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
		
		// loop through the opcodes and insert our snippet before each return
		CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);
			switch (opcode) {
				case Opcode.RETURN: // only need to use RETURN opcode since this is a void method
					// do the insertion
					index = iterator.insertAt(index, bytecode.get());
					iterator.insert(bytecode.getExceptionTable(), index);
				break;
			}
		}
	}
}
