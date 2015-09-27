package hr.caellian.upm.components;

import java.io.Serializable;

/**
 * Components are conceptual abstractions representing idealized electronic components.
 *
 * @author Caellian on 13.7.2015., at 2:00.
 */
public interface Component extends Cloneable, Serializable {
    /**
     * @return List of all terminals
     */
    ElectricalTerminal[] getElectricalTerminals();

    /**
     * @param world World this {@link Component} is located in
     * @return True if update has be successfully handled.
     */
    boolean handleUpdate(Object world);
}
