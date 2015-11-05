package org.wildfly.extras.creaper.core.online;

import org.wildfly.extras.creaper.core.ManagementVersion;

public final class OnlineCommandContext {
    public final OnlineManagementClient client;
    public final OnlineOptions options; // same as client.options()
    public final ManagementVersion serverVersion; // same as client.serverVersion()

    /** @deprecated use {@link #serverVersion} instead, this will be removed before 1.0 */
    @Deprecated
    public final ManagementVersion currentVersion;

    OnlineCommandContext(OnlineManagementClient client, ManagementVersion serverVersion) {
        this.client = client;
        this.options = client.options();
        this.serverVersion = serverVersion;

        this.currentVersion = serverVersion;
    }
}
