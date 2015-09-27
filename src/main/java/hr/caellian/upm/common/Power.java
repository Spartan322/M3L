package hr.caellian.upm.common;

import hr.caellian.upm.unitManagement.SIUnit;
import hr.caellian.upm.unitManagement.UnitPrefix;

/**
 * In physics, power is the rate of doing work. It is equivalent to an amount of energy consumed per unit time. In the
 * SI system, the unit of power is the joule per second (J/s), known as the watt in honour of James Watt, the
 * eighteenth-century developer of the steam engine.
 *
 * @author Caellian
 */
public class Power extends SIUnit {
    public Power(double value) {
        super("W", value);
    }

    public Power(int value, UnitPrefix prefix) {
        super("W", value, prefix);
    }
}
