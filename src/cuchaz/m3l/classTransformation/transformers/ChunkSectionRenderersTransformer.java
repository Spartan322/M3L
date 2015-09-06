package cuchaz.m3l.classTransformation.transformers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.client.renderers.ChunkSectionRenderer;
import net.minecraft.client.renderers.ChunkSectionRenderers;
import net.minecraft.util.BlockPos;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.Util;


public class ChunkSectionRenderersTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(ChunkSectionRenderers.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		// hooks to tweak y-ranges of the chunk section renderers
		compiler.insertBeforeBehavior(
			c.getMethod("setRendererPositions", "(DD)V"),
			"if (" + getClass().getName() + ".setRendererPositions(this)) { return; }"
		);
		
		compiler.insertBeforeBehavior(
			c.getMethod("getRenderer", "(" + Util.getClassDesc(BlockPos.class) + ")" + Util.getClassDesc(ChunkSectionRenderer.class)),
			ChunkSectionRenderer.class.getName() + " override = " + getClass().getName() + ".getRenderer(this, $$);"
			+ "if (override != null) { return override; }"
		);
		
		compiler.insertBeforeBehavior(
			c.getMethod("initRendererCounts", "(I)V"),
			"if (" + getClass().getName() + ".initRendererCounts(this, $$)) { return; }"
		);
	}
	
	public static boolean setRendererPositions(ChunkSectionRenderers renderers) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			return chunkSystem.setChunkSectionRendererPositions(renderers);
		}
		return false;
	}
	
	public static ChunkSectionRenderer getRenderer(ChunkSectionRenderers renderers, BlockPos pos) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			ChunkSectionRenderer renderer = chunkSystem.getChunkSectionRenderer(renderers, pos);
			if (renderer != null) {
				return renderer;
			}
		}
		return null;
	}
	
	public static boolean initRendererCounts(ChunkSectionRenderers renderers, int viewDistance) {
		ChunkSystem chunkSystem = M3L.instance.getRegistry().chunkSystem.get();
		if (chunkSystem != null) {
			return chunkSystem.initChunkSectionRendererCounts(renderers, viewDistance);
		}
		return false;
	}
}
