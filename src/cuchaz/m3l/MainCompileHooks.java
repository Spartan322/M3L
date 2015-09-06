/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import org.slf4j.Logger;

import cuchaz.enigma.analysis.JarClassIterator;
import cuchaz.enigma.bytecode.ClassRenamer;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.ClassTransformerRegistry;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.classTransformation.HookLibrary;
import cuchaz.m3l.classTransformation.hooks.BehaviorHook;
import cuchaz.m3l.classTransformation.hooks.ClassHook;
import cuchaz.m3l.util.Logging;

public class MainCompileHooks {
	
	private static final Logger log = Logging.getLogger();
	
	public static void main(String[] args) throws Exception {
		
		try {
			// parse the arguments
			if (args.length < 1) {
				throw new IllegalArgumentException("side is a required argument");
			}
			Side side = Side.get(args[0]);
			
			if (args.length < 2) {
				throw new IllegalArgumentException("pathToDeobfMinecraftJar is a required argument");
			}
			File jarMinecraft = new File(args[1]).getAbsoluteFile();
			if (!jarMinecraft.exists()) {
				throw new IllegalArgumentException("File not found: " + jarMinecraft.getPath());
			}
			
			try {
				compile(Constants.getConfFile(Constants.getResourceHooks(side)), new JarFile(jarMinecraft), side);
			} catch (Throwable t) {
				t.printStackTrace(System.out);
			}
			
		} catch (IllegalArgumentException ex) {
			printHelp();
		}
	}
	
	private static void printHelp() {
		System.out.println("Magic Mojo Mod Loader (M3L) - v" + Constants.Version);
		System.out.println("Hook compiler arguments:");
		System.out.println("\tside pathToDeobfMinecraftJar");
		System.out.println("where side is \"client\" or \"server\"");
		System.out.println("and pathToDeobfMinecraftJar is the path to either the deobfuscated client or server jar");
	}
	
	private static void compile(File fileOut, JarFile jar, Side side)
	throws Exception {
		
		// init javassist
		final ClassPool classPool = new ClassPool();
		classPool.insertClassPath(new LoaderClassPath(MainCompileHooks.class.getClassLoader()));
		
		// for each minecraft class...
		log.info("Scanning Minecraft classes...");
		int numClassesScanned = 0;
		HookCompiler precompiler = new HookCompiler();
		for (CtClass c : JarClassIterator.classes(jar)) {
			ClassRenamer.moveAllClassesOutOfDefaultPackage(c, cuchaz.enigma.Constants.NonePackage);
			
			// precompile all the hooks
			for (ClassTransformer transformer : ClassTransformerRegistry.getTransformers()) {
				if (transformer.meetsRequirements(c)) {
					log.info("Precompiling hooks for " + transformer.getClass().getSimpleName());
					transformer.compile(precompiler, c, side);
				}
			}
			
			numClassesScanned++;
		}
		log.info("Precompiled " + precompiler.getNumHooks() + " hooks from " + numClassesScanned + " minecraft classes!");
		
		// build the library of hooks
		HookLibrary library = new HookLibrary();
		for (ClassHook hook : precompiler.classHooks()) {
			library.addHook(hook);
		}
		for (BehaviorHook hook : precompiler.behaviorHooks()) {
			library.addHook(hook);
		}
		
		// save the library
		try (FileOutputStream out = new FileOutputStream(fileOut)) {
			library.write(out);
		}
		log.info("Wrote precompiled hooks to:\n\t" + fileOut.getAbsolutePath());
	}
}
