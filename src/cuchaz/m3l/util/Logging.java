/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Logging {

	public static Logger getLogger() {
		return getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
	}

	public static Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}
}
