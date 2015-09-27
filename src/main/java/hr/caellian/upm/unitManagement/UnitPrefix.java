package hr.caellian.upm.unitManagement;

/**
 * @author Caellian
 */
public enum UnitPrefix {
    yocto("y", exp(-24)),
    zepto("z", exp(-21)),
    atto("a", exp(-18)),
    femto("f", exp(-15)),
    pico("p", exp(-12)),
    nano("n", exp(-9)),
    //Using a mu symbol for micro is not a good idea
    micro("u", exp(-6)),
    milli("m", exp(-3)),
    centi("c", exp(-2)),
    deci("d", exp(-1)),
    none("", exp(0)),
    deca("da", exp(1)),
    hecto("h", exp(2)),
    kilo("k", exp(3)),
    mega("M", exp(6)),
    giga("G", exp(9)),
    tera("T", exp(12)),
    peta("P", exp(15)),
    exa("E", exp(18)),
    zetta("Z", exp(21)),
    yotta("Y", exp(24));

    private final double unitMultiplier;
    private final String name;

    UnitPrefix(String name, double unitMultiplier) {
        this.unitMultiplier = unitMultiplier;
        this.name = name;
    }

    static double exp(int pow) {
        return Math.pow(10, pow);
    }

    static UnitPrefix getModified(int pow, UnitPrefix currentPrefix) {
        for (UnitPrefix it : values()) {
            if (it.unitMultiplier == currentPrefix.unitMultiplier + pow) {
                return it;
            }
        }
        return null;
    }

    double getUnitMultiplier() {
        return unitMultiplier;
    }

    String getSymbol() {
        return this.name;
    }
}