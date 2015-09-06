/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import net.minecraft.launchwrapper.Launch;
import cuchaz.m3l.util.Arguments;

public class MainLauncher {
	
	public static void main(String[] args)
	throws Throwable {
		
		try {
			
			// get the platform
			if (args.length < 1) {
				throw new IllegalArgumentException("side is a required argument");
			}
			Side side = Side.get(args[0]);
			
			// are we in dev mode?
			boolean isDev = false;
			if (args.length >= 2 && args[1].toLowerCase().equals("dev")) {
				isDev = true;
			}
			
			/* minecraft launch-time args
			
			shared
			--tweakClass
			
			client only
			--username anything
			--uuid anything
			--accessToken anything
			--userType "legacy" or "mojang"
			--userProperties "{}"
	
			minecraft tweak-time args
			
			shared
			--version "M3L"
			--gameDir "cwd"
			
			client
			--assetsDir "cwd/assets"
	
			*/
			
			Arguments arguments = new Arguments();
			arguments.set("tweakClass", String.format("cuchaz.m3l.tweaker.Tweaker%s%s",
				side.name(),
				isDev ? "Dev" : ""
			));
	
			switch (side) {
				
				case Client:
					// set client args (be anonymous for now)
					arguments.set("userProperties", "{}");
					arguments.set("accessToken", "M3Luser");
				break;
				
				case Server:
					// nothing else to do
				break;
			}
	
			// apply command-line arg overrides and launch minecraft
			arguments.set(args, 2, args.length - 1);
			System.out.println("Launching Minecraft...");
			Launch.main(arguments.build());
			
		} catch (IllegalArgumentException ex) {
			printHelp();
			return;
		}
	}
	
	private static void printHelp() {
		System.out.println("Magic Mojo Mod Loader (M3L) - v" + Constants.Version);
		System.out.println("Launch arguments:");
		System.out.println("\tside [other Minercaft args]");
		System.out.println("where side is \"client\" or \"server\"");
		System.out.println("and [other Minecraft args] can be any normal Minecraft args like --nogui");
	}
}
