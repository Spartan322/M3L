/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import cuchaz.m3l.Side;


public class TweakerClassTransformerClient extends TweakerClassTransformer {
	
	public TweakerClassTransformerClient() {
		super(Side.Client, true);
	}
}
