package hr.caellian.upm.energy;

import hr.caellian.upm.common.Power;
import hr.caellian.upm.unitManagement.SIUnit;

import java.io.Serializable;
import java.util.HashMap;

/**
 * {@link Energy} defines a new energy type.
 *
 * @author Caellian
 */
public abstract class Energy implements Cloneable, Serializable {
    /**
     * Units used to define this mods power system.
     * Some mods with more complex power systems might require multiple energy defining units.
     */
    protected HashMap<String, SIUnit> powerUnits = new HashMap<>();

    /**
     * Used to display energy unit.
     * Example: 'EU'
     */
    protected String energyDisplay;

    /**
     * Used to display full energy name.
     * Example: 'Energy Unit'
     */
    protected String energyName;

    protected IEnergyManager energyManager;

    public Energy(HashMap<String, SIUnit> powerUnits, String energyDisplay, String energyName, IEnergyManager energyManager) {
        this.powerUnits = powerUnits;
        this.energyDisplay = energyDisplay;
        this.energyName = energyName;
        this.energyManager = energyManager;
    }

    /**
     * @param key Used to specify {@link SIUnit}
     * @return SIUnit bound to key argument
     */
    SIUnit getUnit(String key) {
        return powerUnits.get(key);
    }

    /**
     * @return {@link HashMap} containing all units used by this energy source
     */
    public HashMap<String, SIUnit> getPowerUnits() {
        return powerUnits;
    }

    /**
     * @return String used to display this energy unit
     */
    public String getEnergyDisplay() {
        return energyDisplay;
    }

    /**
     * @return String used to display this energy units full name
     */
    public String getEnergyName() {
        return energyName;
    }

    /**
     * @return Energy manager of this energy type
     */
    public IEnergyManager getEnergyManager() {
        return energyManager;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * This method is used to get Power from this energy packet.
     *
     * @return Power conversion multiplier
     */
    abstract public Power toPower();

    /**
     * @param power Power input into conversion
     * @param data  Additional data needed for energy conversion
     * @return {@link EnergyPacket} from received power and data
     */
    abstract public EnergyPacket fromPower(Power power, Object... data);
}
