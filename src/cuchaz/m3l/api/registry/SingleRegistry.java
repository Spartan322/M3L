/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api.registry;


public class SingleRegistry<T> {
	
	private T m_val;
	
	public SingleRegistry() {
		m_val = null;
	}
	
	public boolean isRegistered() {
		return m_val != null;
	}
	
	public void register(T val)
	throws AlreadyRegisteredException {
		if (isRegistered()) {
			throw new AlreadyRegisteredException();
		}
		m_val = val;
	}
	
	public T get() {
		return m_val;
	}
}
