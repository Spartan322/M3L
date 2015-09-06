/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public enum OperatingSystem {
	Windows {
		@Override
		public File getMinecraftDir() {
			// get the user's home directory
			File dirHome = new File(System.getProperty("user.home"));
			
			// let app data override the home dir if it exists
			String pathAppData = System.getenv("APPDATA");
			if (pathAppData != null) {
				dirHome = new File(pathAppData);
			}
			
			return new File(dirHome, ".minecraft");
		}
		
		@Override
		public boolean isNative(String filename) {
			return filename.endsWith(".dll");
		}
	},
	Osx {
		@Override
		public File getMinecraftDir() {
			return new File(new File(System.getProperty("user.home")), "Library/Application Support/minecraft");
		}
		
		@Override
		public boolean isNative(String filename) {
			return filename.endsWith(".jnilib") || filename.endsWith(".dylib");
		}
	},
	Linux {
		@Override
		public File getMinecraftDir() {
			return new File(new File(System.getProperty("user.home")), ".minecraft");
		}
		
		@Override
		public boolean isNative(String filename) {
			return filename.endsWith(".so");
		}
	};
	
	public static OperatingSystem get() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return Windows;
		} else if (SystemUtils.IS_OS_MAC) {
			return Osx;
		}
		
		// just assume linux for everything else
		return Linux;
	}
	
	public static OperatingSystem get(String name) {
		// these names are from Minecraft's json files
		name = name.toLowerCase();
		if (name.equals("windows") || name.equals("win")) {
			return Windows;
		} else if (name.equals("osx") || name.equals("mac")) {
			return Osx;
		} else if (name.equals("linux")) {
			return Linux;
		} else {
			return null;
		}
	}
	
	public abstract File getMinecraftDir();
	
	public abstract boolean isNative(String filename);
}
