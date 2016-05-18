package org.wildfly.extras.creaper.commands.logging;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.CliException;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.OperationException;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

@RunWith(Arquillian.class)
public class AddSyslogHandlerOnlineTest {

    private static final String TEST_HANDLER_NAME = "syslog-handler";
    private static final Address TEST_HANDLER_ADDRESS = Address.subsystem("logging")
            .and("syslog-handler", TEST_HANDLER_NAME);

    private OnlineManagementClient client;
    private Operations ops;
    private Administration administration;

    @Before
    public void connect() throws IOException {
        client = ManagementClient.online(OnlineOptions.standalone().localDefault().build());
        ops = new Operations(client);
        administration = new Administration(client);
    }

    @After
    public void cleanup() throws IOException, CliException, OperationException, TimeoutException, InterruptedException {
        try {
            ops.removeIfExists(TEST_HANDLER_ADDRESS);
            administration.reloadIfRequired();
        } finally {
            client.close();
        }
    }

    @Test
    public void addSyslogHandler() throws Exception {
        AddSyslogHandler addSyslogHandler = new AddSyslogHandler.Builder(TEST_HANDLER_NAME)
                .appName("logging-app")
                .enabled(false)
                .facility(SyslogFacilityType.MAIL_SYSTEM)
                .hostname("hostname")
                .level(LogLevel.INFO)
                .port(9898)
                .serverAddress("127.0.0.1")
                .syslogFormat(SyslogFormatType.RFC3164)
                .build();

        client.apply(addSyslogHandler);
        assertTrue("syslog handler should be created", ops.exists(TEST_HANDLER_ADDRESS));

        ModelNodeResult result = ops.readAttribute(TEST_HANDLER_ADDRESS, "app-name");
        result.assertSuccess();
        assertEquals("app-name should be changed", "logging-app", result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "enabled");
        result.assertSuccess();
        assertFalse("Enabled should be changed", result.booleanValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "facility");
        result.assertSuccess();
        assertEquals("Facility should be changed", "mail-system", result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "hostname");
        result.assertSuccess();
        assertEquals("Hostname should be changed", "hostname", result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "level");
        result.assertSuccess();
        assertEquals("Level should be changed", LogLevel.INFO.value(), result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "port");
        result.assertSuccess();
        assertEquals("Port should be changed", "9898", result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "server-address");
        result.assertSuccess();
        assertEquals("Filter-spec should be changed", "127.0.0.1", result.stringValue());

        result = ops.readAttribute(TEST_HANDLER_ADDRESS, "syslog-format");
        result.assertSuccess();
        assertEquals("Syslog-format should be changed", SyslogFormatType.RFC3164.value(), result.stringValue());
    }

    @Test(expected = CommandFailedException.class)
    public void replaceExistingNotAllowed() throws Exception {
        AddSyslogHandler addSyslogHandler = new AddSyslogHandler.Builder(TEST_HANDLER_NAME)
                .level(LogLevel.WARN)
                .build();

        AddSyslogHandler addSyslogHandler2 = new AddSyslogHandler.Builder(TEST_HANDLER_NAME)
                .level(LogLevel.ALL)
                .build();

        client.apply(addSyslogHandler);
        assertTrue("Syslog handler should be created", ops.exists(TEST_HANDLER_ADDRESS));

        client.apply(addSyslogHandler2);
        fail("Syslog handler already exists in configuration, an exception should be thrown");
    }

    @Test
    public void replaceExistingAllowed() throws Exception {
        AddSyslogHandler addSyslogHandler = new AddSyslogHandler.Builder(TEST_HANDLER_NAME)
                .level(LogLevel.FATAL)
                .replaceExisting()
                .build();

        AddSyslogHandler addSyslogHandler2 = new AddSyslogHandler.Builder(TEST_HANDLER_NAME)
                .level(LogLevel.WARN)
                .replaceExisting()
                .build();

        client.apply(addSyslogHandler);
        assertTrue("The first syslog handler should be created even if it is being created with \"replaceExisting\" "
                + "and the same syslog handler does not exist", ops.exists(TEST_HANDLER_ADDRESS));

        client.apply(addSyslogHandler2);
        assertTrue("Syslog handler should be created", ops.exists(TEST_HANDLER_ADDRESS));

        ModelNodeResult result = ops.readAttribute(TEST_HANDLER_ADDRESS, "level");
        result.assertSuccess();
        assertEquals("Level should be changed", LogLevel.WARN.value(), result.stringValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogHandlerNullName() throws Exception {
        AddSyslogHandler addSyslogHandler = new AddSyslogHandler.Builder(null)
                .build();

        client.apply(addSyslogHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogHandlerEmptyName() throws Exception {
        AddSyslogHandler addSyslogHandler = new AddSyslogHandler.Builder("")
                .build();

        client.apply(addSyslogHandler);
    }
}
