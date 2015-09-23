/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.transformers;

import cuchaz.m3l.M3L;
import cuchaz.m3l.classTransformation.ClassTransformer;
import cuchaz.m3l.classTransformation.HookCompiler;
import cuchaz.m3l.lib.Constants;
import cuchaz.m3l.lib.Side;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderers.FontRenderer;
import net.minecraft.main.Minecraft;

import java.util.Optional;

public class MainMenuTransformer implements ClassTransformer {

    private static Optional<String> m_message = Optional.empty();

    public static void drawScreen(GuiMainMenu gui, int x, int y, float time) {
        if (!m_message.isPresent() && m_message == Optional.<String>empty()) {
            int numMods = M3L.INSTANCE.getModManager().size();
            m_message = Optional.of(String.format("M3L v%s : %s %s loaded",
                    Constants.VERSION,
                    numMods,
                    numMods == 1 ? "mod" : "mods"));
        }
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        gui.drawString(fontRenderer, m_message.get(), 2, gui.height - 20, -1);
    }

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
}
