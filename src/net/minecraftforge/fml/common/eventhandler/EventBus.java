/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package net.minecraftforge.fml.common.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cuchaz.m3l.M3L;
import cuchaz.m3l.mod.M3LModContainer;

public class EventBus {
	
	private static int m_nextBusId = 0;
	
	private int m_id;
	private Multimap<Object,IEventListener> m_listeners;
	
	public EventBus() {
		m_id = m_nextBusId++;
		m_listeners = ArrayListMultimap.create();
		ListenerList.resize(m_id + 1);
	}
	
	public void register(Object target) {
		register(target, SubscribeEvent.class);
	}
	
	public void register(Object target, Class<? extends Annotation> annotationType) {
		
		// get the mod that's trying to register this target
		M3LModContainer container = M3L.instance.getModContainers().getFromStackTrace();
		if (container == null) {
			M3L.log.error("Called from unknown mod instance. Can't register for events.");
			return;
		}
		
		// look for event handlers on the target
		for (Method method : target.getClass().getMethods()) {
			if (method.isAnnotationPresent(annotationType)) {
				
				// does this method look like an event handler?
				Class<?>[] argTypes = method.getParameterTypes();
				if (argTypes.length <= 0) {
					M3L.log.warn("Ignoring event handler {}.{}. Method does not accept any arguments.", target.getClass(), method.getName());
					continue;
				}
				
				Class<?> eventType = argTypes[0];
				if (!Event.class.isAssignableFrom(eventType)) {
					M3L.log.warn("Ignoring event handler {}.{}. First argument is not an event.", target.getClass(), method.getName());
					continue;
				}
				
				try {
					
					// register this event handler
					Constructor<?> ctr = eventType.getConstructor();
		            ctr.setAccessible(true);
		            Event event = (Event)ctr.newInstance();
		            ASMEventHandler listener = new ASMEventHandler(target, method, container);
		            event.getListenerList().register(m_id, listener.getPriority(), listener);
		            
		            m_listeners.put(target, listener);
		            
				} catch (Exception ex) {
					M3L.log.error("Unable to register event handler {}.{}", target.getClass(), method.getName(), ex);
				}
			}
		}
	}
	
	public void unregister(Object object) {
		for (IEventListener listener : m_listeners.get(object)) {
			ListenerList.unregisterAll(m_id, listener);
		}
	}
	
	public boolean post(Event event) {
		for (IEventListener listener : event.getListenerList().getListeners(m_id)) {
			listener.invoke(event);
		}
		return event.isCancelable() && event.isCanceled();
	}
}
