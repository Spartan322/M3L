/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import java.util.ArrayList;
import java.util.Arrays;

public class ArgumentBuilder {
    ArrayList<String> args = new ArrayList<String>(0);

    public boolean contains(String name) {
        return args.contains(name);
    }

    public void add(String... values) {
        args.addAll(Arrays.asList(values));
    }

    public void add(String[] args, int start, int end) {
        add(Arrays.copyOfRange(args, start, end));
    }

    public String[] build() {
        return args.toArray(new String[args.size()]);
    }
}
