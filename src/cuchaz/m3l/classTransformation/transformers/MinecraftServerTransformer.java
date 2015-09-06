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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.Util;

public class MinecraftServerTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(MinecraftServer.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		switch (side) {
			case Client:
				
				compiler.insertAfterVoidBehavior(
					c.getMethod("initialLevelChunkLoad", "()V"),
					getClass().getName() + ".generateWorlds(this);"
				);
				
				compiler.insertAfterVoidBehavior(
					c.getDeclaredMethod("stopServer"),
					getClass().getName() + ".stopServer();"
				);
				
				/* TODO: re-enable these later
				// set the save hook
				compiler.insertAfterVoidBehavior(c.getMethod("saveAllWorlds", "(Z)V"), getClass().getName() + ".afterSaveWorlds( worldServers[0].getSaveHandler() );");
				
				// set the load hook
				compiler.insertAfterVoidBehavior(c.getMethod("convertMapIfNeeded", String.format("(%s)V", Util.getClassDesc(String.class))), getClass().getName()
						+ ".beforeLoadWorlds( getActiveAnvilConverter().getSaveLoader( $1, false ) );");
				*/
			
			break;
			
			case Server:
				compiler.insertBeforeBehavior(
					c.getMethod("main", "(" + Util.getClassDesc(String[].class) + ")V"),
					getClass().getName() + ".onMain(this);"
				);
			break;
		}
	}
	
	public static void onMain(MinecraftServer server) {
		M3L.instance.initDedicatedServer(server);
	}
	
	public static void generateWorlds(MinecraftServer server) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			for (WorldServer worldServer : server.worlds) {
				chunkSystem.generateWorld(worldServer);
			}
		}
	}
	
	public static void stopServer() {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			chunkSystem.onServerStop();
		}
	}
	
	/* TODO: implement id mappings
	public static void afterSaveWorlds(ISaveHandler saveHandler) {
		saveMappings(saveHandler, Block.BLOCK_REGISTRY, "blocks");
		saveMappings(saveHandler, Item.REGISTRY_COMPLEX, "items");
	}
	
	public static void beforeLoadWorlds(ISaveHandler saveHandler) {
		updateNumbersIfChanged(saveHandler, Block.BLOCK_REGISTRY, "blocks");
		updateNumbersIfChanged(saveHandler, Item.REGISTRY_COMPLEX, "items");
	}
	
	private static void saveMappings(ISaveHandler saveHandler, RegistryNamespaced registry, String label) {
		// collect the mappings
		IdNumberMappings mappings = new IdNumberMappings();
		mappings.loadFromRegistry(registry);
		
		// save them to a file
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(saveHandler.getLevelDirectoryName(), label + ".mappings"));
			mappings.write(out);
			Mods.log.info("Saved " + label + " mappings.");
		} catch (IOException ex) {
			Mods.log.error("Unable to save " + label + " mappings!", ex);
		} finally {
			Util.closeQuietly(out);
		}
	}
	
	private static IdNumberMappings loadMappings(ISaveHandler saveHandler, String label) {
		// read the mappings
		IdNumberMappings mappings = new IdNumberMappings();
		InputStream in = null;
		try {
			File file = new File(saveHandler.getLevelDirectoryName(), label + ".mappings");
			if (!file.exists()) {
				return null;
			}
			in = new FileInputStream(file);
			mappings.read(in);
			
			Mods.log.info("Read " + label + " mappings.");
			return mappings;
		} catch (IOException ex) {
			Mods.log.error("Unable to read " + label + " mappings!", ex);
			return null;
		} finally {
			Util.closeQuietly(in);
		}
	}
	
	private static void updateNumbersIfChanged(ISaveHandler saveHandler, RegistryNamespaced registry, String label) {
		// get the saved mappings, if any
		IdNumberMappings mappingsSaved = loadMappings(saveHandler, label);
		if (mappingsSaved == null) {
			return;
		}
		
		// get current mappings
		IdNumberMappings mappingsCurrent = new IdNumberMappings();
		mappingsCurrent.loadFromRegistry(registry);
		
		// are the mappings different?
		if (!mappingsSaved.equals(mappingsCurrent)) {
			Mods.log.info("Detected changes in " + label + " numbers. Restoring saved state.");
			mappingsSaved.restoreRegistry(registry);
		}
	}
	*/
}
