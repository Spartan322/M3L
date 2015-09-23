/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import cuchaz.enigma.analysis.TranslationIndex;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.m3l.M3L;
import javassist.*;
import javassist.bytecode.Descriptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class PseudoDeobfClassPath implements ClassPath {

    private TranslationIndex m_translationIndex;

    public PseudoDeobfClassPath(TranslationIndex translationIndex) {
        m_translationIndex = translationIndex;
    }

    @Override
    public InputStream openClassfile(String classname)
            throws NotFoundException {

        // look for the class and its superclass
        ClassEntry classEntry = new ClassEntry(Descriptor.toJvmName(classname));
        ClassEntry superclassEntry = m_translationIndex.getSuperclass(classEntry);
        if (superclassEntry == null) {
            throw new NotFoundException(classname);
        }
        String superclassName = Descriptor.toJavaName(superclassEntry.toString());

        // dynamically generate a class that has the superclass info
        ClassPool classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(getClass().getClassLoader()));
        CtClass c = classPool.makeClass(
                classname,
                isJre(superclassEntry) ? classPool.get(superclassName) : classPool.makeClass(superclassName)
        );

        try {
            return new ByteArrayInputStream(c.toBytecode());
        } catch (IOException ex) {
            M3L.LOGGER.error("Can't generate class/superclass pair", ex);
            throw new NotFoundException(classname);
        } catch (CannotCompileException ex) {
            M3L.LOGGER.error("Can't generate class/superclass pair", ex);
            throw new NotFoundException(classname);
        }
    }

    @Override
    public URL find(String classname) {

        // look for the class and its superclass
        ClassEntry classEntry = new ClassEntry(Descriptor.toJvmName(classname));
        ClassEntry superclassEntry = m_translationIndex.getSuperclass(classEntry);
        if (superclassEntry == null) {
            return null;
        }

        try {
            // this url is actually ignored by javassist
            // we could return any non-null value and it wouldn't matter
            return new URL(String.format("file:///%s/%s", classEntry.toString(), superclassEntry.toString()));
        } catch (MalformedURLException ex) {
            M3L.LOGGER.error("Apparently our custom url is malformed... who knew.", ex);
            return null;
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    private boolean isJre(ClassEntry classEntry) {
        String packageName = classEntry.getPackageName();
        return packageName != null && (packageName.startsWith("java") || packageName.startsWith("javax"));
    }
}
