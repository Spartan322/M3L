/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.mod;

import com.google.common.collect.Maps;
import cuchaz.m3l.M3L;
import cuchaz.m3l.api.versioning.ArtifactData;
import cuchaz.m3l.api.versioning.InvalidVersionFormatException;
import cuchaz.m3l.api.versioning.Version;
import cuchaz.m3l.api.versioning.VersionRange;
import cuchaz.m3l.event.state.M3LStateEvent;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.util.*;


public class M3LModContainer implements CommonModContainer {
    private M3LMod annotation;
    private Object mod;
    private Optional<CommonModContainer.Disableable> disableable = Optional.empty();
    private Optional<Map<String, String>> modProperties = Optional.empty();
    private Map<Class<?>, Method> lifecycleEventHandlers;

    public M3LModContainer(M3LMod annotation, Object instance) {

        this.annotation = annotation;
        mod = instance;

        // get lifecycle event handlers
        lifecycleEventHandlers = Maps.newHashMap();
        Class<?> c = mod.getClass();
        for (Method method : mod.getClass().getMethods()) {
            if (method.isAnnotationPresent(M3LMod.EventMarker.class)) {
                Class<?>[] argTypes = method.getParameterTypes();
                if (argTypes.length > 0) {
                    Class<?> eventType = argTypes[0];
                    if (argTypes.length > 0 && M3LStateEvent.class.isAssignableFrom(eventType)) {
                        lifecycleEventHandlers.put(eventType, method);
                    } else {
                        M3L.LOGGER.warn("Ignoring event handler {}.{}. First argument is not an event.", c.getClass(), method.getName());
                    }
                } else {
                    M3L.LOGGER.warn("Ignoring event handler {}.{}. Method does not accept any arguments.", c.getClass(), method.getName());
                }
            } else if (method.isAnnotationPresent(Mod.EventHandler.class)) {
                Class<?>[] argTypes = method.getParameterTypes();
                if (argTypes.length > 0) {
                    Class<?> eventType = argTypes[0];
                    if (argTypes.length > 0 && FMLStateEvent.class.isAssignableFrom(eventType)) {
                        lifecycleEventHandlers.put(eventType, method);
                    } else {
                        M3L.LOGGER.warn("Ignoring event handler {}.{}. First argument is not an event.", c.getClass(), method.getName());
                    }
                } else {
                    M3L.LOGGER.warn("Ignoring event handler {}.{}. Method does not accept any arguments.", c.getClass(), method.getName());
                }
            }
        }
    }

    @Override
    public String getModId() {
        return annotation.id();
    }

    @Override
    public String getName() {
        return annotation.name();
    }

    @Override
    public String getVersion() {
        return annotation.version();
    }

    @Override
    public Object getMod() {
        return mod;
    }

    public Method getEventMethod(M3LStateEvent event) {
        return lifecycleEventHandlers.get(event.getClass());
    }

    public void sendEvent(M3LStateEvent event) {
        Method handler = lifecycleEventHandlers.get(event.getClass());
        if (handler != null) {
            try {
                handler.invoke(mod, event);
            } catch (IllegalAccessException ex) {
                M3L.LOGGER.error("Couldn't dispatch event to mod: {}", getModId(), ex);
            } catch (IllegalArgumentException ex) {
                M3L.LOGGER.error("Couldn't dispatch event to mod: {}", getModId(), ex);
            } catch (InvocationTargetException ex) {
                M3L.LOGGER.error("Exception in mod event handler: {}", getModId(), ex.getCause());
            }
        }
    }

    @Override
    public VersionRange acceptableMinecraftVersions() {
        if (annotation.acceptedMinecraftVersions().length > 0) {
            try {
                Version[] versions = Version.fromStringArray(annotation.acceptedMinecraftVersions());
                return new VersionRange(versions[0], Arrays.copyOfRange(versions, 1, versions.length));
            } catch (InvalidVersionFormatException e) {
                M3L.LOGGER.error("Unsupported version format in: {}", annotation.acceptedMinecraftVersions(), e.getCause());
                e.printStackTrace();
            }
        }
        try {
            return new VersionRange(Version.fromString(annotation.version()));
        } catch (InvalidVersionFormatException e) {
            e.printStackTrace();
        }
        return VersionRange.EMPTY;
    }

    @Override
    public Disableable canBeDisabled() {
        return disableable != Optional.<Disableable>empty() ? disableable.get() : Disableable.NEVER;
    }

    protected Disableable setDisableable(Disableable setter) {
        disableable = Optional.of(setter);
        return disableable.get();
    }

    @Override
    public Map<String, String> getCustomModProperties() {
        if (modProperties == Optional.<Map<String, String>>empty()) {
            M3LMod.CustomProperty properties[] = annotation.customProperties();

            Map<String, String> result = new HashMap<String, String>(properties.length);
            for (M3LMod.CustomProperty property : properties) {
                result.put(property.key(), property.value());
            }

            modProperties = Optional.of(result);
            return modProperties.get();
        } else {
            return modProperties.get();
        }
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        // TODO
        return ResourcePackRepository.ResourcePack.class;
    }

    @Override
    public List<ArtifactData> getDependants() {
        // TODO
        return new ArrayList<ArtifactData>(0);
    }

    @Override
    public List<ArtifactData> getDependencies() {
        // TODO
        return new ArrayList<ArtifactData>(0);
    }

    @Override
    public String getDisplayVersion() {
        try {
            return new Version(annotation.version()).toString();
        } catch (InvalidVersionFormatException e) {
            e.printStackTrace();
            return String.format("INVALID VERSIONING: %s", annotation.id());
        }
    }

    @Override
    public String getGuiClassName() {
        // TODO
        return "";
    }

    @Override
    public ModMetadata getMetadata() {
        // TODO
        return new ModMetadata();
    }

    @Override
    public void bindMetadata(MetadataCollection mc) {
        // TODO
    }

    @Override
    public List<String> getOwnedPackages() {
        // TODO
        return new ArrayList<String>(0);
    }

    @Override
    public ArtifactData getProcessedVersion() {
        // TODO
        return null;
    }

    @Override
    public List<ArtifactData> getRequirements() {
        // TODO
        return new ArrayList<ArtifactData>(0);
    }

    @Override
    public Map<String, String> getSharedModDescriptor() {
        // TODO
        return new HashMap<String, String>(0);
    }

    @Override
    public Certificate getSigningCertificate() {
        // TODO
        return null;
    }

    @Override
    public String getSortingRules() {
        // TODO
        return "";
    }

    @Override
    public File getSource() {
        // TODO
        return new File("");
    }

    @Override
    public boolean isImmutable() {
        // TODO
        return false;
    }

    @Override
    public boolean matches(Object obj) {
        // TODO
        return false;
    }

//    @Override
//    public boolean registerBus(EventBus bus, LoadController loadController) {
//        // TODO
//        return false;
//    }

    @Override
    public void setEnabledState(boolean val) {
        // TODO
    }

    @Override
    public boolean shouldLoadInEnvironment() {
        // TODO Auto-generated method stub
        return false;
    }
}
