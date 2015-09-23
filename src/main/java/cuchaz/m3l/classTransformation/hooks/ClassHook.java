/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.EntryFactory;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.Serializable;

public abstract class ClassHook implements Serializable {

    private static final long serialVersionUID = 381176350479916612L;

    private ClassEntry m_classEntry;

    protected ClassHook(CtClass c) {
        m_classEntry = EntryFactory.getClassEntry(c);
    }

    public ClassEntry getClassEntry() {
        return m_classEntry;
    }

    public void applyToClass(CtClass c)
            throws NotFoundException, CannotCompileException {

        // make sure this hook applies to this class
        ClassEntry classEntry = EntryFactory.getClassEntry(c);
        if (!classEntry.equals(m_classEntry)) {
            throw new IllegalArgumentException(String.format("Class to transform %s does not match hooked class %s",
                    classEntry,
                    m_classEntry
            ));
        }

        onApply(c);
    }

    protected abstract void onApply(CtClass c) throws NotFoundException, CannotCompileException;
}
