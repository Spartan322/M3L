/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTranslation;

import java.lang.reflect.Field;
import java.util.List;

import javassist.bytecode.Descriptor;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.ClassMapping;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.MethodMapping;

public class ExceptionTranslator {
	
	private Mappings m_mappings;
	private Field m_fieldClassName;
	private Field m_fieldMethodName;
	private Field m_fieldFileName;
	private Field m_fieldLineNumber;
	
	public ExceptionTranslator(Mappings mappings) {
		
		m_mappings = mappings;
		
		try {
			m_fieldClassName = StackTraceElement.class.getDeclaredField("declaringClass");
			m_fieldClassName.setAccessible(true);
			m_fieldMethodName = StackTraceElement.class.getDeclaredField("methodName");
			m_fieldMethodName.setAccessible(true);
			m_fieldFileName = StackTraceElement.class.getDeclaredField("fileName");
			m_fieldFileName.setAccessible(true);
			m_fieldLineNumber = StackTraceElement.class.getDeclaredField("lineNumber");
			m_fieldLineNumber.setAccessible(true);
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
				List<ClassMapping> mappingsChain = m_mappings.getClassMappingChain(obfClassEntry);
				String[] obfClassNames = obfClassEntry.getName().split("\\$");
				StringBuilder buf = new StringBuilder();
				for (int i=0; i<obfClassNames.length; i++) {
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
				// TODO: we could try to use the line number to solve the ambiguity
				// but we don't have a line number index lying around anywhere
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
			m_fieldClassName.set(frame, val);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	
	private void setMethodName(StackTraceElement frame, String val) {
		try {
			m_fieldMethodName.set(frame, val);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	
	private void setFileName(StackTraceElement frame, String val) {
		try {
			m_fieldFileName.set(frame, val);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	
	@SuppressWarnings("unused")
	private void setLineNumber(StackTraceElement frame, int val) {
		try {
			m_fieldLineNumber.set(frame, val);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
}
