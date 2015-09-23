/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.api.versioning;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author Caellian
 */
public class ArtifactData implements Comparable<ArtifactData> {

    @SerializedName("label")
    protected final String m_label;

    @SerializedName("version")
    protected final Version m_version;

    public ArtifactData(String label, Version version) {
        m_label = label;
        m_version = version;
    }

    public String getLabel() {
        return m_label;
    }

    public Version getVersion() {
        return m_version;
    }

    @Override
    public int compareTo(ArtifactData o) {
        return getVersion().compareTo(o.getVersion());
    }

    public static class ArtifactTypeAdapter extends TypeAdapter<ArtifactData> {
        @Override
        public void write(JsonWriter out, ArtifactData value) throws IOException {

        }

        @Override
        public ArtifactData read(JsonReader in) throws IOException {
            try {
                return new ArtifactData(in.nextString(), Version.fromString(in.nextString()));
            } catch (InvalidVersionFormatException e) {
                return new ArtifactData("", Version.EMPTY);
            }
        }
    }
}
