/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class DuckTyper {

    public void removeCheckCasts(CtClass c) {
        for (CtBehavior behavior : c.getDeclaredBehaviors()) {
            try {
                if (behavior.getAnnotation(RemoveCheckCasts.class) != null) {
                    MethodInfo info = behavior.getMethodInfo();
                    CodeAttribute attribute = info.getCodeAttribute();
                    if (attribute != null) {
                        removeCheckCasts(attribute);
                    }
                }
            } catch (ClassNotFoundException ex) {
                // I have no idea how this can even happen...
                throw new Error(ex);
            }
        }
    }

    private void removeCheckCasts(CodeAttribute attribute) {
        CodeIterator i = attribute.iterator();
        try {
            while (i.hasNext()) {
                int pos = i.next();
                int op = i.byteAt(pos);
                if (op == Opcode.CHECKCAST) {
                    i.writeByte(Opcode.NOP, pos + 0);
                    i.writeByte(Opcode.NOP, pos + 1);
                    i.writeByte(Opcode.NOP, pos + 2);
                }
            }
        } catch (BadBytecode ex) {
            throw new Error("Can't filter out checkcasts", ex);
        }
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.METHOD)
    public @interface RemoveCheckCasts {
    }
}
