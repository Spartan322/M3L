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
import cuchaz.m3l.util.BytecodeTools;

public class InsertBeforeBehaviorHook extends BehaviorHook {
	
	private static final long serialVersionUID = -2465140513982825483L;
	
	public InsertBeforeBehaviorHook(CtBehavior behavior, byte[] bytes) {
		super(behavior, bytes);
	}
	
	@Override
	protected void onApply(CtBehavior behavior, Bytecode bytecode)
	throws BadBytecode {
		
		bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
		
		// insert at beginning
		int index = 0;
		
		// do the insertion
		CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
		index = iterator.insertAt(index, bytecode.get());
		iterator.insert(bytecode.getExceptionTable(), index);
	}
}
