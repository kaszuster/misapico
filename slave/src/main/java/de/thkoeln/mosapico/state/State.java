package de.thkoeln.mosapico.state;

/**
 * Created by szuster on 23.08.2015.
 */
public enum State {
    IDLE("IDLE"),
    ANALYSE("ANALYSE"),
    BUILD("BUILD");

    private String value;

    State(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
