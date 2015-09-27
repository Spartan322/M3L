package hr.caellian.upm.common;

import hr.caellian.upm.unitManagement.SIUnit;
import hr.caellian.upm.unitManagement.UnitPrefix;

/**
 * @author Caellian
 */
public class HeatCapacity extends SIUnit {
    public HeatCapacity(double value) {
        super("J/K", value);
    }

    public HeatCapacity(double value, UnitPrefix prefix) {
        super("J/K", value, prefix);
    }
}
