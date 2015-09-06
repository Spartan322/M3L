/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import java.util.Map;

import com.google.common.collect.Maps;

public class Arguments {
	Map<String,String> m_args;
	
	public Arguments() {
		m_args = Maps.newHashMap();
	}
	
	public boolean contains(String name) {
		return m_args.containsKey(name);
	}
	
	public String get(String name) {
		return m_args.get(name);
	}
	
	public void set(String name, String value) {
		m_args.put(name, value);
	}
	
	public void set(String[] args, int start, int stop) {
		for (int i = start; i <= stop; i++) {
			String key = args[i];
			if (key.startsWith("--") && i + 1 <= stop) {
				key = key.substring(2);
				String val = args[i + 1];
				set(key, val);
			}
		}
	}
	
	public String[] build() {
		String[] args = new String[m_args.size() * 2];
		int i = 0;
		for (Map.Entry<String,String> entry : m_args.entrySet()) {
			args[i++] = "--" + entry.getKey();
			args[i++] = entry.getValue();
		}
		return args;
	}
}
