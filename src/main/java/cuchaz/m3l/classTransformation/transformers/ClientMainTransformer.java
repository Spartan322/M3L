/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.M3L;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.Util;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.client.main.Main;

public class ClientMainTransformer implements ClassTransformer {

    public static void onMain(String[] args) {
        M3L.INSTANCE.initClient();
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(Main.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {
        compiler.insertBeforeBehavior(
                c.getMethod("main", "(" + Util.getClassDesc(String[].class) + ")V"),
                getClass().getName() + ".onMain($1);"
        );
    }
}
