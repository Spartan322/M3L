package hr.caellian.upm.common;

import hr.caellian.upm.unitManagement.SIUnit;
import hr.caellian.upm.unitManagement.UnitPrefix;

/**
 * @author Caellian
 */
public class Ampere extends SIUnit {
    public Ampere(double value) {
        super("A", value);
    }

    public Ampere(double value, UnitPrefix prefix) {
        super("A", value, prefix);
    }
}
