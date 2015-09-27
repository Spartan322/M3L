package hr.caellian.upm.components;

import java.io.Serializable;

/**
 * Enumeration helping to determine if a {@link ElectricalTerminal} is an input or output terminal.
 *
 * @author Caellian
 */
public enum TerminalType implements Serializable {
    INPUT,
    OUTPUT,
    BOTH
}
