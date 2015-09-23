/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.tweaker;

import cuchaz.m3l.lib.Side;

public class TweakerClient extends Tweaker {

    public TweakerClient() {
        super("net.minecraft.client.main.Main", TweakerClassTransformerClient.class.getName(), Side.Client);
    }
}
