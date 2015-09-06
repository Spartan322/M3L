/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.mod;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import cuchaz.m3l.M3L;


public class M3LModContainer implements ModContainer {
	
	private Mod m_annotation;
	private Object m_mod;
	private Map<Class<?>,Method> m_lifecycleEventHandlers;
	
	public M3LModContainer(Mod annotation, Object instance) {
		
		m_annotation = annotation;
		m_mod = instance;
		
		// get lifecycle event handlers
		m_lifecycleEventHandlers = Maps.newHashMap();
		Class<?> c = m_mod.getClass();
		for (Method method : m_mod.getClass().getMethods()) {
			if (method.isAnnotationPresent(Mod.EventHandler.class)) {
				
				// does this method look like an event handler?
				Class<?>[] argTypes = method.getParameterTypes();
				if (argTypes.length <= 0) {
					M3L.log.warn("Ignoring event handler {}.{}. Method does not accept any arguments.", c.getClass(), method.getName());
					continue;
				}
				
				Class<?> eventType = argTypes[0];
				if (!FMLStateEvent.class.isAssignableFrom(eventType)) {
					M3L.log.warn("Ignoring event handler {}.{}. First argument is not an event.", c.getClass(), method.getName());
					continue;
				}
				
				// register it
				m_lifecycleEventHandlers.put(eventType, method);
			}
		}
		
		// tell the mod about its instance
		for (Field field : m_mod.getClass().getFields()) {
			if (field.isAnnotationPresent(Mod.Instance.class)) {
				
				// does this field have the right type?
				if (!m_mod.getClass().isAssignableFrom(field.getType())) {
					M3L.log.warn("Ignoring instance field {}.{}. Field has wrong type.", c.getClass(), field.getName());
					continue;
				}
				
				try {
					field.set(m_mod, m_mod);
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					M3L.log.warn("Ignoring instance field {}.{}. Could not set value.", c.getClass(), field.getName(), ex);
				}
			}
		}
	}
	
	@Override
	public String getModId() {
		return m_annotation.modid();
	}
	
	@Override
	public String getName() {
		return m_annotation.name();
	}

	@Override
	public String getVersion() {
		return m_annotation.version();
	}

	@Override
	public Object getMod() {
		return m_mod;
	}
	
	public void sendEvent(FMLStateEvent event) {
		Method handler = m_lifecycleEventHandlers.get(event.getClass());
		if (handler != null) {
			try {
				handler.invoke(m_mod, event);
			} catch (IllegalAccessException | IllegalArgumentException ex) {
				M3L.log.error("Couldn't dispatch event to mod: {}", getModId(), ex);
			} catch (InvocationTargetException ex) {
				M3L.log.error("Exception in mod event handler: {}", getModId(), ex.getCause());
			}
		}
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		// TODO
		return null;
	}

	@Override
	public void bindMetadata(MetadataCollection metadata) {
		// TODO
	}

	@Override
	public Disableable canBeDisabled() {
		// TODO
		return null;
	}

	@Override
	public Map<String,String> getCustomModProperties() {
		// TODO
		return null;
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		// TODO
		return null;
	}

	@Override
	public List<ArtifactVersion> getDependants() {
		// TODO
		return null;
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		// TODO
		return null;
	}

	@Override
	public String getDisplayVersion() {
		// TODO
		return null;
	}

	@Override
	public String getGuiClassName() {
		// TODO
		return null;
	}

	@Override
	public ModMetadata getMetadata() {
		// TODO
		return null;
	}

	@Override
	public List<String> getOwnedPackages() {
		// TODO
		return null;
	}

	@Override
	public ArtifactVersion getProcessedVersion() {
		// TODO
		return null;
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		// TODO
		return null;
	}

	@Override
	public Map<String,String> getSharedModDescriptor() {
		// TODO
		return null;
	}

	@Override
	public Certificate getSigningCertificate() {
		// TODO
		return null;
	}

	@Override
	public String getSortingRules() {
		// TODO
		return null;
	}

	@Override
	public File getSource() {
		// TODO
		return null;
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

	@Override
	public boolean registerBus(EventBus bus, LoadController loadController) {
		// TODO
		return false;
	}

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
