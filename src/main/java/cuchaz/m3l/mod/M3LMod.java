/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.mod;

import cuchaz.m3l.api.versioning.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Caellian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface M3LMod {
    /**
     * It is preferred that mod if follows Minecraft id formatting:
     * Mod name without any capital letters, numbers or diacritical marks and with underscore ('_') replacing spaces
     * in name (e.g. "applied_energistics", "extra_trees"). Version numbers should not be put in mod id
     * (e.g. "thaumcraft_2") - they should be put in version instead.
     * It is a bad idea to use short names for mods (e.g. "twl" instead of "taller_worlds_mod").
     * Following Minecraft formatting will make it easier for other mod developers to reference your mod from their code.
     *
     * @return String id of the mod
     */
    String id();

    /**
     * This is your mod name. You are allowed to do whatever you wish with it as long as it doesn't break Minecraft.
     *
     * @return String name of the mod
     */
    String name();

    /**
     * Use of semantic versioning is encouraged here as it will make it easier for others to understand roughly what
     * changed with your code in newer versions. For reference, see: <a href="http://semver.org/">Semantic Versioning</a>
     * <p/>
     * Semantic versioning is also supported in {@link Version} convenience class.
     *
     * @return String version of the mod.
     */
    String version();

    /**
     * A simple description of the mod.
     *
     * @return Mod description
     */
    String description() default "";

    /**
     * The domains used by the mod for its assets.
     * These domain names are used to load the assets from file.
     * The assets should be placed in <i>assets/<b>domain</b>/*</i>
     *
     * @return An array of domain names.
     */
    String[] domains() default {};

    /**
     * @return String array of mods that should be co-installed with your mod.
     * @throws NullPointerException thrown if defined mod id isn't loaded.
     */
    String[] dependencies() default "";

    /**
     * This method tells M3L is the mod is bundled with it's metadata json file.
     *
     * @return True if mod metadata is provided with the mod.
     */
    boolean useMetadata() default false;

    /**
     * This will prevent your mod from being ran on Minecraft servers.
     *
     * @return True if this mod should only be installed on client.
     */
    boolean clientSideOnly() default false;

    /**
     * This will prevent your mod from being ran on Minecraft clients.
     *
     * @return True if this mod should only be installed on server.
     */
    boolean serverSideOnly() default false;


    String[] acceptedMinecraftVersions() default "";

    String[] acceptableRemoteVersions() default "";

    //TODO: Ask players do they want to open the world with mod that might break it.
    String[] acceptableSaveVersions() default "";

    /**
     * Currently supported are: "java".
     *
     * @return String value of language this mod is written in.
     */
    String modLanguage() default "java";

    /**
     * @deprecated
     */
    @Deprecated String asmHookClass() default "";

    /**
     * This is useful for mods that only change client and don't affect worlds.
     *
     * @return True if this mod can be turned off.
     */
    boolean canBeDeactivated() default false;

    String guiFactory() default "";

    M3LMod.CustomProperty[] customProperties() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface InstanceFactory {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Metadata {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Instance {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface EventMarker {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface InitMethod {
        String[] executeAfter() default "";

        String[] executeBefore() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface CustomProperty {
        String key();

        String value();
    }
}
