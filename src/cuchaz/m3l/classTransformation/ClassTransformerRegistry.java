/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import java.util.ArrayList;
import java.util.List;

import cuchaz.m3l.classTransformation.transformers.BlockDoorTransformer;
import cuchaz.m3l.classTransformation.transformers.ChunkCacheTransformer;
import cuchaz.m3l.classTransformation.transformers.ChunkSectionRenderersTransformer;
import cuchaz.m3l.classTransformation.transformers.ClientMainTransformer;
import cuchaz.m3l.classTransformation.transformers.DimensionTransformer;
import cuchaz.m3l.classTransformation.transformers.EntityPlayerMPTransformer;
import cuchaz.m3l.classTransformation.transformers.EntityTransformer;
import cuchaz.m3l.classTransformation.transformers.IntegratedServerTransformer;
import cuchaz.m3l.classTransformation.transformers.MainMenuTransformer;
import cuchaz.m3l.classTransformation.transformers.MinecraftServerTransformer;
import cuchaz.m3l.classTransformation.transformers.MinecraftTransformer;
import cuchaz.m3l.classTransformation.transformers.MobSpawnerTransformer;
import cuchaz.m3l.classTransformation.transformers.NetHandlerPlayServerTransformer;
import cuchaz.m3l.classTransformation.transformers.OverriddenBlockStatesChunkCacheTransformer;
import cuchaz.m3l.classTransformation.transformers.WorldClientTransformer;
import cuchaz.m3l.classTransformation.transformers.WorldRendererTransformer;
import cuchaz.m3l.classTransformation.transformers.WorldServerTransformer;
import cuchaz.m3l.classTransformation.transformers.WorldTransformer;

public class ClassTransformerRegistry {
	private static List<ClassTransformer> m_transformers;
	
	static {
		m_transformers = new ArrayList<ClassTransformer>();
		m_transformers.add(new ClientMainTransformer());
		m_transformers.add(new MinecraftServerTransformer());
		m_transformers.add(new IntegratedServerTransformer());
		m_transformers.add(new MainMenuTransformer());
		m_transformers.add(new WorldServerTransformer());
		m_transformers.add(new WorldClientTransformer());
		m_transformers.add(new DimensionTransformer());
		m_transformers.add(new EntityPlayerMPTransformer());
		m_transformers.add(new MobSpawnerTransformer());
		m_transformers.add(new WorldTransformer());
		m_transformers.add(new EntityTransformer());
		m_transformers.add(new ChunkCacheTransformer());
		m_transformers.add(new NetHandlerPlayServerTransformer());
		m_transformers.add(new WorldRendererTransformer());
		m_transformers.add(new ChunkSectionRenderersTransformer());
		m_transformers.add(new OverriddenBlockStatesChunkCacheTransformer());
		m_transformers.add(new MinecraftTransformer());
		m_transformers.add(new BlockDoorTransformer());
		
		/* TODO: get old transformers working
		m_transformers.add(new BlockTransformer());
		m_transformers.add(new ItemTransformer());
		m_transformers.add(new BiomeGenBaseTransformer());
		m_transformers.add(new ItemBlockTransformer());
		*/
	}
	
	public static List<ClassTransformer> getTransformers() {
		return new ArrayList<ClassTransformer>(m_transformers);
	}
}
