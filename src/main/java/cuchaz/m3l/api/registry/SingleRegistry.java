/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.api.registry;


import java.util.Optional;

public class SingleRegistry<T> {

    private Optional<T> val;

    public SingleRegistry() {
        val = Optional.empty();
    }

    public boolean isRegistered() {
        return val.isPresent() && val != Optional.<T>empty();
    }

    public void register(T val) throws AlreadyRegisteredException {
        if (isRegistered()) {
            throw new AlreadyRegisteredException();
        }
        this.val = Optional.of(val);
    }

    public Optional<T> get() {
        return val;
    }
}
