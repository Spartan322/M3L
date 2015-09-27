package hr.caellian.upm.common;

import hr.caellian.upm.energy.Energy;
import hr.caellian.upm.energy.EnergyPacket;
import hr.caellian.upm.energy.IEnergyManager;
import hr.caellian.upm.unitManagement.SIUnit;

import java.util.HashMap;

/**
 * @author Caellian
 */
public class ElectricPower extends Energy {
    public static final String AMPERE = "Ampere";
    public static final String VOLT = "Volt";

    public ElectricPower(IEnergyManager energyManager) {
        super(getUnits(), "W", "Electric Power", energyManager);
    }

    public static HashMap<String, SIUnit> getUnits() {
        HashMap<String, SIUnit> powerUnits = new HashMap<>(0);
        powerUnits.put(AMPERE, new Ampere(0));
        powerUnits.put(VOLT, new Volt(0));
        return powerUnits;
    }

    @Override
    public Power toPower() {
        return new Power(this.getPowerUnits().get(AMPERE).baseValue() * this.getPowerUnits().get(VOLT).baseValue());
    }

    @Override
    public EnergyPacket fromPower(Power power, Object... data) {
        if (data != null) {
            HashMap<String, SIUnit> extra = new HashMap<>(0);

            int init = 0;
            for (Object it : data) {
                if (init == 2) {
                    break;
                } else if (it instanceof Ampere) {
                    extra.put(AMPERE, (Ampere) it);
                    init++;
                } else if (it instanceof Volt) {
                    extra.put(VOLT, (Volt) it);
                    init++;
                }
            }

            return new EnergyPacket(this, extra);
        } else {
            return new EnergyPacket(this);
        }
    }
}
