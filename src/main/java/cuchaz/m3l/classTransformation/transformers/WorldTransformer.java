/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.M3L;
import cuchaz.m3l.api.chunks.ChunkSystem;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.Util;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Optional;

public class WorldTransformer implements ClassTransformer {

    public static Optional<Integer> getSeaLevel(World world) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.getSeaLevel(world));
        }
        return Optional.empty();
    }

    public static Optional<Double> getHorizonLevel(World world) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.getHorizonLevel(world));
        }
        return Optional.empty();
    }

	/* DEBUG: these might come in handy later
    public static RaytraceHitResult debugRaytraceBlocks(Block block, World world, BlockPos pos, Vec3 start, Vec3 stop) {
		
		M3L.log.info("Ray trace hit block: ({},{},{})", pos.getX(), pos.getY(), pos.getZ()); 
		
		// do the default thing
		return block.getHitResult(world, pos, start, stop);
	}

	public static IBlockState debugGetBlockStateAt(World world, BlockPos pos) {
		
		IBlockState state = world.getBlockStateAt(pos);
		
		M3L.log.info("Ray trace checking block state at: ({},{},{}), got {}, blockInWorld={}, chunk thinks it's {}",
			pos.getX(), pos.getY(), pos.getZ(),
			state.getBlock().name,
			world.isBlockInWorld(pos),
			world.getChunkFromBlockCoords(pos).getBlockState(pos).getBlock().name
		);
		
		return state;
	}
	*/

    public static Optional<Boolean> updateLightingAt(World world, LightType type, BlockPos pos) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.updateLightingAt(world, type, pos));
        }
        return Optional.empty();
    }

    public static Optional<Boolean> checkBlockRangeIsInWorld(World world, int minBlockX, int minBlockY, int minBlockZ, int maxBlockX, int maxBlockY, int maxBlockZ, boolean allowEmptyChunks) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            return Optional.of(chunkSystem.checkBlockRangeIsInWorld(world, minBlockX, minBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ, allowEmptyChunks));
        }
        return Optional.empty();
    }

    public static boolean checkChunksExistForEntityUpdate(World world, int minBlockX, int minBlockY, int minBlockZ, int maxBlockX, int maxBlockY, int maxBlockZ, boolean allowEmptyChunks, World worldAgain, Entity entity, boolean forceUpdate) {
        ChunkSystem chunkSystem = M3L.INSTANCE.getRegistry().chunkSystem.get().get();
        if (chunkSystem != null) {
            Boolean result = chunkSystem.checkEntityIsInWorld(world, entity, minBlockX, minBlockZ, maxBlockX, maxBlockZ, allowEmptyChunks);
            if (result != null) {
                return result;
            }
        }

        // or just do the original thing
        return world.checkBlockRangeIsInWorld(minBlockX, minBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ, allowEmptyChunks);
    }

    @Override
    public boolean meetsRequirements(CtClass c) {
        return c.getName().equals(World.class.getName());
    }

    @Override
    public void compile(HookCompiler compiler, CtClass c, Side side)
            throws NotFoundException, CannotCompileException {

        String me = getClass().getName();

        String blockPosType = Util.getClassDesc(BlockPos.class);
        String entityType = Util.getClassDesc(Entity.class);
        String lightTypeType = Util.getClassDesc(LightType.class);

        // override sea level
        compiler.insertBeforeBehavior(
                c.getDeclaredMethod("getSeaLevel"),
                "Integer override = " + me + ".getSeaLevel(this);"
                        + "if (override != null) { return override.intValue(); };"
        );

        if (side == Side.Client) {
            // override horizon level
            compiler.insertBeforeBehavior(
                    c.getDeclaredMethod("getHorizonLevel"),
                    "Double override = " + me + ".getHorizonLevel(this);"
                            + "if (override != null) { return override.doubleValue(); };"
            );
        }

        // override diffuse lighting
        compiler.insertBeforeBehavior(
                c.getMethod("updateLightingAt", "(" + lightTypeType + blockPosType + ")Z"),
                "Boolean override = " + me + ".updateLightingAt(this, $$);"
                        + "if (override != null) { return override.booleanValue(); }"
        );

        // override chunk exists
        compiler.insertBeforeBehavior(
                c.getMethod("checkBlockRangeIsInWorld", "(IIIIIIZ)Z"),
                "Boolean override = " + me + ".checkBlockRangeIsInWorld(this, $$);"
                        + "if (override != null) { return override.booleanValue(); }"
        );
        compiler.replaceVirtualCall(
                c.getMethod("updateEntity", "(" + entityType + "Z)V"),
                EntryFactory.getBehaviorEntry(World.class, "checkBlockRangeIsInWorld", "(IIIIIIZ)Z"),
                EntryFactory.getClassEntry(WorldTransformer.class),
                "checkChunksExistForEntityUpdate",
                true
        );

        // override height
        compiler.insertBeforeBehavior(
                c.getMethod("getHeight", "()I"),
                "return " + BuildSizeTransformer.class.getName() + ".getBuildHeight(this);"
        );
        compiler.insertBeforeBehavior(
                c.getMethod("getActualHeight", "()I"),
                "return " + BuildSizeTransformer.class.getName() + ".getBuildHeight(this);"
        );

        CtMethod method;
        String args = "this";

        method = c.getMethod("isBlockInWorld", "(" + blockPosType + ")Z");
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setBuildDepthLt(compiler, method, args);

        method = c.getMethod("getLight", "(" + blockPosType + ")I");
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setBuildDepthGt(compiler, method, args);

        method = c.getMethod("getLightAt", "(" + lightTypeType + blockPosType + ")I");
        BuildSizeTransformer.setBuildDepthGt(compiler, method, args);

        method = c.getMethod("getNeighborLight", "(" + blockPosType + "Z)I");
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setMaxBlockY(compiler, method, args);
        BuildSizeTransformer.setBuildDepthGt(compiler, method, args);

        method = c.getMethod("canBlockFreeze", "(" + blockPosType + "Z)Z");
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setBuildDepthLt(compiler, method, args);

        method = c.getMethod("canSnowAt", "(" + blockPosType + "Z)Z");
        BuildSizeTransformer.setBuildHeight(compiler, method, args);
        BuildSizeTransformer.setBuildDepthLt(compiler, method, args);

		/* DEBUG: these might come in handy later
        compiler.insertBeforeBehavior(
			c.getMethod("isBlockInWorld", "(" + Util.getClassDesc(BlockPos.class) + ")Z"),
			"if ($1.getX() == 28 && $1.getY() < 0 && $1.getZ() == 244) {"
				+ "int height = " + getClass().getName() + ".getBuildHeight(this);"
				+ "int depth = " + getClass().getName() + ".getBuildDepth(this);"
				+ "System.out.println(\"is block in world? \" + $1.getY() + \" [\" + depth + \",\" + height + \") \" + ($1.getY() >= depth && $1.getY() < height) + \" \" + this.getChunkFromBlockCoords($1).getBlockState($1).getBlock().name);"
			+ "}"
		);
		CtMethod raytraceBlocksMethod = c.getMethod("raytraceBlocks", String.format("(%s%sZZZ)%s", Util.getClassDesc(Vec3.class), Util.getClassDesc(Vec3.class), Util.getClassDesc(RaytraceHitResult.class)));
		compiler.replaceVirtualCall(
			raytraceBlocksMethod,
			EntryFactory.getBehaviorEntry(Block.class, "getHitResult", "(%s%s%s%s)%s", World.class, BlockPos.class, Vec3.class, Vec3.class, RaytraceHitResult.class),
			m_thisClass,
			"debugRaytraceBlocks",
			false
		);
		compiler.replaceVirtualCall(
			raytraceBlocksMethod,
			EntryFactory.getBehaviorEntry(World.class, "getBlockStateAt", "(%s)%s", BlockPos.class, IBlockState.class),
			m_thisClass,
			"debugGetBlockStateAt",
			false
		);
		*/
    }
}
