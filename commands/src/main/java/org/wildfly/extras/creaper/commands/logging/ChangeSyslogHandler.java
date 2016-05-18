package org.wildfly.extras.creaper.commands.logging;

import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;

public class ChangeSyslogHandler extends AbstractSyslogHandlerCommand {

    private ChangeSyslogHandler(Builder builder) {
        super(builder);
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);

        Address handlerAddress = Address.subsystem("logging").and("syslog-handler", name);

        if (!ops.exists(handlerAddress)) {
            throw new IllegalStateException(String.format("sylog handler %s does not exist.", name));
        }

        Batch batch = new Batch();
        if (appName != null) {
            batch.writeAttribute(handlerAddress, "app-name", appName);
        }
        if (enabled != null) {
            batch.writeAttribute(handlerAddress, "enabled", enabled);
        }
        if (facility != null) {
            ops.writeAttribute(handlerAddress, "facility", facility.value());
        }
        if (hostname != null) {
            batch.writeAttribute(handlerAddress, "hostname", hostname);
        }
        if (level != null) {
            batch.writeAttribute(handlerAddress, "level", level.value());
        }
        if (port != null) {
            batch.writeAttribute(handlerAddress, "port", port);
        }
        if (serverAddress != null) {
            batch.writeAttribute(handlerAddress, "server-address", serverAddress);
        }
        if (syslogFormat != null) {
            batch.writeAttribute(handlerAddress, "syslog-format", syslogFormat.value());
        }

        ops.batch(batch);
    }

    @Override
    public void apply(OfflineCommandContext ctx) throws Exception {
        ctx.client.apply(GroovyXmlTransform.of(ChangeSyslogHandler.class)
                .subtree("logging", Subtree.subsystem("logging"))
                .parameter("name", name)
                .parameter("appName", appName)
                .parameter("enabled", String.valueOf(enabled))
                .parameter("facility", facility == null ? null : facility.value())
                .parameter("hostname", hostname)
                .parameter("level", level == null ? null : level.value())
                .parameter("port", String.valueOf(port))
                .parameter("serverAddress", serverAddress)
                .parameter("syslogFormat", syslogFormat == null ? null : syslogFormat.value())
                .build());
    }

    public static final class Builder extends AbstractSyslogHandlerCommand.Builder<Builder> {

        public Builder(String name) {
            super(name);
        }

        @Override
        public ChangeSyslogHandler build() {
            if (name == null) {
                throw new IllegalArgumentException("Name of the syslog-handler must not be null");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the syslog-handler must not be empty value");
            }

            return new ChangeSyslogHandler(this);
        }

    }
}
