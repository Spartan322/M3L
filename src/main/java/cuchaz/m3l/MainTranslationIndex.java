/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import cuchaz.enigma.analysis.JarClassIterator;
import cuchaz.enigma.analysis.TranslationIndex;
import cuchaz.enigma.bytecode.ClassRenamer;
import cuchaz.m3l.lib.Constants;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.Logging;
import javassist.CtClass;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.JarFile;

public class MainTranslationIndex {

    private static final Logger log = Logging.getLogger();

    public static void main(String[] args) throws Exception {

        try {
            // parse the arguments
            if (args.length < 1) {
                throw new IllegalArgumentException("side is a required argument");
            }
            Side side = Side.get(args[0]);

            if (args.length < 2) {
                throw new IllegalArgumentException("pathToObfMinecraftJar is a required argument");
            }
            File jarObfMinecraft = new File(args[1]).getAbsoluteFile();
            if (!jarObfMinecraft.exists()) {
                throw new IllegalArgumentException("File not found: " + jarObfMinecraft.getPath());
            }

            if (args.length < 3) {
                throw new IllegalArgumentException("pathToDeobfMinecraftJar is a required argument");
            }
            File jarDeobfMinecraft = new File(args[2]).getAbsoluteFile();
            if (!jarDeobfMinecraft.exists()) {
                throw new IllegalArgumentException("File not found: " + jarDeobfMinecraft.getPath());
            }

            try {
                build(Constants.getConfFile(Constants.getResourceObfIndex(side)), new JarFile(jarObfMinecraft));
                build(Constants.getConfFile(Constants.getResourceDeobfIndex(side)), new JarFile(jarDeobfMinecraft));
            } catch (Throwable t) {
                t.printStackTrace(System.out);
            }

        } catch (IllegalArgumentException ex) {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("Magic Mojo Mod Loader (M3L) - v" + Constants.VERSION);
        System.out.println("Translation indexer arguments:");
        System.out.println("\tside pathToObfMinecraftJar pathToDeobfMinecraftJar");
        System.out.println("where side is \"client\" or \"server\"");
        System.out.println("pathToObfMinecraftJar is the path the obfuscated Minecraft jar");
        System.out.println("and pathToDeobfMinecraftJar is the path the deobfuscated Minecraft jar");
    }

    private static void build(File fileOut, JarFile jar)
            throws Exception {

        // build translation index
        TranslationIndex index = new TranslationIndex();
        for (CtClass c : JarClassIterator.classes(jar)) {
            ClassRenamer.moveAllClassesOutOfDefaultPackage(c, cuchaz.enigma.Constants.NonePackage);
            index.indexClass(c);
        }

        // save the library
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileOut);
            index.write(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        log.info("Wrote translation index to:\n\t" + fileOut.getAbsolutePath());
    }
}
