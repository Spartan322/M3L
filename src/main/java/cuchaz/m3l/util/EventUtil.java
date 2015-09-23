package cuchaz.m3l.util;

import cuchaz.m3l.event.handler.M3LEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * @author Caellian
 */
public class EventUtil {
    public static boolean isEvent(Method method) {
        return method.getParameterTypes()[0].isAssignableFrom(M3LEvent.class);
    }

    public static Optional<Parameter> getEventParameter(Method method) {
        return isEvent(method) ? Optional.of(method.getParameters()[0]) : Optional.<Parameter>empty();
    }
}
