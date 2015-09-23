/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.mod;

import com.google.common.collect.ImmutableMap;
import cuchaz.m3l.api.versioning.ArtifactData;

import java.io.File;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

/**
 * @author Caellian
 */
public interface ModContainer {
    Map<String, String> EMPTY_PROPERTIES = ImmutableMap.of();

    String getModId();

    String getName();

    String getVersion();

    File getSource();

    ModMetadata getMetadata();

    void setEnabledState(boolean var1);

    List<ArtifactData> getRequirements();

    List<ArtifactData> getDependencies();

    List<ArtifactData> getDependants();

    String getSortingRules();

//    boolean registerBus(EventBus var1, LoadController var2);

    boolean matches(Object var1);

    Object getMod();

    ArtifactData getProcessedVersion();

    boolean isImmutable();

    String getDisplayVersion();

    String[] acceptedMinecraftVersions();

    Certificate getSigningCertificate();

    Map<String, String> getCustomModProperties();

    Class<?> getCustomResourcePackClass();

    Map<String, String> getSharedModDescriptor();

    Disableable canBeDisabled();

    String getGuiClassName();

    List<String> getOwnedPackages();

    boolean shouldLoadInEnvironment();

    enum Disableable {
        YES,
        RESTART,
        NEVER,
        DEPENDENCIES
    }
}
