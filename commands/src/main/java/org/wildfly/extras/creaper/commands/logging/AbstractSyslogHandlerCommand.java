package org.wildfly.extras.creaper.commands.logging;

import org.wildfly.extras.creaper.core.offline.OfflineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommand;

public abstract class AbstractSyslogHandlerCommand implements OnlineCommand, OfflineCommand {

    protected final String name;
    protected final String appName;
    protected final Boolean enabled;
    protected final SyslogFacilityType facility;
    protected final String hostname;
    protected final LogLevel level;
    protected final Integer port;
    protected final String serverAddress;
    protected final SyslogFormatType syslogFormat;

    protected AbstractSyslogHandlerCommand(Builder builder) {
        this.name = builder.name;
        this.appName = builder.appName;
        this.enabled = builder.enabled;
        this.facility = builder.facility;
        this.hostname = builder.hostname;
        this.level = builder.level;
        this.port = builder.port;
        this.serverAddress = builder.serverAddress;
        this.syslogFormat = builder.syslogFormat;
    }

    public abstract static class Builder<THIS extends Builder> {

        protected String name;
        protected String appName;
        protected Boolean enabled;
        protected SyslogFacilityType facility;
        protected String hostname;
        protected LogLevel level;
        protected Integer port;
        protected String serverAddress;
        protected SyslogFormatType syslogFormat;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of syslog handler cannot be null.");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of syslog handler cannot be null.");
            }
            this.name = name;
        }

        public final THIS enabled(Boolean enabled) {
            this.enabled = enabled;
            return (THIS) this;
        }

        public final THIS appName(String appName) {
            this.appName = appName;
            return (THIS) this;
        }

        public final THIS facility(SyslogFacilityType facility) {
            this.facility = facility;
            return (THIS) this;
        }

        public final THIS hostname(String hostname) {
            this.hostname = hostname;
            return (THIS) this;
        }

        public final THIS level(LogLevel level) {
            this.level = level;
            return (THIS) this;
        }

        public final THIS port(Integer port) {
            this.port = port;
            return (THIS) this;
        }

        public final THIS serverAddress(String serverAddress) {
            this.serverAddress = serverAddress;
            return (THIS) this;
        }

        public final THIS syslogFormat(SyslogFormatType syslogFormat) {
            this.syslogFormat = syslogFormat;
            return (THIS) this;
        }

        public abstract AbstractSyslogHandlerCommand build();
    }
}
