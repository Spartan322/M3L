/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.lib;

import cuchaz.enigma.analysis.TranslationIndex;
import cuchaz.enigma.mapping.MappingParseException;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.MappingsReader;
import cuchaz.m3l.classTransformation.HookLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class Constants {

    public static final String NAME = "M3L";
    public static final String VERSION = "1.8.3-0.4b";

    //Metadata related
    public static final String METADATA_FILE = NAME + "Metadata.info";
    public static final String METADATA_IDENTIFIER = NAME + "mod";

    public static final int KIB = 1024; // 1 kibibyte
    public static final int MIB = KIB * 1024; // 1 mibibyte

    public static final String MINECRAFT_VERSION = "1.8.3";
    public static final File DIR_CONF = new File("conf");
    public static final String CONFIGURATION_EXTENSION = ".cfg";

    public static URL getResource(String path) {
        return Constants.class.getResource(path);
    }

    public static String getResourceMappings(Side side) {
        return String.format("/%s.%s.mappings", MINECRAFT_VERSION, side.name().toLowerCase());
    }

    public static String getResourceHooks(Side side) {
        return String.format("/%s.%s.hooks", MINECRAFT_VERSION, side.name().toLowerCase());
    }

    public static String getResourceObfIndex(Side side) {
        return String.format("/%s.%s.obf.index", MINECRAFT_VERSION, side.name().toLowerCase());
    }

    public static String getResourceDeobfIndex(Side side) {
        return String.format("/%s.%s.deobf.index", MINECRAFT_VERSION, side.name().toLowerCase());
    }

    public static File getConfFile(String resourcePath) {
        return new File(DIR_CONF, resourcePath);
    }

    public static Optional<Mappings> getMappings(Side side) {
        URL url = getResource(getResourceMappings(side));
        if (url == null) {
            return Optional.empty();
        }

        InputStreamReader in = null;
        try {
            in = new InputStreamReader(url.openStream());
            return Optional.of(new MappingsReader().read(in));
        } catch (IOException ex) {
            throw new Error("Couldn't read " + side + " mappings!", ex);
        } catch (MappingParseException ex) {
            throw new Error("Couldn't read " + side + " mappings!", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //Nothing to do.
                }
            }
        }
    }

    public static HookLibrary getHooks(Side side) {
        URL url = getResource(getResourceHooks(side));
        InputStream in = null;
        try {
            in = url.openStream();
            HookLibrary library = new HookLibrary();
            library.read(in);
            return library;
        } catch (IOException ex) {
            throw new Error("Couldn't read " + side + " hooks!", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //Nothing to do.
                }
            }
        }
    }

    public static Optional<TranslationIndex> getObfIndex(Side side) {
        try {
            return newTranslationIndex(Optional.of(getResource(getResourceObfIndex(side))));
        } catch (IOException ex) {
            throw new Error("Couldn't read " + side + " obfuscated index!", ex);
        }
    }

    public static Optional<TranslationIndex> getDeobfIndex(Side side) {
        try {
            return newTranslationIndex(Optional.of(getResource(getResourceDeobfIndex(side))));
        } catch (IOException ex) {
            throw new Error("Couldn't read " + side + " deobfuscated index!", ex);
        }
    }

    private static Optional<TranslationIndex> newTranslationIndex(Optional<URL> url)
            throws IOException {
        if (!url.isPresent() || url == Optional.<URL>empty()) {
            return Optional.empty();
        }

        InputStream in = null;
        try {
            in = url.get().openStream();
            TranslationIndex index = new TranslationIndex();
            index.read(in);
            return Optional.of(index);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
