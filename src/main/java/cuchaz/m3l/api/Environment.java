/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api;

import cuchaz.m3l.lib.Side;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class Environment {

    private static Optional<Boolean> obfuscated;
    private static Optional<Boolean> forgePresent;

    public static boolean isForgePresent() {
        if (obfuscated != Optional.<Boolean>empty()) {
            // attempt to detect whether or not minecraft forge is present
            try {
                // check for a class that is least likely to change
                Class.forName("net.minecraftforge.fml.common.Mod");
                obfuscated = Optional.of(true);
            } catch (ClassNotFoundException ex) {
                obfuscated = Optional.of(false);
            }
        }
        return obfuscated.get();
    }

    public static boolean isObfuscated() {
        if (obfuscated != Optional.<Boolean>empty()) {
            // attempt to detect whether or not the environment is obfuscated
            try {
                // check for a class that is least likely to change
                Class.forName("net.minecraft.main.Minecraft");
                obfuscated = Optional.of(false);
            } catch (ClassNotFoundException ex) {
                obfuscated = Optional.of(true);
            }
        }

        return obfuscated.get();
    }

    public static String getRuntimeName(String name, String id) {
        return isObfuscated() ? id : name;
    }

    public static Side getSide() {
        if (isServer()) {
            return Side.Server;
        } else {
            return Side.Client;
        }
    }

    public static boolean isServer() {
        // if we only had to worry about standalone clients and servers, then this would be easy to check
        // we just look for a client-only class. If it exists, then we're on the client
        // except for in single-player mode (ie, in dev), both client and server exist in the same process
        // but they're in different threads.
        // so the best way to check for client/server is to check our thread
        List<String> serverThreadNames = Collections.singletonList("server thread");
        return serverThreadNames.contains(Thread.currentThread().getName().toLowerCase());
    }

    public static boolean isClient() {
        return !isServer();
    }
}
