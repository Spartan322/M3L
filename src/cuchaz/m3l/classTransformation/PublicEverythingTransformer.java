/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.InnerClassesAttribute;


public class PublicEverythingTransformer {
	
	public static void transform(CtClass c) {
		
		// make the class public
		c.setModifiers(Modifier.setPublic(c.getModifiers()));
		
		// make inner classes public
		InnerClassesAttribute attribute = (InnerClassesAttribute)c.getClassFile().getAttribute(InnerClassesAttribute.tag);
		if (attribute != null) {
			for (int i=0; i<attribute.tableLength(); i++) {
				attribute.setAccessFlags(i, Modifier.setPublic(attribute.accessFlags(i)));
			}
		}
		
		// make all fields public
		for (CtField field : c.getDeclaredFields()) {
			field.setModifiers(Modifier.setPublic(field.getModifiers()));
		}
		
		// make all behaviors public
		for (CtBehavior behavior : c.getDeclaredBehaviors()) {
			behavior.setModifiers(Modifier.setPublic(behavior.getModifiers()));
		}
	}
}
