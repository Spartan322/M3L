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
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import net.minecraft.server.IntegratedServer;

public class IntegratedServerTransformer implements ClassTransformer {

    public static void onConstructed(IntegratedServer server) {
        M3L.INSTANCE.initIntegratedServer(server);
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(IntegratedServer.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        // hook server lifecycle events
        for (CtConstructor constructor : c.getDeclaredConstructors()) {
            compiler.insertAfterVoidBehavior(constructor, getClass().getName() + ".onConstructed(this);");
        }
    }
}
