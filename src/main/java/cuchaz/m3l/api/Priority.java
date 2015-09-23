/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.api;

/**
 * Event listeners of higher priority will get executed before those of lower priority.
 * Events of higher priority have the ability to raise lower priority event listeners during topological sorting in
 * case they depend on them.
 * This is used to suggest approximate execution priority as an event listener with lowest
 * priority might get executed before an event listener with highest priority in case said event listener (with highest
 * priority) depends on it (event listener with lowest priority).
 *
 * @author Caellian
 */
public enum Priority {
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST
}
