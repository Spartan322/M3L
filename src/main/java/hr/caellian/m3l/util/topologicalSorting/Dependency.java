package hr.caellian.m3l.util.topologicalSorting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Caellian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Dependency {
    String[] executeAfter() default "";

    String[] executeBefore() default "";
}
