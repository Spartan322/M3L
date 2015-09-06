/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import java.io.File;
import java.net.MalformedURLException;

import net.minecraft.launchwrapper.LaunchClassLoader;


public class ModDiscovery {
	
	private String m_id;
	private String m_className;
	private File m_jarFile;
	
	public ModDiscovery(String id, String className) {
		this(id, className, null);
	}
	
	public ModDiscovery(String id, String className, File jarFile) {
		m_id = id;
		m_className = className;
		m_jarFile = jarFile;
	}
	
	public String getId() {
		return m_id;
	}
	
	public String getClassName() {
		return m_className;
	}
	
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		if (m_jarFile != null) {
			try {
				classLoader.addURL(m_jarFile.toURI().toURL());
			} catch (MalformedURLException ex) {
				throw new Error(ex);
			}
		}
	}
}
