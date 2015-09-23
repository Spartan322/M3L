/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package hr.caellian.m3l.util.topologicalSorting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Caellian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE})
public @interface HashDependency {
    int[] executeAfter() default 0;

    int[] executeBefore() default 0;
}
