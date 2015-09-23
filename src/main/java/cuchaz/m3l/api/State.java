package cuchaz.m3l.api;

/**
 * @author Caellian
 */
public enum State {
    UNLOADED("Unloaded", "U"),
    LOADED("Loaded", "L"),
    CONSTRUCTED("Constructed", "C"),
    PRE_INITIALIZED("Pre-initialized", "H"),
    INITIALIZED("Initialized", "I"),
    POST_INITIALIZED("Post-initialized", "J"),
    AVAILABLE("Available", "A"),
    DISABLED("Disabled", "D"),
    ERRORED("Errored", "E");

    private String label;
    private String marker;

    State(String label, String marker) {
        this.label = label;
        this.marker = marker;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public String getMarker() {
        return this.marker;
    }
}
