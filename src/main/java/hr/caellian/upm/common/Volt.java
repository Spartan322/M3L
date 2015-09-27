package hr.caellian.upm.common;

import hr.caellian.upm.unitManagement.SIUnit;
import hr.caellian.upm.unitManagement.UnitPrefix;

/**
 * @author Caellian
 */
public class Volt extends SIUnit {
    public Volt(double value) {
        super("V", value);
    }

    public Volt(double value, UnitPrefix prefix) {
        super("V", value, prefix);
    }
}
