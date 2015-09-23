/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.event.handler;

import com.google.common.base.Preconditions;
import cuchaz.m3l.api.Priority;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Caellian
 */
public class M3LEvent {

    private static ListenerNodeList listeners = new ListenerNodeList();
    private boolean hasResult = false;
    private Result result = Result.DEFAULT;
    private Optional<Priority> priority = Optional.empty();

    public M3LEvent() {
        hasResult = this.getClass().isAnnotationPresent(HasResult.class);
        setup();
    }

    /**
     * Determines if this event expects a significant result value.
     * <p/>
     * Note:
     * Events with the HasResult annotation will have this method automatically added to return true.
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * Returns the value set as the result of this event
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the result value for this event, not all events can have a result set, and any attempt to
     * set a result for a event that isn't expecting it will result in a {@link IllegalArgumentException}.
     * <p/>
     * The functionality of setting the result is defined on a per-event bases.
     *
     * @param value The new result
     */
    public void setResult(Result value) {
        result = value;
    }

    /**
     * Called by the base constructor, this is used by ASM generated
     * event classes to setup various functionality such as the listener list.
     */
    protected void setup() {
    }

    /**
     * Returns a {@link ListenerNodeList} object that contains all listeners
     * that are registered to this event.
     *
     * @return {@link ListenerNodeList}
     */
    public ListenerNodeList getListenerList() {
        return listeners;
    }

    /**
     * @return event {@link Priority}
     */
    public Optional<Priority> getPriority() {
        return this.priority;
    }

    /**
     * Sets the event {@link Priority}. This method can only raise event priority and can't lower it.
     *
     * @param value new event {@link Priority}
     */
    public void setPriority(Priority value) {
        Preconditions.checkArgument(value != null, "setPhase argument must not be null");
        int prev = priority == null ? -1 : priority.get().ordinal();
        Preconditions.checkArgument(prev < value.ordinal(), "Attempted to set event priority to %s when already %s", value, priority);
        priority = Optional.of(value);
    }

    public enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }

    @Retention(value = RUNTIME)
    @Target(value = TYPE)
    public @interface HasResult {
    }
}
