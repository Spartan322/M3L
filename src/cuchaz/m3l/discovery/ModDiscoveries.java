/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cuchaz.m3l.util.Logging;


public class ModDiscoveries {
	
	public static final Logger log = Logging.getLogger();
	
	public static final String DesiredModClassNamesKey = "cuchaz.m3l.modClassNames";
	public static final String DiscoveredModClassNamesKey = "cuchaz.m3l.discoveredModClassNames";
	
	private static final ModDiscoveries m_instance = new ModDiscoveries();
	
	public static ModDiscoveries getInstance() {
		return m_instance;
	}
	
	private Map<String,ModDiscovery> m_discoveries;
	
	private ModDiscoveries() {
		m_discoveries = Maps.newHashMap();
	}
	
	public void discoverMods() {
		
		// did we already discover mods?
		if (!m_discoveries.isEmpty()) {
			return;
		}
		
		log.info("Discovering mods...");
		
		// find all the mods
		List<ModDiscoverer> discoverers = new ArrayList<ModDiscoverer>();
		discoverers.add(new PropertiesModDiscoverer());
		discoverers.add(new ModFolderModDiscoverer());
		for (ModDiscoverer discoverer : discoverers) {
			for (ModDiscovery discovery : discoverer.findMods()) {
				if (m_discoveries.containsKey(discovery.getId())) {
					log.warn("Ignoring duplicate mod: {}", discovery.getId());
				} else {
					m_discoveries.put(discovery.getId(), discovery);
				}
			}
		}
		
		// freeze the mod list
		m_discoveries = Collections.unmodifiableMap(m_discoveries);
		log.info("Discovered {} mods", m_discoveries.size());
		
		// save the class names to the system properties
		// so it can be picked up in another class loader
		StringBuilder buf = new StringBuilder();
		for (ModDiscovery discovery : m_discoveries.values()) {
			if (buf.length() > 0) {
				buf.append(",");
			}
			buf.append(discovery.getClassName());
		}
		System.setProperty(DiscoveredModClassNamesKey, buf.toString());
	}
	
	public Collection<ModDiscovery> discoveries() {
		return m_discoveries.values();
	}

	public static List<String> getClassNames(String key) {
		List<String> classNames = Lists.newArrayList();
		String val = System.getProperty(key);
		if (val != null) {
			for (String className : val.split(",")) {
				classNames.add(className);
			}
		}
		return classNames;
	}
}
