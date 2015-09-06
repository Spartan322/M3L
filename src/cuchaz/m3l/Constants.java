/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import cuchaz.enigma.analysis.TranslationIndex;
import cuchaz.enigma.mapping.MappingParseException;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.MappingsReader;
import cuchaz.m3l.classTransformation.HookLibrary;

public class Constants {
	
	public static final String Name = "M3L";
	public static final String Version = "1.8.3-0.3b";
	
	public static final int KiB = 1024; // 1 kibibyte
	public static final int MiB = KiB * 1024; // 1 mibibyte
	
	public static final String MinecraftVersion = "1.8.3";
	public static final File DirConf = new File("conf");
	
	public static URL getResource(String path) {
		return Constants.class.getResource(path);
	}
	
	public static String getResourceMappings(Side side) {
		return String.format("/%s.%s.mappings", MinecraftVersion, side.name().toLowerCase());
	}
	
	public static String getResourceHooks(Side side) {
		return String.format("/%s.%s.hooks", MinecraftVersion, side.name().toLowerCase());
	}
	
	public static String getResourceObfIndex(Side side) {
		return String.format("/%s.%s.obf.index", MinecraftVersion, side.name().toLowerCase());
	}
	
	public static String getResourceDeobfIndex(Side side) {
		return String.format("/%s.%s.deobf.index", MinecraftVersion, side.name().toLowerCase());
	}
	
	public static File getConfFile(String resourcePath) {
		return new File(DirConf, resourcePath);
	}
	
	public static Mappings getMappings(Side side) {
		URL url = getResource(getResourceMappings(side));
		if (url == null) {
			return null;
		}
		try (Reader in = new InputStreamReader(url.openStream())) {
			return new MappingsReader().read(in);
		} catch (IOException | MappingParseException ex) {
			throw new Error("Couldn't read " + side + " mappings!", ex);
		}
	}
	
	public static HookLibrary getHooks(Side side) {
		URL url = getResource(getResourceHooks(side));
		try (InputStream in = url.openStream()) {
			HookLibrary library = new HookLibrary();
			library.read(in);
			return library;
		} catch (IOException ex) {
			throw new Error("Couldn't read " + side + " hooks!", ex);
		}
	}

	public static TranslationIndex getObfIndex(Side side) {
		try {
			return newTranslationIndex(getResource(getResourceObfIndex(side)));
		} catch (IOException ex) {
			throw new Error("Couldn't read " + side + " obfuscated index!", ex);
		}
	}

	public static TranslationIndex getDeobfIndex(Side side) {
		try {
			return newTranslationIndex(getResource(getResourceDeobfIndex(side)));
		} catch (IOException ex) {
			throw new Error("Couldn't read " + side + " deobfuscated index!", ex);
		}
	}
	
	private static TranslationIndex newTranslationIndex(URL url)
	throws IOException {
		if (url == null) {
			return null;
		}
		try (InputStream in = url.openStream()) {
			TranslationIndex index = new TranslationIndex();
			index.read(in);
			return index;
		}
	}
}
