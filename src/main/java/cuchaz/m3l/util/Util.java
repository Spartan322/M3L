/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import javassist.CtClass;
import javassist.bytecode.Descriptor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.JarFile;

public class Util {

    public static String joinPaths(String a, String b) {
        return new File(new File(a), b).getPath();
    }

    public static int combineHashesOrdered(Object... objs) {
        final int prime = 67;
        int result = 1;
        for (Object obj : objs) {
            result *= prime;
            if (obj != null) {
                result += obj.hashCode();
            }
        }
        return result;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                // just ignore any further exceptions
            }
        }
    }

    public static void closeQuietly(JarFile jarFile) {
        // silly library should implement Closeable...
        if (jarFile != null) {
            try {
                jarFile.close();
            } catch (IOException ex) {
                // just ignore any further exceptions
            }
        }
    }

    public static String getClassDesc(String className) {
        return "L" + Descriptor.toJvmName(className) + ";";
    }

    public static String getClassDesc(Class<?> c) {
        if (c.isArray()) {
            return Descriptor.toJvmName(c.getName());
        } else {
            return "L" + Descriptor.toJvmName(c.getName()) + ";";
        }
    }

    public static List<String> getSignatureArguments(String signature) {
        List<String> out = new ArrayList<String>();

        // for each argument to the method...
        for (int i = 0; i < signature.length(); i++) {
            char c = signature.charAt(i);

            if (c == '(') {
                continue;
            }
            if (c == ')') {
                break;
            }

            int start = i;

            // skip to the end of the array declarations
            while (c == '[') {
                c = signature.charAt(++i);
            }

            // skip to the end of class names
            if (c == 'L') {
                i = signature.indexOf(';', i);
            }

            // add the argument
            out.add(signature.substring(start, i + 1));
        }

        assert (Descriptor.numOfParameters(signature) == out.size());
        return out;
    }

    public static void writeClass(CtClass c, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            c.getClassFile().write(new DataOutputStream(out));
            System.out.println("Wrote class " + c.getName() + " to\n\t" + file.getAbsolutePath());
        } catch (Exception ex) {
            throw new Error(ex);
        } finally {
            closeQuietly(out);
        }
    }

    public static int randRange(Random rand, int lower, int upper) {
        return rand.nextInt((upper - lower) + 1) + lower;
    }
}
