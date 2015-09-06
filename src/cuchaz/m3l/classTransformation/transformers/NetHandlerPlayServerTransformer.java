package cuchaz.m3l.classTransformation.transformers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.network.play.NetHandlerPlayServer;
import net.minecraft.network.play.packet.serverbound.PacketBlockDig;
import net.minecraft.network.play.packet.serverbound.PacketBlockPlace;
import net.minecraft.server.MinecraftServer;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.Util;


public class NetHandlerPlayServerTransformer implements ClassTransformer {
	
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
	
	public static int getBuildHeight(MinecraftServer server, NetHandlerPlayServer netHandler, PacketBlockDig packet) {
		return BuildSizeTransformer.getBuildHeight(netHandler.player.getWorld());
	}
	
	public static int getBuildHeight(MinecraftServer server, NetHandlerPlayServer netHandler, PacketBlockPlace packet) {
		return BuildSizeTransformer.getBuildHeight(netHandler.player.getWorld());
	}
}
