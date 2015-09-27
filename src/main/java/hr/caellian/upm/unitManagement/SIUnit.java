package hr.caellian.upm.unitManagement;

/**
 * @author Caellian
 */
public class SIUnit {
    protected double value;
    protected UnitPrefix prefix;
    protected String symbol;

    public SIUnit(String symbol, double value) {
        this.symbol = symbol;
        this.value = value;
        this.prefix = UnitPrefix.none;
    }

    public SIUnit(String symbol, double value, UnitPrefix prefix) {
        this.symbol = symbol;
        this.value = value;
        this.prefix = prefix;
    }

    public double baseValue() {
        return prefix.getUnitMultiplier() * value;
    }

    double getValue() {
        updateValue();
        return value;
    }

    public void updateValue() {
        if (Math.abs(value) < 0 || Math.abs(value) >= 10) {
            if (value > 0) {
                if (value >= 10) {
                    double newValue = value % 10;
                    int leftExponent = (int) (value / 10);
                    UnitPrefix modified = UnitPrefix.getModified(leftExponent, prefix);
                    if (modified != null) {
                        value = newValue;
                        prefix = modified;
                    }
                }
            } else if (value < 0) {
                if (value <= -10) {
                    double newValue = value % 10;
                    int leftExponent = (int) (value / 10);
                    UnitPrefix modified = UnitPrefix.getModified(leftExponent, prefix);
                    if (modified != null) {
                        value = newValue;
                        prefix = modified;
                    }
                }
            } else {
                prefix = UnitPrefix.none;
            }
        }
    }

    int setValue(int value) {
        this.value = value;
        return value;
    }

    UnitPrefix getPrefix() {
        updateValue();
        return prefix;
    }

    UnitPrefix setPrefix(UnitPrefix prefix) {
        this.prefix = prefix;
        return prefix;
    }
}
