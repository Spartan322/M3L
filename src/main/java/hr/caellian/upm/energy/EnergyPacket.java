package hr.caellian.upm.energy;

import hr.caellian.upm.unitManagement.SIUnit;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Packets used to transfer {@link Energy} trough components.
 *
 * @author Caellian
 */
public class EnergyPacket implements Serializable, Cloneable {
    Energy energyUnit;

    public EnergyPacket(Energy parentEnergy) {
        try {
            this.energyUnit = (Energy) parentEnergy.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public EnergyPacket(Energy parentEnergy, HashMap<String, SIUnit> units) {
        try {
            this.energyUnit = (Energy) parentEnergy.clone();
            this.energyUnit.getPowerUnits().putAll(units);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public Energy getEnergyUnit() {
        return energyUnit;
    }
}