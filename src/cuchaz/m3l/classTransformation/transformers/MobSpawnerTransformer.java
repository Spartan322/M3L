/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import java.util.Random;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.world.MobSpawner;
import net.minecraft.world.World;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.EntryFactory;

public class MobSpawnerTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(MobSpawner.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		compiler.replaceVirtualCall(
			c.getDeclaredMethod("getRandomBlockPosAboveSurface"),
			EntryFactory.getBehaviorEntry(Random.class, "nextInt", "(I)I"),
			EntryFactory.getClassEntry(getClass()),
			"getSpawnY",
			true
		);
	}
	
	public static int getSpawnY(Random rand, int upper, World world, int cubeX, int cubeZ) {
		
		// NOTE: the vanilla method has three calls to rand.nextInt()
		// make sure we get the one about blockY
		if (upper != 16) {
			
			ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
			if (chunkSystem != null) {
				Integer blockY = chunkSystem.getRandomBlockYForMobSpawnAttempt(rand, upper, world, cubeX, cubeZ);
				if (blockY != null) {
					return blockY;
				}
			}
		}
		
		// no override chosen, do the default thing
		return rand.nextInt(upper);
	}
}
