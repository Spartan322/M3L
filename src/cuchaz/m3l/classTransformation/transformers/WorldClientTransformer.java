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
import javassist.NotFoundException;
import net.minecraft.world.WorldClient;
import net.minecraft.world.gen.ClientChunkCache;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;

public class WorldClientTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(WorldClient.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		// override the client chunk cache
		compiler.insertBeforeBehavior(
			c.getDeclaredMethod("createChunkCache"),
			ClientChunkCache.class.getName() + " override = " + getClass().getName() + ".getChunkCache(this);"
			+ "if (override != null) { this.clientChunkCache = override; return override; }"
		);

		// hook the tick method
		compiler.insertAfterVoidBehavior(
			c.getDeclaredMethod("tick"),
			getClass().getName() + ".onWorldClientTick(this);"
		);
		
		/* TODO
		compiler.replaceVirtualCall(
			c.getMethod("doVoidFogParticles", "(III)V"),
			getClass().getName(),
			"onRandomInt",
			new MethodEntry(new ClassEntry(Random.class.getName()), "nextInt", new Signature("(I)I"))
		);
		*/
	}
	
	public static ClientChunkCache getChunkCache(WorldClient worldClient) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			return chunkSystem.getClientChunkCache(worldClient);
		}
		return null;
	}
	
	public static void onWorldClientTick(WorldClient worldClient) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			chunkSystem.onWorldClientTick(worldClient);
		}
	}
	
	/* TODO
	public static int onRandomInt(Random rand, int upper) {
		// default void fog range is [0,8)
		// only change something if we match this upper value
		if (upper == 8) {
			// use the new void fog range
			Integer min = VoidFogRangeEvent.getMin();
			Integer max = VoidFogRangeEvent.getMax();
			if (min != null && max != null) {
				return Util.randRange(rand, min, max);
			}
		}
		
		return rand.nextInt(upper);
		return 0;
	}
	*/
}
