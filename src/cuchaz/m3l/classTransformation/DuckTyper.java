/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;


public class DuckTyper {

	@Retention(value=RetentionPolicy.RUNTIME)
	@Target(value=ElementType.METHOD)
	public static @interface RemoveCheckCasts {}

	public void removeCheckCasts(CtClass c) {
		for (CtBehavior behavior : c.getDeclaredBehaviors()) {
			try {
				if (behavior.getAnnotation(RemoveCheckCasts.class) != null) {
					MethodInfo info = behavior.getMethodInfo();
					CodeAttribute attribute = info.getCodeAttribute();
					if (attribute != null) {
						removeCheckCasts(attribute);
					}
				}
			} catch (ClassNotFoundException ex) {
				// I have no idea how this can even happen...
				throw new Error(ex);
			}
		}
	}

	private void removeCheckCasts(CodeAttribute attribute) {
		CodeIterator iter = attribute.iterator();
		try {
			while (iter.hasNext()) {
				int pos = iter.next();
				int op = iter.byteAt(pos);
				if (op == Opcode.CHECKCAST) {
					iter.writeByte(Opcode.NOP, pos+0);
					iter.writeByte(Opcode.NOP, pos+1);
					iter.writeByte(Opcode.NOP, pos+2);
				}
			}
		} catch (BadBytecode ex) {
			throw new Error("Can't filter out checkcasts", ex);
		}
	}
}
