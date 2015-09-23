/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cuchaz.m3l.util.Logging;
import org.slf4j.Logger;

import java.util.*;


public class ModDiscoveryManager {

    public static final Logger log = Logging.getLogger();

    public static final String DesiredModClassNamesKey = "cuchaz.m3l.modClassNames";
    public static final String DiscoveredModClassNamesKey = "cuchaz.m3l.discoveredModClassNames";

    private static final ModDiscoveryManager instance = new ModDiscoveryManager();
    private Map<String, ModDiscovery> discoveries;

    private ModDiscoveryManager() {
        discoveries = Maps.newHashMap();
    }

    public static ModDiscoveryManager getInstance() {
        return instance;
    }

    public static List<String> getClassNames(String key) {
        List<String> classNames = Lists.newArrayList();
        String val = System.getProperty(key);
        if (val != null) {
            val = val.trim();
            if (!val.isEmpty()) {
                Collections.addAll(classNames, val.split(","));
            }
        }
        return classNames;
    }

    public void discoverMods() {

        // did we already discover mods?
        if (!discoveries.isEmpty()) {
            return;
        }

        log.info("Discovering mods...");

        // find all the mods
        List<ModDiscoverer> discoverers = new ArrayList<ModDiscoverer>();
        discoverers.add(new PropertiesModDiscoverer());
        discoverers.add(new ModFolderModDiscoverer());
        for (ModDiscoverer discoverer : discoverers) {
            for (ModDiscovery discovery : discoverer.findMods()) {
                if (discoveries.containsKey(discovery.getId())) {
                    log.warn("Ignoring duplicate mod: {}", discovery.getId());
                } else {
                    discoveries.put(discovery.getId(), discovery);
                }
            }
        }

        // freeze the mod list
        discoveries = Collections.unmodifiableMap(discoveries);
        log.info("Discovered {} mods", discoveries.size());

        // save the class names to the system properties
        // so it can be picked up in another class loader
        StringBuilder buf = new StringBuilder();
        for (ModDiscovery discovery : discoveries.values()) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append(discovery.getClassName());
        }
        System.setProperty(DiscoveredModClassNamesKey, buf.toString());
    }

    public Collection<ModDiscovery> discoveries() {
        return discoveries.values();
    }
}
