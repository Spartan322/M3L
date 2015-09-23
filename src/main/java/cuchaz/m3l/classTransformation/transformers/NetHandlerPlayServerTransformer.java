/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.classTransformation.transformers;

import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.Util;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.network.play.NetHandlerPlayServer;
import net.minecraft.network.play.packet.serverbound.PacketBlockDig;
import net.minecraft.network.play.packet.serverbound.PacketBlockPlace;
import net.minecraft.server.MinecraftServer;


public class NetHandlerPlayServerTransformer implements ClassTransformer {

    public static int getBuildHeight(MinecraftServer server, NetHandlerPlayServer netHandler, PacketBlockDig packet) {
        return BuildSizeTransformer.getBuildHeight(netHandler.player.getWorld());
    }

    public static int getBuildHeight(MinecraftServer server, NetHandlerPlayServer netHandler, PacketBlockPlace packet) {
        return BuildSizeTransformer.getBuildHeight(netHandler.player.getWorld());
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(NetHandlerPlayServer.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        // calls to get the build height are apparently per-server, not per-world
        // change to be per-world
        BehaviorEntry targetBehavior = EntryFactory.getBehaviorEntry(MinecraftServer.class, "getBuildLimit", "()I");
        compiler.replaceVirtualCall(
                c.getMethod("handleBlockDig", "(" + Util.getClassDesc(PacketBlockDig.class) + ")V"),
                targetBehavior,
                EntryFactory.getClassEntry(getClass()),
                "getBuildHeight",
                true
        );
        compiler.replaceVirtualCall(
                c.getMethod("handleBlockPlace", "(" + Util.getClassDesc(PacketBlockPlace.class) + ")V"),
                targetBehavior,
                EntryFactory.getClassEntry(getClass()),
                "getBuildHeight",
                true
        );
    }
}
