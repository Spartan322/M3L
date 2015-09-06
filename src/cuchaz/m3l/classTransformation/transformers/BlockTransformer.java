/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.NumberUtils;
import cuchaz.m3l.util.Util;

public class BlockTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(Block.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		// add hook for block registration
		compiler.insertAfterVoidBehavior(c.getMethod("registerBlocks", "()V"), getClass().getName() + ".afterBlocksRegistered();");
		
		// Block getBlockFromItem( Item )
		CtMethod method = c.getMethod("getBlockFromItem", "(" + Util.getClassDesc(Item.class) + ")" + Util.getClassDesc(Block.class));
		method.setBody("return " + BlockTransformer.class.getName() + ".onGetIdFromItem( $1 );");
		compiler.replaceBehavior(method);
	}
	
	public static void afterBlocksRegistered() {
		// tell all the mods to register their blocks
		/* TODO: implement block registration
		Mods.getInstance().sendEventToMods(new BlockRegistrationEvent());
		*/
	}
	
	public static Block onGetIdFromItem(Item item) {
		int itemNumber = Item.getItemId(item);
		if (NumberUtils.isModItemBlock(itemNumber)) {
			return Block.getBlockFromIndex(NumberUtils.getModBlockNumberFromModItemNumber(itemNumber));
		} else {
			return Block.getBlockFromIndex(itemNumber);
		}
	}
}
