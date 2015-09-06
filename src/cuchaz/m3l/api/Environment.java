/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api;

import java.util.Arrays;
import java.util.List;

import cuchaz.m3l.Side;


public class Environment {
	
	private static Boolean m_isObfuscated;
	
	static {
		m_isObfuscated = null;
	}
	
	public static boolean isObfuscated() {
		if (m_isObfuscated == null) {
			// attempt to detect whether or not the environment is obfuscated
			try {
				// check for a well-known class name
				Class.forName("net.minecraft.entity.Entity");
				m_isObfuscated = false;
			} catch (ClassNotFoundException ex) {
				m_isObfuscated = true;
			}
		}
		
		return m_isObfuscated;
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
		List<String> serverThreadNames = Arrays.asList("server thread");
		return serverThreadNames.contains(Thread.currentThread().getName().toLowerCase());
	}
	
	public static boolean isClient() {
		return !isServer();
	}
}
