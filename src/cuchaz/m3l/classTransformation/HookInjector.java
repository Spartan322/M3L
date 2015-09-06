/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import java.util.Set;

import javassist.CtBehavior;
import javassist.CtClass;

import org.slf4j.Logger;

import com.google.common.collect.Sets;

import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.m3l.classTransformation.hooks.BehaviorHook;
import cuchaz.m3l.classTransformation.hooks.ClassHook;
import cuchaz.m3l.util.Logging;

public class HookInjector {
	
	private static final Logger log = Logging.getLogger();
	
	private HookLibrary m_library;
	private Set<ClassEntry> m_hookedClassEntries;
	
	public HookInjector(HookLibrary library) {
		m_library = library;
		
		// build a list of the hooked class names
		m_hookedClassEntries = Sets.newHashSet();
		for (ClassHook hook : m_library.classHooks()) {
			m_hookedClassEntries.add(hook.getClassEntry());
		}
		for (BehaviorHook hook : m_library.methodHooks()) {
			m_hookedClassEntries.add(hook.getBehaviorEntry().getClassEntry());
		}
	}
	
	public boolean isClassHooked(ClassEntry deobfClassEntry) {
		return m_hookedClassEntries.contains(deobfClassEntry);
	}
	
	public void transformClass(CtClass c) {
		
		// process class hooks
		for (ClassHook hook : m_library.getHooks(c)) {
			try {
				hook.applyToClass(c);
			} catch (Exception ex) {
				log.error(String.format("Cannot transform %s", c.getName()), ex);
			}
		}
		
		// process method hooks
		for (CtBehavior behavior : c.getDeclaredBehaviors()) {
			for (BehaviorHook hook : m_library.getHooks(behavior)) {
				try {
					hook.apply(behavior);
				} catch (Exception ex) {
					log.error(String.format("Cannot transform %s.%s()", c.getName(), behavior.getName()), ex);
				}
			}
		}
	}
}
