/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import cuchaz.m3l.Side;

public interface ClassTransformer {
	boolean meetsRequirements(CtClass c);
	void compile(HookCompiler precopmiler, CtClass c, Side side) throws NotFoundException, CannotCompileException;
}
