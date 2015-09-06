/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import java.io.IOException;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import net.minecraft.launchwrapper.IClassTransformer;
import cuchaz.enigma.bytecode.ClassPublifier;
import cuchaz.enigma.bytecode.ClassRenamer;
import cuchaz.enigma.bytecode.ClassTranslator;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.TranslationDirection;
import cuchaz.enigma.mapping.Translator;
import cuchaz.m3l.Constants;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassFilter;
import cuchaz.m3l.classTransformation.HookInjector;

public class TweakerClassTransformer implements IClassTransformer {
	
	private boolean m_isObfuscated;
	private Mappings m_mappings;
	private Translator m_deobfuscator;
	private Translator m_obfuscator;
	private ClassTranslator m_classDeobfuscator;
	private ClassTranslator m_classObfuscator;
	private HookInjector m_injector;
	private ClassFilter m_filter;
	
	public TweakerClassTransformer(Side side, boolean isObfuscated) {
		
		m_isObfuscated = isObfuscated;
		
		try {
			// init the de/re-obofuscators
			m_mappings = Constants.getMappings(side);
			if (m_isObfuscated) {
				m_deobfuscator = m_mappings.getTranslator(TranslationDirection.Deobfuscating, Constants.getObfIndex(side));
				m_obfuscator = m_mappings.getTranslator(TranslationDirection.Obfuscating, Constants.getObfIndex(side));
				m_classDeobfuscator = new ClassTranslator(m_deobfuscator);
				m_classObfuscator = new ClassTranslator(m_obfuscator);
			}

			// init the hooks
			m_injector = new HookInjector(Constants.getHooks(side));
			
			// init the filter
			m_filter = new ClassFilter(side);
			
		} catch (Exception ex) {
			throw new Error("Unable to load class transformer!", ex);
		}
	}
	
	@Override
	public byte[] transform(String name, String sameAsName, byte[] data) {
		
		// NOTE: as far as I can tell, the two names are always the same
		
		if (data == null) {
			return null;
		}

		ClassEntry deobfClassEntry = null;
		if (m_isObfuscated) {
			// deobfuscate the class name
			ClassEntry obfClassEntry = new ClassEntry(Descriptor.toJvmName(name));
			if (obfClassEntry.isInDefaultPackage()) {
				// move to "none" package to match the mappings
				obfClassEntry = new ClassEntry(cuchaz.enigma.Constants.NonePackage + "/" + obfClassEntry.getName());
			}
			deobfClassEntry = m_deobfuscator.translateEntry(obfClassEntry);
		} else {
			deobfClassEntry = new ClassEntry(Descriptor.toJvmName(name));
		}
		
		// what kind of class are we dealing with?
		ClassType type = ClassType.get(Descriptor.toJavaName(deobfClassEntry.getClassName()));
		if (!type.shouldFilter() && !type.shouldHook() && !type.shouldTranslate()) {
			return data;
		}
		
		try {
			
			// get a javassist handle for the class
			ClassPool classPool = new ClassPool();
			classPool.insertClassPath(new PseudoDeobfClassPath(m_obfuscator.getTranslationIndex()));
			classPool.insertClassPath(new LoaderClassPath(getClass().getClassLoader()));
			classPool.insertClassPath(new ByteArrayClassPath(name, data));
			CtClass c = classPool.get(name);
			
			if (type.shouldFilter()) {
				m_filter.filter(c);
			}
			
			if (m_isObfuscated) {
				ClassRenamer.moveAllClassesOutOfDefaultPackage(c, cuchaz.enigma.Constants.NonePackage);
				if (type.shouldTranslate()) {
					m_classDeobfuscator.translate(c);
				}
			}
			
			if (type.shouldHook()) {
				ClassPublifier.publify(c);
				if (m_injector.isClassHooked(deobfClassEntry)) {
					m_injector.transformClass(c);
				}
			} else {
				// update the translation index with the deobf'd class we've never seen before
				// so we can get the obf step right when the class extends a Minecraft class
				m_obfuscator.getTranslationIndex().indexClass(c, false);
			}
			
			if (m_isObfuscated) {
				if (type.shouldTranslate()) {
					m_classObfuscator.translate(c);
				}
				ClassRenamer.moveAllClassesIntoDefaultPackage(c, cuchaz.enigma.Constants.NonePackage);
			}
			
			data = c.toBytecode();
			
		} catch (NotFoundException | CannotCompileException | IOException ex) {
			// these are serious problems
			throw new Error(ex);
		}
		
		return data;
	}
}
