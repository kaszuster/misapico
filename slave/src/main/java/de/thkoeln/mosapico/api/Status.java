package de.thkoeln.mosapico.api;

/**
 * Created by szuster on 23.08.2015.
 */
public enum Status {
    IDLE("IDLE"),
    BUSY("BUSY");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
