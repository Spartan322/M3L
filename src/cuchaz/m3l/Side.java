/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import org.apache.commons.lang3.StringUtils;


public enum Side {
	Client,
	Server;

	public static Side get(String val) {
		return valueOf(StringUtils.capitalize(val.toLowerCase()));
	}
}
