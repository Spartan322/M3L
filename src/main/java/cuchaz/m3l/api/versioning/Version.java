/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api.versioning;

import com.google.gson.annotations.SerializedName;

//TODO: Make a more flexible (but less useful) variant
public class Version implements Comparable<Version>, Cloneable {
    public static final Version EMPTY = new Version(0, 0, 0);
    @SerializedName("major")
    public final short major;
    @SerializedName("minor")
    public final short minor;
    @SerializedName("patch")
    public final short patch;

    public Version(short major, short minor, short patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(int major, int minor, int patch) {
        this((short) major, (short) minor, (short) patch);
    }

    public Version(String version) throws InvalidVersionFormatException {
        String[] versionNumbers = version.split("\\.");

        if (versionNumbers.length != 3) {
            throw new InvalidVersionFormatException(String.format("%s is not a valid version format. Use Semantic versioning!", version));
        }

        this.major = Short.parseShort(versionNumbers[0]);
        this.minor = Short.parseShort(versionNumbers[1]);
        this.patch = Short.parseShort(versionNumbers[2]);
    }

    public static Version fromString(String version) throws InvalidVersionFormatException {
        return new Version(version);
    }

    public static Version[] fromStringArray(String[] versions) throws InvalidVersionFormatException {
        Version[] result = new Version[versions.length];

        for (int ver = 0; ver < versions.length; ver++) {
            result[ver] = fromString(versions[ver]);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }

        Version version = (Version) o;

        return major == version.major && minor == version.minor && patch == version.patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int compareTo(Version o) {
        if (this.major == o.major) {
            if (this.minor == o.minor) {
                return this.patch - o.patch;
            } else {
                return this.minor - o.minor;
            }
        } else {
            return this.major - o.major;
        }
    }

    @Override
    public int hashCode() {
        int result = (int) major;
        result = 31 * result + (int) minor;
        result = 31 * result + (int) patch;
        return result;
    }

    public boolean equals(Version other) {
        return this == other || other != null && major == other.major && minor == other.minor && patch == other.patch;
    }
}
