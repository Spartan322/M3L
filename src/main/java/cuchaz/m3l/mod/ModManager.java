/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.mod;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cuchaz.m3l.M3L;
import cuchaz.m3l.api.Environment;
import cuchaz.m3l.discovery.ModDiscoveryManager;
import cuchaz.m3l.event.state.*;
import hr.caellian.m3l.util.topologicalSorting.OrderedMethodExecutor;

import java.lang.reflect.Method;
import java.util.*;

public class ModManager {
    protected Map<String, M3LModContainer> containersById = Maps.newHashMap();
    protected Map<Object, M3LModContainer> containersByMod = Maps.newHashMap();
    protected Map<String, M3LModContainer> containersByModClassName = Maps.newHashMap();

    protected ArrayList<String> modDependencies = Lists.newArrayList();
    protected ArrayList<String> missingMods = Lists.newArrayList();

    public void load() {
        loadM3LMods();
        checkModDependencies();

        sendLifeEventToMods(new M3LConstructionEvent());
        sendLifeEventToMods(new M3LPreInitializationEvent(M3L.CONFIGURATION_FOLDER));
        sendLifeEventToMods(new M3LInitializationEvent());
        sendLifeEventToMods(new M3LPostInitializationEvent());
        sendLifeEventToMods(new M3LLoadCompleteEvent());
    }

    private void checkModDependencies() {
        for (String requiredMod : modDependencies) {
            if (!containersById.containsKey(requiredMod)) {
                M3L.LOGGER.warn("Dependency with id \"{}\" not found. Will try to run without it.", requiredMod);
                missingMods.add(requiredMod);
            }
        }
    }

    private void loadM3LMods() {
        List<String> modClassNames = ModDiscoveryManager.getClassNames(ModDiscoveryManager.DiscoveredModClassNamesKey);
        if (modClassNames.isEmpty()) {
            M3L.LOGGER.info("No mods to load");
            return;
        }

        M3L.LOGGER.info("Loading {} mods...", modClassNames.size());

        for (String modClassName : modClassNames) {
            M3L.LOGGER.info("Loading mod: {}", modClassName);

            try {
                // get the mod instance
                Class<?> modClass = Class.forName(modClassName);

                // look for the annotation
                M3LMod annotation = modClass.getAnnotation(M3LMod.class);
                if (annotation == null) {
                    M3L.LOGGER.error("Loaded class is not a mod: {}", modClassName);
                }

                if ((annotation != null && annotation.clientSideOnly()) && Environment.isClient()) {
                    continue;
                } else if ((annotation != null && annotation.serverSideOnly()) && Environment.isServer()) {
                    continue;
                }

                // construct the mod
                M3LModContainer container = new M3LModContainer(annotation, modClass.newInstance());
                containersById.put(container.getModId(), container);
                containersByMod.put(container.getMod(), container);
                containersByModClassName.put(modClassName, container);

                if (annotation != null) {
                    modDependencies.addAll(Arrays.asList(annotation.dependencies()));
                }

            } catch (ClassNotFoundException ex) {
                M3L.LOGGER.error("Couldn't find mod class: {}", modClassName, ex);
            } catch (InstantiationException ex) {
                M3L.LOGGER.error("Couldn't get mod instance: {}", modClassName, ex);
            } catch (IllegalAccessException ex) {
                M3L.LOGGER.error("Couldn't get mod instance: {}", modClassName, ex);
            }
        }
    }

    public void sendLifeEventToMods(M3LStateEvent event) {
        OrderedMethodExecutor collectedMethods = new OrderedMethodExecutor();
        HashBiMap<Method, M3LModContainer> modReferences = HashBiMap.create();
        for (M3LModContainer mod : containersById.values()) {
            Method eventMethod = mod.getEventMethod(event);
            modReferences.put(eventMethod, mod);
            collectedMethods.add(eventMethod);
        }
        collectedMethods.execute(modReferences, event);
        collectedMethods.clear();
    }

    public Optional<M3LModContainer> getFromStackTrace() {
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            M3LModContainer mod = containersByModClassName.get(frame.getClassName());
            if (mod != null) {
                return Optional.of(mod);
            }
        }
        return Optional.empty();
    }

    public ArrayList<String> getMissingMods() {
        return missingMods;
    }

    public ArrayList<String> getModDependencies() {
        return modDependencies;
    }

    public int size() {
        return containersById.size();
    }
}
