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
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.RegistryNamespaced;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.NumberUtils;
import cuchaz.m3l.util.Util;

public class ItemTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(Item.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		CtMethod method = c.getMethod("registerItems", "()V");
		
		// prevent registration of items for mod blocks (vanilla logic gets the ids wrong)
		// need to change:
		// itemRegistry.addObject(Block.getIdFromBlock(var4), var3, var5);
		// to
		// int blockId = Block.getIdFromBlock(var4);
		// if( blockId <= BlockRegistrationEvent.BlockNumberMaxVanilla || !ItemBlock.class.isAssignableFrom( var5.getClass() ) ) {
		//    itemRegistry.addObject(Block.getIdFromBlock(var4), var3, var5);
		// }
		method.instrument(new ExprEditor() {
			@Override
			public void edit(MethodCall call) throws CannotCompileException {
				if (call.getClassName().equals(RegistryNamespaced.class.getName()) && call.getMethodName().equals("addObject")) {
					call.replace("if( " + NumberUtils.class.getName() + ".isVanillaBlock( $1 ) || !( " + ItemBlock.class.getName() + ".class.isAssignableFrom( $3.getClass() ) ) ) { $proceed( $$ ); }");
				}
			}
		});
		
		// add hook for item registration
		method.insertAfter(getClass().getName() + ".onAfterItemsRegistered();");
		
		compiler.replaceBehavior(method);
		
		// Item getItemFromBlock( Block )
		method = c.getMethod("getItemFromBlock", "(" + Util.getClassDesc(Block.class) + ")" + Util.getClassDesc(Item.class));
		method.setBody("return " + ItemTransformer.class.getName() + ".onGetItemFromBlock( $1 );");
		compiler.replaceBehavior(method);
	}
	
	public static void onAfterItemsRegistered() {
		/* TODO: implement item registration
		// tell all the mods to register their items
		Mods.getInstance().sendEventToMods(new ItemRegistrationEvent());
		*/
	}
	
	public static Item onGetItemFromBlock(Block block) {
		int blockNumber = Block.getBlockIndex(block);
		if (NumberUtils.isModBlock(blockNumber)) {
			return Item.getItem(NumberUtils.getModItemNumberFromModBlockNumber(blockNumber));
		} else {
			return Item.getItem(blockNumber);
		}
	}
}
