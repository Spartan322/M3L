package hr.caellian.upm.components;

import java.io.Serializable;

/**
 * Electrical terminals are points at which a conductor form an electrical component, device or network comes to an end
 * and provides point of connection to external circuits.
 *
 * @author Caellian
 */
public interface ElectricalTerminal extends Conductor, Serializable, Cloneable {
    /**
     * @return True if this terminal is connected to an Conductor
     */
    boolean isConnected();

    /**
     * @return Conductor this terminal is connected to. Returns null if {@code #isConnected() == false}
     */
    Conductor isConnectedTo();

    /**
     * @return Component this Terminal is a part of
     */
    Component isBoundTo();

    /**
     * @return Type if this terminal
     */
    TerminalType getTerminalType();
}
