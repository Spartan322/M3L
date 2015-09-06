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
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderers.FontRenderer;
import net.minecraft.main.Minecraft;
import cuchaz.m3l.Constants;
import cuchaz.m3l.M3L;
import cuchaz.m3l.Side;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;

public class MainMenuTransformer implements ClassTransformer {
	
	private static String m_message = null;
	
	@Override
	public boolean meetsRequirements(CtClass c) {
		return c.getName().equals(GuiMainMenu.class.getName());
	}
	
	@Override
	public void compile(HookCompiler compiler, CtClass c, Side side)
	throws NotFoundException, CannotCompileException {
		
		// add a hook so we can render on the main menu
		compiler.insertAfterVoidBehavior(
			c.getMethod("drawScreen", "(IIF)V"),
			getClass().getName() + ".drawScreen(this, $$);"
		);
	}
	
	public static void drawScreen(GuiMainMenu gui, int x, int y, float time) {
		if (m_message == null) {
			int numMods = M3L.instance.getModContainers().size();
			m_message = String.format("M3L v%s : %s %s loaded",
				Constants.Version,
				numMods,
				numMods == 1 ? "mod" : "mods"
			);
		}
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		gui.drawString(fontRenderer, m_message, 2, gui.height - 20, -1);
	}
}
