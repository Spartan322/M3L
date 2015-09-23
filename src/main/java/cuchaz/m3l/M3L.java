/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l;

import cuchaz.m3l.api.registry.Registry;
import cuchaz.m3l.classTranslation.TranslatingRewritePolicy;
import cuchaz.m3l.mod.ModManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.IntegratedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class M3L {
    public static final M3L INSTANCE = new M3L();
    public static final Logger LOGGER = LoggerFactory.getLogger("M3L");

    public static final File CONFIGURATION_FOLDER = new File(Launch.minecraftHome.getPath() + "modConfig");

    private ModManager m_mods;
    private Registry m_registry;


    private M3L() {
        m_mods = new ModManager();
        m_registry = new Registry();
    }

    public void initClient() {
        m_mods.load();
        TranslatingRewritePolicy.install();
    }

    public void initDedicatedServer() {
        m_mods.load();
        TranslatingRewritePolicy.install();
    }

    public void initIntegratedServer(IntegratedServer server) {
        // TODO: handle server init
    }

    public ModManager getModManager() {
        return m_mods;
    }

    public Registry getRegistry() {
        return m_registry;
    }
}
