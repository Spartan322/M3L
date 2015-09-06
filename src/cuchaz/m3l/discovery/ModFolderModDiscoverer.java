/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import net.minecraftforge.fml.common.Mod;

import com.google.common.collect.Lists;

import cuchaz.enigma.analysis.JarClassIterator;

public class ModFolderModDiscoverer implements ModDiscoverer {
	
	private static final FilenameFilter m_zipFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}
	};
	
	@Override
	public Iterable<ModDiscovery> findMods() {
		
		List<ModDiscovery> mods = Lists.newArrayList();
		
		ModDiscoveries.log.info("Looking for mods in the mods folder...");
		
		// TODO: fancy version-based or tag-based mod discovery
		// probably with different filters? dunno yet
		
		List<File> files = Lists.newArrayList();
		File dirMods = new File("mods");
		if (dirMods.exists() && dirMods.isDirectory()) {
			for (File file : dirMods.listFiles(m_zipFilter)) {
				files.add(file);
			}
		}
		
		for (File file : files) {
			ModDiscoveries.log.info("Checking jar file: {}", file.getAbsolutePath());
			try (JarFile jarFile = new JarFile(file)) {
				for (CtClass c : JarClassIterator.classes(jarFile)) {
			
					// look for the annotation
					AnnotationsAttribute attributeInfo = (AnnotationsAttribute)c.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
					if (attributeInfo == null) {
						continue;
					}
					Annotation annotation = attributeInfo.getAnnotation(Mod.class.getName());
					if (annotation == null) {
						continue;
					}
					
					MemberValue modIdMember = annotation.getMemberValue("modid");
					if (modIdMember instanceof StringMemberValue) {
						String modId = ((StringMemberValue)modIdMember).getValue();
						mods.add(new ModDiscovery(modId, c.getName(), file));
						ModDiscoveries.log.info("Found mod: {}", modId);
					} else {
						ModDiscoveries.log.warn("Mod annotation for Class {} is incorrect or corrupt", c.getName());
					}
				}
				
			} catch (IOException ex) {
				ModDiscoveries.log.warn("Unable to check jar file", ex);
				continue;
			}
		}
		
		return mods;
	}
}
