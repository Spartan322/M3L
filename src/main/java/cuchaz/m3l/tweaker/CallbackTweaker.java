/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.tweaker;

import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.ArgumentBuilder;
import net.minecraft.launchwrapper.Launch;

import java.util.Map;


public abstract class CallbackTweaker extends Tweaker {

    protected CallbackTweaker(Class<?> callbackClass) {
        super(callbackClass.getName(), TweakerClassTransformerClient.class.getName(), Side.Client);
    }

    public static <T extends CallbackTweaker> void launch(Class<T> tweakerType) {
        launch(tweakerType, null);
    }

    public static <T extends CallbackTweaker> void launch(Class<T> tweakerType, Map<String, String> args) {

        // build the base arguments
        ArgumentBuilder argumentBuilder = new ArgumentBuilder();
        argumentBuilder.add("tweakClass", tweakerType.getName());
        argumentBuilder.add("userProperties", "{}");
        argumentBuilder.add("accessToken", "M3Luser");

        // add more arguments if needed
        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, String> arg : args.entrySet()) {
                argumentBuilder.add(arg.getKey(), arg.getValue());
            }
        }

        Launch.main(argumentBuilder.build());
    }
}
