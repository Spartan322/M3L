package cuchaz.m3l.event.state;

import cuchaz.m3l.api.State;

/**
 * @author Caellian
 */
public class M3LInitializationEvent extends M3LStateEvent {
    @Override
    public State getState() {
        return State.INITIALIZED;
    }
}
