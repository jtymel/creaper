package org.wildfly.extras.creaper.commands.logging;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineOptions;

import java.io.File;

import static org.wildfly.extras.creaper.XmlAssert.assertXmlIdentical;
import static org.wildfly.extras.creaper.commands.logging.LogHandlerType.SYSLOG;

public class RemoveLogHandlerOfflineTest {
    private static final String HANDLER_ORIGINAL = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <console-handler name=\"console\" autoflush=\"false\" enabled=\"false\">\n"
            + "                <level name=\"FINEST\"/>\n"
            + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
            + "                <encoding value=\"UTF-8\"/>\n"
            + "                <formatter>\n"
            + "                    <named-formatter name=\"PATTERN\"/>\n"
            + "                </formatter>\n"
            + "                <target name=\"System.out\"/>\n"
            + "            </console-handler>\n"
            + "            <periodic-rotating-file-handler name=\"periodic\" autoflush=\"false\" enabled=\"false\">\n"
            + "                <level name=\"FINEST\"/>\n"
            + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
            + "                <encoding value=\"UTF-8\"/>\n"
            + "                <formatter>\n"
            + "                    <named-formatter name=\"PATTERN\"/>\n"
            + "                </formatter>\n"
            + "                <file relative-to=\"jboss.server.log.dir\" path=\"server.log\"/>\n"
            + "                <suffix value=\".suffix\"/>\n"
            + "                <append value=\"true\"/>\n"
            + "            </periodic-rotating-file-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String HANDLER_REMOVE_CONSOLE_EXPECTED = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <periodic-rotating-file-handler name=\"periodic\" autoflush=\"false\" enabled=\"false\">\n"
            + "                <level name=\"FINEST\"/>\n"
            + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
            + "                <encoding value=\"UTF-8\"/>\n"
            + "                <formatter>\n"
            + "                    <named-formatter name=\"PATTERN\"/>\n"
            + "                </formatter>\n"
            + "                <file relative-to=\"jboss.server.log.dir\" path=\"server.log\"/>\n"
            + "                <suffix value=\".suffix\"/>\n"
            + "                <append value=\"true\"/>\n"
            + "            </periodic-rotating-file-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String HANDLER_REMOVE_PERIODIC_EXPECTED = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <console-handler name=\"console\" autoflush=\"false\" enabled=\"false\">\n"
            + "                <level name=\"FINEST\"/>\n"
            + "                <filter-spec value=\"match(&quot;filter*&quot;)\"/>"
            + "                <encoding value=\"UTF-8\"/>\n"
            + "                <formatter>\n"
            + "                    <named-formatter name=\"PATTERN\"/>\n"
            + "                </formatter>\n"
            + "                <target name=\"System.out\"/>\n"
            + "            </console-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SYSLOG_HANDLER = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <syslog-handler name=\"syslog\">\n"
            + "                <level name=\"WARN\"/>\n"
            + "                <server-address value=\"127.0.0.1\"/>\n"
            + "                <port value=\"9899\"/>\n"
            + "                <app-name value=\"java\"/>\n"
            + "                <formatter>\n"
            + "                    <syslog-format syslog-type=\"RFC5424\"/>\n"
            + "                </formatter>\n"
            + "            </syslog-handler>"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String NO_HANDLERS = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\"/>\n"
            + "    </profile>\n"
            + "</server>";

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void setUp() {
        XMLUnit.setNormalizeWhitespace(true);
    }

    @Test
    public void removeConsole() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");

        Files.write(HANDLER_ORIGINAL, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        assertXmlIdentical(HANDLER_ORIGINAL, Files.toString(cfg, Charsets.UTF_8));

        client.apply(Logging.handler().console().remove("console"));

        assertXmlIdentical(HANDLER_REMOVE_CONSOLE_EXPECTED, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void removePeriodic() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");

        Files.write(HANDLER_ORIGINAL, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        assertXmlIdentical(HANDLER_ORIGINAL, Files.toString(cfg, Charsets.UTF_8));

        client.apply(Logging.handler().periodicRotatingFile().remove("periodic"));

        assertXmlIdentical(HANDLER_REMOVE_PERIODIC_EXPECTED, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test(expected = CommandFailedException.class)
    public void removeNonExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");

        Files.write(HANDLER_ORIGINAL, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        assertXmlIdentical(HANDLER_ORIGINAL, Files.toString(cfg, Charsets.UTF_8));

        client.apply(Logging.handler().console().remove("NONEXISTING"));
    }

    @Test
    public void removeSyslogHandler() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SYSLOG_HANDLER, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        RemoveLogHandler removeLogHandler = new RemoveLogHandler(SYSLOG, "syslog");

        assertXmlIdentical(SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));

        client.apply(removeLogHandler);
        assertXmlIdentical(NO_HANDLERS, Files.toString(cfg, Charsets.UTF_8));
    }
}
