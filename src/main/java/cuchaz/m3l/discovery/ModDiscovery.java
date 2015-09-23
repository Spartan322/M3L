/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.discovery;

import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;


public class ModDiscovery {

    private String id;
    private String className;
    private Optional<File> jarFile;

    public ModDiscovery(String id, String className) {
        this(id, className, null);
    }

    public ModDiscovery(String id, String className, File jarFile) {
        this.id = id;
        this.className = className;
        this.jarFile = Optional.of(jarFile);
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if (jarFile.isPresent()) {
            try {
                classLoader.addURL(jarFile.get().toURI().toURL());
            } catch (MalformedURLException ex) {
                throw new Error(ex);
            }
        }
    }
}
