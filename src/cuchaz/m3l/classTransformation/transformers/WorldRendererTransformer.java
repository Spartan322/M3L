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
import net.minecraft.client.renderers.ChunkSectionRenderer;
import net.minecraft.client.renderers.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Facing;
import net.minecraft.world.chunk.Chunk;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.Util;

public class WorldRendererTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(WorldRenderer.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		// hooks for chunk section rendering
		compiler.insertBeforeBehavior(
			c.getDeclaredMethod("getChunkSectionRendererNeighbor"),
			ChunkSectionRenderer.class.getName() + " override = " + getClass().getName() + ".getChunkSectionRendererNeighbor(this, $$);"
			+ "if (override != null) { return override; }"
		);
		
		// hooks for reading entity sections
		compiler.replaceVirtualCallThenArrayAccess(
			c.getDeclaredMethod("renderEntities"),
			EntryFactory.getBehaviorEntry(Chunk.class, "getEntityStore", "()[" + Util.getClassDesc(EntitySet.class)),
			EntryFactory.getClassEntry(getClass()),
			"getEntityStore"
		);
	}
	
	public static ChunkSectionRenderer getChunkSectionRendererNeighbor(WorldRenderer worldRenderer, BlockPos pos, ChunkSectionRenderer chunkSectionRenderer, Facing facing) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			return chunkSystem.getChunkSectionRendererNeighbor(worldRenderer, pos, chunkSectionRenderer, facing);
		}
		return null;
	}
	
	public static EntitySet<Entity> getEntityStore(Chunk chunk, int chunkSectionIndex) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			EntitySet<Entity> override = chunkSystem.getEntityStore(chunk, chunkSectionIndex);
			if (override != null) {
				return override;
			}
		}
		
		// otherwise, do the default thing
		return chunk.getEntityStore()[chunkSectionIndex];
	}
}
