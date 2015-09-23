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
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;

public class EntityPlayerMPTransformer implements ClassTransformer {

    public static void onUpdate(EntityPlayerMP player) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            chunkSystem.processChunkLoadQueue(player);
        }
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(EntityPlayerMP.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        CtMethod method = c.getMethod("update", "()V");
        compiler.insertAfterVoidBehavior(method, getClass().getName() + ".onUpdate(this);");
    }
}
