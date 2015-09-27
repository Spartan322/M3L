package hr.caellian.upm.components;

import hr.caellian.upm.energy.Energy;

import java.io.Serializable;

/**
 * Conductor is any
 *
 * @author Caellian
 */
public interface Conductor extends Serializable {
    /**
     * @return Energy currently being stored in conductor.
     */
    Energy getCurrentData();
}
