/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api.chunks;

import java.util.Random;

import net.minecraft.client.renderers.ChunkSectionRenderer;
import net.minecraft.client.renderers.ChunkSectionRenderers;
import net.minecraft.client.renderers.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Facing;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldClient;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ClientChunkCache;
import net.minecraft.world.gen.ServerChunkCache;


public interface ChunkSystem {
	
	// TODO: deal with hiding client things from the dedicated server
	
	ServerChunkCache getServerChunkCache(WorldServer worldServer);
	ClientChunkCache getClientChunkCache(WorldClient worldClient);
	BiomeManager getBiomeManager(World world);
	Integer getMinBlockY(World world);
	Integer getMaxBlockY(World world);
	PlayerManager getPlayerManager(WorldServer worldServer);
	void processChunkLoadQueue(EntityPlayerMP player);
	void onWorldClientTick(WorldClient worldClient);
	void onWorldServerTick(WorldServer worldServer);
	Integer getRandomBlockYForMobSpawnAttempt(Random rand, int upper, World world, int cubeX, int cubeZ);
	void generateWorld(WorldServer worldServer);
	boolean calculateSpawn(WorldServer worldServer, WorldSettings settings);
	Integer getSeaLevel(World world);
	Double getHorizonLevel(World world);
	Boolean updateLightingAt(World world, LightType type, BlockPos pos);
	Boolean checkBlockRangeIsInWorld(World world, int minBlockX, int minBlockY, int minBlockZ, int maxBlockX, int maxBlockY, int maxBlockZ, boolean allowEmptyChunks);
	Boolean checkEntityIsInWorld(World world, Entity entity, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, boolean allowEmptyChunks);
	boolean setChunkSectionRendererPositions(ChunkSectionRenderers renderers);
	ChunkSectionRenderer getChunkSectionRenderer(ChunkSectionRenderers renderers, BlockPos pos);
	boolean initChunkSectionRendererCounts(ChunkSectionRenderers renderers, int viewDistance);
	ChunkSectionRenderer getChunkSectionRendererNeighbor(WorldRenderer worldRenderer, BlockPos pos, ChunkSectionRenderer chunkSectionRenderer, Facing facing);
	EntitySet<Entity> getEntityStore(Chunk chunk, int chunkSectionIndex);
	void onServerStop();
	void unloadClientWorld();
}
