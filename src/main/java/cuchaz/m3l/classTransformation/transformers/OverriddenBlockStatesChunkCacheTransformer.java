/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.Util;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.OverriddenBlockStatesChunkCache;

public class OverriddenBlockStatesChunkCacheTransformer implements ClassTransformer {

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(OverriddenBlockStatesChunkCache.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        String blockPosType = Util.getClassDesc(BlockPos.class);
        String blockStateType = Util.getClassDesc(IBlockState.class);

        CtMethod method;
        String args = "this.world";

        method = c.getMethod("getOriginalBlockStateAt", "(" + blockPosType + ")" + blockStateType);
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setBuildDepthLt(compiler, method, args);
    }
}
