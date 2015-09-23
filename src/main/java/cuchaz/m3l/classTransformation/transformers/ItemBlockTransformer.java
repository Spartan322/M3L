/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.item.ItemBlock;

public class ItemBlockTransformer implements ClassTransformer {

	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(ItemBlock.class.getName());
	}

	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
			throws NotFoundException, CannotCompileException {

		/* TODO
		ClassPool pool = c.getClassPool();
		
		// override build size
		compiler.replaceInt(
			c.getDeclaredMethod("onItemUse", new CtClass[] {
				pool.get(ItemStack.class.getName()),
				pool.get(EntityPlayer.class.getName()),
				pool.get(World.class.getName()),
				CtClass.intType,
				CtClass.intType,
				CtClass.intType, 
				CtClass.intType,
				CtClass.floatType,
				CtClass.floatType,
				CtClass.floatType
			}),
			255,
			BuildSizeEvent.class.getName(),
			"getBuildHeight"
		);
		*/
	}
}
