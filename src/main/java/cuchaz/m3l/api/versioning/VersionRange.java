/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.api.versioning;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Caellian
 */
public class VersionRange {

    public static final VersionRange EMPTY = new VersionRange(Version.EMPTY);

    final Version preferredVersion;
    final Optional<Version> maxVersion;
    final Optional<Version> minVersion;
    final Optional<List<Version>> supportedVersions;

    private boolean rangedMode = true;

    public VersionRange(Version preferredVersion, Version... acceptedVersions) {
        this.preferredVersion = preferredVersion;
        this.maxVersion = Optional.empty();
        this.minVersion = Optional.empty();
        this.supportedVersions = Optional.of(Arrays.asList(acceptedVersions));
        this.rangedMode = false;
    }

    public VersionRange(Version preferredVersion, Optional<Version> maxVersion, Optional<Version> minVersion) {
        this.preferredVersion = preferredVersion;
        this.maxVersion = maxVersion;
        this.minVersion = minVersion;
        this.supportedVersions = Optional.empty();
    }

    Version getPreferredVersion() {
        return preferredVersion;
    }

    Version getMinVersion() {
        return minVersion.isPresent() && minVersion != Optional.<Version>empty() ? minVersion.get() : getPreferredVersion();
    }

    Version getMaxVersion() {
        return maxVersion.isPresent() && maxVersion != Optional.<Version>empty() ? maxVersion.get() : getPreferredVersion();
    }

    public boolean isInRange(Version check) {
        if (rangedMode) {
            return check.compareTo(maxVersion.get()) <= 0 && check.compareTo(minVersion.get()) >= 0;
        } else {
            for (Version version : supportedVersions.get()) {
                if (check.compareTo(version) == 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
