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
import cuchaz.m3l.util.EntryFactory;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.entity.Entity;

public class EntityTransformer implements ClassTransformer {

    public static double getOutOfWorldDepth(Entity entity) {
        return BuildSizeTransformer.getBuildDepth(entity.getWorld()) - 64;
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(Entity.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        compiler.replaceDouble(
                c.getMethod("tick", "()V"),
                -64,
                EntryFactory.getClassEntry(getClass()),
                "getOutOfWorldDepth",
                "this"
        );
    }
}
