package cuchaz.m3l.classTransformation.transformers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.Util;


public class BlockDoorTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(BlockDoor.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		// fix build height
		CtMethod method = c.getMethod("canPlace", "(" + Util.getClassDesc(World.class) + Util.getClassDesc(BlockPos.class) + ")Z");
		BuildSizeTransformer.setMaxBlockY(compiler, method, "$1");
	}
}
