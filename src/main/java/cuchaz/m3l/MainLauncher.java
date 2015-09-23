/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import cuchaz.m3l.lib.Constants;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.ArgumentBuilder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.launchwrapper.Launch;

import java.util.List;

public class MainLauncher {

    public static void main(String... args)
            throws Throwable {

        try {
            OptionParser optionParser = new OptionParser();
            optionParser.allowsUnrecognizedOptions();

            final OptionSpec<String> argSide = optionParser.accepts("side").withRequiredArg().ofType(String.class);
            final OptionSpec<Boolean> argDevelopment = optionParser.accepts("dev").withOptionalArg().ofType(Boolean.class).defaultsTo(false);
            final OptionSpec<String> options = optionParser.nonOptions();

            final OptionSet argOptions = optionParser.parse(args);
            final List<String> minecraftArguments = argOptions.valuesOf(options);

            // what side are we using?
            Side side = Side.get(argOptions.valueOf("side").toString());
            // are we in dev mode?
            boolean isDev = (Boolean) argOptions.valueOf("dev");

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

            ArgumentBuilder argumentBuilder = new ArgumentBuilder();
            argumentBuilder.add("--tweakClass", String.format("cuchaz.m3l.tweaker.Tweaker%s%s", side.name(), isDev ? "Dev" : ""));

            switch (side) {
                case Client:
                    // set client args (be anonymous for now)
                    argumentBuilder.add("--userProperties", "{}");
                    argumentBuilder.add("--accessToken", "M3Luser");
                    break;

                case Server:
                default:
                    // nothing else to do
                    break;
            }

            argumentBuilder.add(minecraftArguments.toArray(new String[minecraftArguments.size()]));
            System.out.println("Launching Minecraft...");
            Launch.main(argumentBuilder.build());

        } catch (IllegalArgumentException ex) {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("Magic Mojo Mod Loader (M3L) - v" + Constants.VERSION);
        System.out.println("Launch arguments:");
        System.out.println("\tside [other Minercaft args]");
        System.out.println("where side is \"client\" or \"server\"");
        System.out.println("and [other Minecraft args] can be any normal Minecraft args like --nogui");
    }
}
