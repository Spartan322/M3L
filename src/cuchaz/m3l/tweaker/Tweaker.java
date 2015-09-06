/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cuchaz.m3l.Side;
import cuchaz.m3l.discovery.ModDiscoveries;
import cuchaz.m3l.discovery.ModDiscovery;
import cuchaz.m3l.util.Arguments;

public abstract class Tweaker implements ITweaker {
	
	private String m_mainClassName;
	private String m_transformerClassName;
	private Side m_side;
	private Arguments m_args;
	
	public Tweaker(String mainClassName, String transformerclassName, Side side) {
		m_mainClassName = mainClassName;
		m_transformerClassName = transformerclassName;
		m_side = side;
		m_args = new Arguments();
	}
	
	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		
		// pass any args through
		for (int i = 0; i < args.size(); i++) {
			String arg = args.get(i);
			if (arg.startsWith("--") && i < args.size() - 1) {
				String name = arg.substring(2);
				String value = args.get(i + 1);
				m_args.set(name, value);
			}
		}
		
		// make sure these are set
		File dirGame = new File(System.getProperty("user.dir"));
		m_args.set("version", "M3L");
		m_args.set("gameDir", dirGame.getAbsolutePath());
		
		if (m_side == Side.Client) {
			m_args.set("assetsDir", new File(dirGame, "assets").getAbsolutePath());
		}
	}
	
	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		
		// NOTE: cuchaz.m3l.tweaker.* is always excluded from the classloader
		classLoader.registerTransformer(m_transformerClassName);
		
		// inject mods into the class loader
		ModDiscoveries.getInstance().discoverMods();
		for (ModDiscovery mod : ModDiscoveries.getInstance().discoveries()) {
			mod.injectIntoClassLoader(classLoader);
		}
	}
	
	@Override
	public String[] getLaunchArguments() {
		return m_args.build();
	}
	
	@Override
	public String getLaunchTarget() {
		return m_mainClassName;
	}
}
