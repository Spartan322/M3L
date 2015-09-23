/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTranslation;

import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.ClassMapping;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.MethodMapping;
import javassist.bytecode.Descriptor;

import java.lang.reflect.Field;
import java.util.List;

public class ExceptionTranslator {

    private Mappings mappings;
    private Field fieldClassName;
    private Field fieldMethodName;
    private Field fieldFileName;
    private Field fieldLineNumber;

    public ExceptionTranslator(Mappings mappings) {

        this.mappings = mappings;

        try {
            fieldClassName = StackTraceElement.class.getDeclaredField("declaringClass");
            fieldClassName.setAccessible(true);
            fieldMethodName = StackTraceElement.class.getDeclaredField("methodName");
            fieldMethodName.setAccessible(true);
            fieldFileName = StackTraceElement.class.getDeclaredField("fileName");
            fieldFileName.setAccessible(true);
            fieldLineNumber = StackTraceElement.class.getDeclaredField("lineNumber");
            fieldLineNumber.setAccessible(true);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public void translate(Throwable in) {
        Throwable t = in;
        while (t != null) {
            for (StackTraceElement frame : t.getStackTrace()) {

                // get the obf class (deal with packages as necessary)
                ClassEntry obfClassEntry = new ClassEntry(Descriptor.toJvmName(frame.getClassName()));
                if (obfClassEntry.isInDefaultPackage()) {
                    obfClassEntry = new ClassEntry(cuchaz.enigma.Constants.NonePackage + "/" + obfClassEntry.getName());
                }

                // translate the class name
                List<ClassMapping> mappingsChain = mappings.getClassMappingChain(obfClassEntry);
                String[] obfClassNames = obfClassEntry.getName().split("\\$");
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < obfClassNames.length; i++) {
                    boolean isFirstClass = buf.length() == 0;
                    String className = obfClassNames[i];
                    ClassMapping classMapping = mappingsChain.get(i);
                    if (classMapping != null) {
                        className = classMapping.getDeobfName();
                    }
                    if (!isFirstClass) {
                        buf.append("$");
                    }
                    buf.append(className);
                }
                ClassEntry deobfClassEntry = new ClassEntry(buf.toString());
                setClassName(frame, Descriptor.toJavaName(deobfClassEntry.getName()));
                setFileName(frame, deobfClassEntry.getSimpleName() + ".java");

                // try to translate the method name
                // we only get the method name and not the signature, so we might match more than one method
                // TODO: Try to use the line number to solve the ambiguity (line number unavailable)
                // SOLVE: Maybe adding an integer increased every line iteration and use it?
                ClassMapping classMapping = mappingsChain.get(mappingsChain.size() - 1);
                if (classMapping != null) {
                    buf = new StringBuilder();
                    for (MethodMapping methodMapping : classMapping.methods()) {
                        if (methodMapping.getObfName().equals(frame.getMethodName())) {
                            if (buf.length() > 0) {
                                buf.append("|");
                            }
                            if (methodMapping.getDeobfName() != null) {
                                buf.append(methodMapping.getDeobfName());
                            } else {
                                buf.append(methodMapping.getObfName());
                            }
                        }
                    }
                    if (buf.length() > 0) {
                        setMethodName(frame, buf.toString());
                    }
                }
            }

            // recurse
            t = t.getCause();
        }
    }

    private void setClassName(StackTraceElement frame, String val) {
        try {
            fieldClassName.set(frame, val);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private void setMethodName(StackTraceElement frame, String val) {
        try {
            fieldMethodName.set(frame, val);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private void setFileName(StackTraceElement frame, String val) {
        try {
            fieldFileName.set(frame, val);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    @SuppressWarnings("unused")
    private void setLineNumber(StackTraceElement frame, int val) {
        try {
            fieldLineNumber.set(frame, val);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
}
