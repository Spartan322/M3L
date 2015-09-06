/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import java.io.IOException;
import java.io.Serializable;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.EntryFactory;
import cuchaz.m3l.util.BytecodeTools;

public abstract class BehaviorHook implements Serializable {
	private static final long serialVersionUID = -5345949606532667041L;
	
	private BehaviorEntry m_behaviorEntry;
	private byte[] m_savedBytecode;
	
	protected BehaviorHook(CtBehavior behaviorEntry, byte[] savedBytecode) {
		m_behaviorEntry = EntryFactory.getBehaviorEntry(behaviorEntry);
		m_savedBytecode = savedBytecode;
	}
	
	public BehaviorEntry getBehaviorEntry() {
		return m_behaviorEntry;
	}
	
	public void apply(CtBehavior behavior)
	throws BadBytecode, IOException {
		
		// make sure our hook matches this behavior
		BehaviorEntry behaviorEntry = EntryFactory.getBehaviorEntry(behavior);
		if (!behaviorEntry.equals(m_behaviorEntry)) {
			throw new IllegalArgumentException(String.format("Behavior to transform %s() does not match hooked behavior %s()",
				behaviorEntry,
				m_behaviorEntry
			));
		}
		
		onApply(behavior, BytecodeTools.readBytecode(m_savedBytecode));

		// stuff changed, rebuild the stack map
		CtClass c = behavior.getDeclaringClass();
		behavior.getMethodInfo().rebuildStackMapIf6(c.getClassPool(), c.getClassFile2());
	}
	
	protected abstract void onApply(CtBehavior behavior, Bytecode bytecode) throws BadBytecode;
}
