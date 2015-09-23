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
import cuchaz.m3l.util.Util;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.main.Minecraft;
import net.minecraft.world.WorldClient;

import java.util.List;

public class MinecraftTransformer implements ClassTransformer {

    public static void onAddDefaultResourcePacks(List<IResourcePack> resourcePacks) {
        /* TODO: implement resource packs
		Mods.getInstance().createModResourcePacks(resourcePacks);
		*/
    }

    public static void setWorld(WorldClient worldClient) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            if (worldClient == null) {
                chunkSystem.unloadClientWorld();
            }
        }
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(Minecraft.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

		/* TODO: re-enable resource packs later
        compiler.insertAfterVoidBehavior(
			c.getMethod("addDefaultResourcePack", "()V"),
			getClass().getName() + ".onAddDefaultResourcePacks(defaultResourcePacks);"
		);
		*/

        compiler.insertBeforeBehavior(
                c.getMethod("setWorld", "(" + Util.getClassDesc(WorldClient.class) + Util.getClassDesc(String.class) + ")V"),
                getClass().getName() + ".setWorld($1);"
        );
    }
}
