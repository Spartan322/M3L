/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import java.util.HashSet;
import java.util.Set;

public enum ClassType {
	
	Excluded(false, false, false,
		"java",
		"javax",
		"sun",
		"com.mojang",
		"org.apache",
		"com.google",
		"javassist",
		"org.reflections",
		"gnu.trove",
		"io.netty",
		"joptsimple",
		"paulscode.sound",
		"com.jcraft"
	),
	Minecraft(true, true, false,
		"net.minecraft"
	),
	ModLoader(true, false, true,
		"cuchaz.m3l"
	),
	Other(true, false, true
		// everything else
	);
	
	private boolean m_shouldTranslate;
	private boolean m_shouldHook;
	private boolean m_shouldFilter;
	private Set<String> m_packages;
	
	private ClassType(boolean shouldTranslate, boolean shouldHook, boolean shouldFilter, String... packages) {
		m_shouldTranslate = shouldTranslate;
		m_shouldHook = shouldHook;
		m_shouldFilter = shouldFilter;
		
		// build a fast lookup structure for the packages
		m_packages = new HashSet<String>();
		for (String thePackage : packages) {
			m_packages.add(thePackage);
		}
	}
	
	public boolean shouldTranslate() {
		return m_shouldTranslate;
	}
	
	public boolean shouldHook() {
		return m_shouldHook;
	}
	
	public boolean shouldFilter() {
		return m_shouldFilter;
	}
	
	public Iterable<String> getPackages() {
		return m_packages;
	}
	
	public boolean includes(String nameJava) {
		String thePackage = nameJava;
		// check each package in the class name for a match
		while ( (thePackage = getPackage(thePackage)) != null) {
			if (m_packages.contains(thePackage)) {
				return true;
			}
		}
		return false;
	}
	
	public static ClassType get(String name) {
		for (ClassType type : values()) {
			if (type.includes(name)) {
				return type;
			}
		}
		
		// by default, assume its a mod class, since we can't know the mod packages
		// you might think we could get them from mod zip files, but that won't work for classpath mods
		return Other;
	}
	
	private static String getPackage(String nameJava) {
		int index = nameJava.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		return nameJava.substring(0, index);
	}
}
