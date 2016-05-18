package org.wildfly.extras.creaper.commands.logging;

import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.offline.OfflineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.OperationException;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

public class AddSyslogHandler extends AbstractSyslogHandlerCommand {

    private final boolean replaceExisting;

    private AddSyslogHandler(Builder builder) {
        super(builder);
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);

        Address handlerAddress = Address.subsystem("logging").and("syslog-handler", name);

        if (replaceExisting) {
            try {
                ops.removeIfExists(handlerAddress);
            } catch (OperationException ex) {
                throw new CommandFailedException("Failed to remove existing syslog-handler " + name, ex);
            }
        }

        ops.add(handlerAddress, Values.empty()
                .andOptional("name", name)
                .andOptional("app-name", appName)
                .andOptional("enabled", enabled)
                .andOptional("facility", facility == null ? null : facility.value())
                .andOptional("hostname", hostname)
                .andOptional("level", level == null ? null : level.value())
                .andOptional("port", port)
                .andOptional("server-address", serverAddress)
                .andOptional("syslog-format", syslogFormat == null ? null : syslogFormat.value()));
    }

    @Override
    public void apply(OfflineCommandContext ctx) throws Exception {
        ctx.client.apply(GroovyXmlTransform.of(AddSyslogHandler.class)
                .subtree("logging", Subtree.subsystem("logging"))
                .parameter("name", name)
                .parameter("appName", appName)
                .parameter("enabled", enabled)
                .parameter("facility", facility == null ? null : facility.value())
                .parameter("hostname", hostname)
                .parameter("level", level == null ? null : level.value())
                .parameter("port", port)
                .parameter("serverAddress", serverAddress)
                .parameter("syslogFormat", syslogFormat == null ? null : syslogFormat.value())
                .parameter("replaceExisting", replaceExisting)
                .build());
    }

    @Override
    public String toString() {
        return "AddSyslogLogHandler{"
                + "name=" + name
                + ", appName=" + appName
                + ", enabled=" + enabled
                + ", facility=" + facility.value()
                + ", hostname=" + hostname
                + ", level=" + level
                + ", port=" + port
                + ", serverAddress=" + serverAddress
                + ", syslogFormat=" + syslogFormat.value()
                + ", replaceExisting=" + replaceExisting + '}';
    }

    public static final class Builder extends AbstractSyslogHandlerCommand.Builder<Builder> {

        private boolean replaceExisting;

        public Builder(String name) {
            super(name);
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        @Override
        public AddSyslogHandler build() {
            if (name == null) {
                throw new IllegalArgumentException("Name of the syslog-handler must not be null");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the syslog-handler must not be empty value");
            }
            return new AddSyslogHandler(this);
        }

    }
}
