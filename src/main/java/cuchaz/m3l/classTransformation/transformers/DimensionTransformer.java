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
import javassist.NotFoundException;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;

import java.util.Optional;


public class DimensionTransformer implements ClassTransformer {

    public static Optional<BiomeManager> getBiomeManager(World world) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.getBiomeManager(world));
        }
        return Optional.empty();
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(Dimension.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        compiler.insertBeforeBehavior(
                c.getDeclaredMethod("registerBiomeManager"),
                BiomeManager.class.getName() + " override = " + getClass().getName() + ".getBiomeManager(this.world);"
                        + "if (override != null) { return override; }"
        );
    }
}
