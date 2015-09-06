/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api;

public class Version implements Comparable<Version> {
	private Integer m_major;
	private Integer m_minor;
	private Integer m_build;
	private String m_tag;
	
	public Version(Integer major, Integer minor, Integer build, String tag) {
		m_major = major;
		m_minor = minor;
		m_build = build;
		m_tag = tag;
	}
	
	public Version(String text) {
		this(null, null, null, null);
		
		// segment the version and the tag
		String[] versionAndTag = text.split(" ");
		String version = versionAndTag[0];
		if (versionAndTag.length > 1) {
			m_tag = versionAndTag[1];
		}
		
		// parse the version numbers
		String[] parts = version.split("\\.");
		
		// try to get the major version
		if (parts.length >= 1) {
			try {
				m_major = Integer.parseInt(parts[0]);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Unable to read major version number from: " + text);
			}
		} else {
			throw new IllegalArgumentException("Could not find major version number from: " + text);
		}
		
		// try to get the minor version
		if (parts.length >= 2) {
			try {
				m_minor = Integer.parseInt(parts[1]);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Unable to read minor version number from: " + text);
			}
		}
		
		// try to get the build number
		if (parts.length >= 3) {
			try {
				m_minor = Integer.parseInt(parts[2]);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Unable to read build number number from: " + text);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(m_major);
		if (m_minor != null) {
			buf.append(".");
			buf.append(m_minor);
		}
		if (m_build != null) {
			buf.append(".");
			buf.append(m_minor);
		}
		if (m_tag != null) {
			buf.append(" ");
			buf.append(m_tag);
		}
		return buf.toString();
	}
	
	@Override
	public int compareTo(Version other) {
		// rules:
		// compare major first, then minor, then build
		// null values are treated as 0
		// tags are not compared
		
		// compare major versions
		int compare = m_major - other.m_major;
		if (compare != 0) {
			return compare;
		}
		
		// compare minor versions
		compare = (m_minor != null ? m_minor : 0) - (other.m_minor != null ? other.m_minor : 0);
		if (compare != 0) {
			return compare;
		}
		
		// compare build numbers
		compare = (m_build != null ? m_build : 0) - (other.m_build != null ? other.m_build : 0);
		return compare;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Version) {
			return equals((Version)other);
		}
		return false;
	}
	
	public boolean equals(Version other) {
		boolean isMajorSame = m_major == other.m_major;
		boolean isMinorSame = (m_minor == null && other.m_minor == null) || (m_minor == other.m_minor);
		boolean isBuildSame = (m_build == null && other.m_build == null) || (m_build == other.m_build);
		return isMajorSame && isMinorSame && isBuildSame;
	}
}
