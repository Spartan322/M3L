package hr.caellian.upm.energy;

/**
 * @author Caellian
 */
public class ConversionManager {
    /**
     * This method allows conversion of energy between different mods.
     *
     * @param from           Energy that is going to be converted
     * @param to             Energy energy that the input energy will be converted into
     * @param additionalData Additional data that might be required for energy conversion
     * @return Energy packet converted from input energy type. This should not be null, but an empty energy packet
     * instead.
     */
    public static EnergyPacket convertEnergy(EnergyPacket from, EnergyPacket to, Object... additionalData) {
        return to.getEnergyUnit().fromPower(from.getEnergyUnit().toPower(), additionalData);
    }
}