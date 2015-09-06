/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import net.minecraft.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cuchaz.m3l.api.registry.Registry;
import cuchaz.m3l.classTranslation.TranslatingRewritePolicy;
import cuchaz.m3l.mod.ModContainers;


public class M3L {
	
	public static final M3L instance = new M3L();
	public static final Logger log = LoggerFactory.getLogger("M3L");

	private ModContainers m_mods;
	private Registry m_registry;

	private M3L() {
		m_mods = new ModContainers();
		m_registry = new Registry();
	}
	
	public void initClient() {
		m_mods.load();
		TranslatingRewritePolicy.install();
	}
	
	public void initDedicatedServer(MinecraftServer server) {
		m_mods.load();
		
		// TODO: handle server init
	}
	
	public void initIntegratedServer(IntegratedServer server) {
		// TODO: handle server init
	}
	
	public ModContainers getModContainers() {
		return m_mods;
	}
	
	public Registry getRegistry() {
		return m_registry;
	}
}
