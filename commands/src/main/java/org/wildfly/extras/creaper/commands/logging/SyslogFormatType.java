package org.wildfly.extras.creaper.commands.logging;

public enum SyslogFormatType {

    RFC5424("RFC5424"),
    RFC3164("RFC3164");

    private final String value;

    SyslogFormatType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
