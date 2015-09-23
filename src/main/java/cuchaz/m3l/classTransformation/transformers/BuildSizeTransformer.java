/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.classTransformation.transformers;

import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.m3l.M3L;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.EntryFactory;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.world.World;


public class BuildSizeTransformer {

    public static ClassEntry m_thisClass = EntryFactory.getClassEntry(BuildSizeTransformer.class);

    public static void setBuildHeight(HookCompiler compiler, CtBehavior behavior, String args)
            throws NotFoundException, CannotCompileException {
        compiler.replaceInt(behavior, 256, m_thisClass, "getBuildHeight", args);
    }

    public static void setMaxBlockY(HookCompiler compiler, CtBehavior behavior, String args)
            throws NotFoundException, CannotCompileException {
        compiler.replaceInt(behavior, 255, m_thisClass, "getMaxBlockY", args);
    }

    public static void setBuildDepthGt(HookCompiler compiler, CtMethod method, String args)
            throws NotFoundException, CannotCompileException {
        compiler.replaceGtZero(method, m_thisClass, "getBuildDepth", args);
    }

    public static void setBuildDepthLt(HookCompiler compiler, CtMethod method, String args)
            throws NotFoundException, CannotCompileException {
        compiler.replaceLtZero(method, m_thisClass, "getBuildDepth", args);
    }

    public static int getMaxBlockY(World world) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            Integer result = chunkSystem.getMaxBlockY(world);
            if (result != null) {
                return result;
            }
        }
        return 255;
    }

    public static int getBuildHeight(World world) {
        return getMaxBlockY(world) + 1;
    }

    public static int getBuildDepth(World world) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            Integer result = chunkSystem.getMinBlockY(world);
            if (result != null) {
                return result;
            }
        }
        return 0;
    }
}
