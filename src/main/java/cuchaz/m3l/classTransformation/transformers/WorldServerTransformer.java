/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.M3L;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.ServerChunkCache;

import java.util.Optional;

public class WorldServerTransformer implements ClassTransformer {

    public static Optional<ServerChunkCache> getChunkCache(WorldServer worldServer) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.getServerChunkCache(worldServer));
        }
        return Optional.empty();
    }

    public static Optional<PlayerManager> getPlayerManager(WorldServer worldServer) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.getPlayerManager(worldServer));
        }
        return Optional.empty();
    }

    public static void onWorldServerTick(WorldServer worldServer) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            chunkSystem.onWorldServerTick(worldServer);
        }
    }

    public static boolean calculateSpawn(WorldServer worldServer, WorldSettings settings) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        return chunkSystem != null && chunkSystem.calculateSpawn(worldServer, settings);
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(WorldServer.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        // override the server chunk cache
        compiler.insertBeforeBehavior(
                c.getDeclaredMethod("createChunkCache"),
                ServerChunkCache.class.getName() + " override = " + getClass().getName() + ".getChunkCache(this);"
                        + "if (override != null) { this.serverChunkCache = override; return override; }"
        );

        // override the player manager
        for (CtConstructor constructor : c.getDeclaredConstructors()) {
            compiler.insertAfterVoidBehavior(
                    constructor,
                    PlayerManager.class.getName() + " override = " + getClass().getName() + ".getPlayerManager(this);"
                            + "if (override != null) { this.playerManager = override; }"
            );
        }

        // hook the tick method
        compiler.insertAfterVoidBehavior(
                c.getDeclaredMethod("tick"),
                getClass().getName() + ".onWorldServerTick(this);"
        );

        // hook into spawn point calculation
        compiler.insertBeforeBehavior(
                c.getDeclaredMethod("initializeSpawn"),
                "boolean calculated = " + getClass().getName() + ".calculateSpawn(this, $1);"
                        + "if (calculated) { return; }"
        );
    }
}
