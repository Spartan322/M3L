/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.world.biome.Biome;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.util.Util;

public class BiomeGenBaseTransformer implements ClassTransformer {
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(Biome.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		// BiomeGenBase func_150568_d( int )
		compiler.insertBeforeBehavior(c.getMethod("func_150568_d", "(I)" + Util.getClassDesc(Biome.class)), "$1 = " + getClass().getName() + ".mapBiomeId( $1 );");
	}
	
	public static int mapBiomeId(int id) {
		int largestId = getLargestBiomeId();
		
		// fix biomes that are too small
		if (id < 0) {
			id *= -1;
		}
		
		// fix biomes that are too large
		id %= largestId + 1;
		
		return id;
	}
	
	private static int getLargestBiomeId() {
		Biome[] biomes = Biome.getBiomeArray();
		for (int i = 0; i < biomes.length; i++) {
			if (biomes[i] == null) {
				return i - 1;
			}
		}
		return 0;
	}
}
