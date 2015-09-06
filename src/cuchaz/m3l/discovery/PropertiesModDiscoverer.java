/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import net.minecraftforge.fml.common.Mod;

import com.google.common.collect.Lists;

public class PropertiesModDiscoverer implements ModDiscoverer {

	@Override
	public Iterable<ModDiscovery> findMods() {
		
		List<ModDiscovery> mods = Lists.newArrayList();
		
		// init the class pool
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		
		// look for classes defined in system properties
		ModDiscoveries.log.info("Looking for mods in the system property {} ...", ModDiscoveries.DesiredModClassNamesKey);
		for (String className : ModDiscoveries.getClassNames(ModDiscoveries.DesiredModClassNamesKey)) {
			try {
				
				// look for the annotation
				CtClass modClass = classPool.get(className);
				AnnotationsAttribute attributeInfo = (AnnotationsAttribute)modClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
				if (attributeInfo == null) {
					ModDiscoveries.log.warn("Class {} does not have the Mod annotation", className);
					continue;
				}
				Annotation annotation = attributeInfo.getAnnotation(Mod.class.getName());
				if (annotation == null) {
					ModDiscoveries.log.warn("Class {} does not have the Mod annotation", className);
					continue;
				}
				
				// found a mod!
				MemberValue modIdMember = annotation.getMemberValue("modid");
				if (modIdMember instanceof StringMemberValue) {
					String modId = ((StringMemberValue)modIdMember).getValue();
					mods.add(new ModDiscovery(modId, className));
					ModDiscoveries.log.info("Found mod: {}", modId);
				} else {
					ModDiscoveries.log.warn("Mod annotation for Class {} is incorrect or corrupt", className);
				}
				
			} catch (NotFoundException ex) {
				ModDiscoveries.log.warn("Explicitly specified mod class {} was not found", className);
			}
		}
		return mods;
	}
}
