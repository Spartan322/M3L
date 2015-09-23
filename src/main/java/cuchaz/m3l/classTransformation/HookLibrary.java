/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.EntryFactory;
import cuchaz.m3l.classTransformation.hooks.BehaviorHook;
import cuchaz.m3l.classTransformation.hooks.ClassHook;
import javassist.CtBehavior;
import javassist.CtClass;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HookLibrary {

    private ListMultimap<ClassEntry, ClassHook> m_classHooks;
    private ListMultimap<BehaviorEntry, BehaviorHook> m_behaviorHooks;

    public HookLibrary() {
        m_classHooks = ArrayListMultimap.create();
        m_behaviorHooks = ArrayListMultimap.create();
    }

    public void addHook(ClassHook hook) {
        m_classHooks.get(hook.getClassEntry()).add(hook);
    }

    public void addHook(BehaviorHook hook) {
        m_behaviorHooks.get(hook.getBehaviorEntry()).add(hook);
    }

    public List<ClassHook> getHooks(CtClass c) {
        return m_classHooks.get(EntryFactory.getClassEntry(c));
    }

    public List<BehaviorHook> getHooks(CtBehavior behavior) {
        return m_behaviorHooks.get(EntryFactory.getBehaviorEntry(behavior));
    }

    public Iterable<ClassHook> classHooks() {
        return m_classHooks.values();
    }

    public Iterable<BehaviorHook> methodHooks() {
        return m_behaviorHooks.values();
    }

    public void write(OutputStream out)
            throws IOException {
        GZIPOutputStream gzipout = new GZIPOutputStream(out);
        ObjectOutputStream oout = new ObjectOutputStream(gzipout);
        oout.writeObject(m_classHooks);
        oout.writeObject(m_behaviorHooks);
        gzipout.finish();
    }

    @SuppressWarnings("unchecked")
    public void read(InputStream in)
            throws IOException {
        try {
            ObjectInputStream oin = new ObjectInputStream(new GZIPInputStream(in));
            m_classHooks = (ListMultimap<ClassEntry, ClassHook>) oin.readObject();
            m_behaviorHooks = (ListMultimap<BehaviorEntry, BehaviorHook>) oin.readObject();
        } catch (ClassNotFoundException ex) {
            throw new Error(ex);
        }
    }
}
