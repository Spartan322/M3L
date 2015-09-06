/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.mod;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import cuchaz.m3l.M3L;
import cuchaz.m3l.discovery.ModDiscoveries;

public class ModContainers {
	
	private Map<String,M3LModContainer> m_containersById;
	private Map<Object,M3LModContainer> m_containersByMod;
	private Map<String,M3LModContainer> m_containersByModClassName;
	
	public ModContainers() {
		m_containersById = Maps.newHashMap();
		m_containersByMod = Maps.newHashMap();
		m_containersByModClassName = Maps.newHashMap();
	}
	
	public void load() {
		
		// TODO: implement mod dependency resolution and load order
		
		construct();
		sendLifeEventToMods(new FMLConstructionEvent(null, null, null));
		sendLifeEventToMods(new FMLPreInitializationEvent(null, null));
		sendLifeEventToMods(new FMLInitializationEvent());
		sendLifeEventToMods(new FMLPostInitializationEvent());
		sendLifeEventToMods(new FMLLoadCompleteEvent());
	}
	
	private void construct() {
		
		List<String> modClassNames = ModDiscoveries.getClassNames(ModDiscoveries.DiscoveredModClassNamesKey);
		M3L.log.info("Loading {} mods...", modClassNames.size());
		
		for (String modClassName : modClassNames) {
			M3L.log.info("Loading mod: {}", modClassName);
			
			try {
				// get the mod instance
				Class<?> modClass = Class.forName(modClassName);
				
				// look for the annotation
				net.minecraftforge.fml.common.Mod annotation = modClass.getAnnotation(net.minecraftforge.fml.common.Mod.class);
				if (annotation == null) {
					M3L.log.error("Loaded class is not a mod: {}", modClassName);
				}
				
				// construct the mod
				M3LModContainer container = new M3LModContainer(annotation, modClass.newInstance());
				m_containersById.put(container.getModId(), container);
				m_containersByMod.put(container.getMod(), container);
				m_containersByModClassName.put(modClassName, container);
				
			} catch (ClassNotFoundException ex) {
				M3L.log.error("Couldn't find mod class: {}", modClassName, ex);
			} catch (InstantiationException | IllegalAccessException ex) {
				M3L.log.error("Couldn't get mod instance: {}", modClassName, ex);
			}
		}
	}
	
	public void sendLifeEventToMods(FMLStateEvent event) {
		for (M3LModContainer mod : m_containersById.values()) {
			mod.sendEvent(event);
		}
	}

	public M3LModContainer getFromStackTrace() {
		for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
			M3LModContainer mod = m_containersByModClassName.get(frame.getClassName());
			if (mod != null) {
				return mod;
			}
		}
		return null;
	}

	public int size() {
		return m_containersById.size();
	}
}
