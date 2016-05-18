package org.wildfly.extras.creaper.commands.logging;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.wildfly.extras.creaper.XmlAssert.assertXmlIdentical;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineOptions;

public class ChangeSyslogHandlerOfflineTest {

    private static final String SYSLOG_HANDLER_DEFAULT_VALUES = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <syslog-handler name=\"syslog-handler\"/>"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String ONE_SYSLOG_HANDLER = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <syslog-handler name=\"syslog-handler\" enabled=\"false\">\n"
            + "                <app-name value=\"logging-app\"/>"
            + "                <facility value=\"ftp-daemon\"/>"
            + "                <hostname value=\"hostname\"/>"
            + "                <level name=\"FATAL\"/>\n"
            + "                <port value=\"9899\"/>\n"
            + "                <server-address value=\"127.0.0.1\"/>\n"
            + "                <formatter>\n"
            + "                    <syslog-format syslog-type=\"RFC3164\"/>\n"
            + "                </formatter>"
            + "            </syslog-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String CHANGED_SYSLOG_HANDLER = ""
            + "<server xmlns=\"urn:jboss:domain:4.0\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:jboss:domain:logging:3.0\">\n"
            + "            <syslog-handler name=\"syslog-handler\" enabled=\"true\">\n"
            + "                <app-name value=\"changed-logging-app\"/>"
            + "                <facility value=\"syslogd\"/>"
            + "                <hostname value=\"changed-hostname\"/>"
            + "                <level name=\"TRACE\"/>\n"
            + "                <port value=\"514\"/>\n"
            + "                <server-address value=\"localhost\"/>\n"
            + "                <formatter>\n"
            + "                    <syslog-format syslog-type=\"RFC5424\"/>\n"
            + "                </formatter>"
            + "            </syslog-handler>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void setUp() {
        XMLUnit.setNormalizeWhitespace(true);
    }

    @Test
    public void addAllProperties() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SYSLOG_HANDLER_DEFAULT_VALUES, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        ChangeSyslogHandler changeSyslogHandler = new ChangeSyslogHandler.Builder("syslog-handler")
                .appName("logging-app")
                .enabled(false)
                .facility(SyslogFacilityType.FTP_DAEMON)
                .hostname("hostname")
                .level(LogLevel.FATAL)
                .port(9899)
                .serverAddress("127.0.0.1")
                .syslogFormat(SyslogFormatType.RFC3164)
                .build();

        assertXmlIdentical(SYSLOG_HANDLER_DEFAULT_VALUES, Files.toString(cfg, Charsets.UTF_8));

        client.apply(changeSyslogHandler);
        assertXmlIdentical(ONE_SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void changeAllProperties() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(ONE_SYSLOG_HANDLER, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        ChangeSyslogHandler changeSyslogHandler = new ChangeSyslogHandler.Builder("syslog-handler")
                .appName("changed-logging-app")
                .enabled(true)
                .facility(SyslogFacilityType.SYSLOGD)
                .hostname("changed-hostname")
                .level(LogLevel.TRACE)
                .port(514)
                .serverAddress("localhost")
                .syslogFormat(SyslogFormatType.RFC5424)
                .build();

        assertXmlIdentical(ONE_SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));

        client.apply(changeSyslogHandler);
        assertXmlIdentical(CHANGED_SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void changeNothing() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(ONE_SYSLOG_HANDLER, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        ChangeSyslogHandler changeSyslogHandler = new ChangeSyslogHandler.Builder("syslog-handler")
                .appName("logging-app")
                .enabled(false)
                .facility(SyslogFacilityType.FTP_DAEMON)
                .hostname("hostname")
                .level(LogLevel.FATAL)
                .port(9899)
                .serverAddress("127.0.0.1")
                .syslogFormat(SyslogFormatType.RFC3164)
                .build();

        assertXmlIdentical(ONE_SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));

        client.apply(changeSyslogHandler);
        assertXmlIdentical(ONE_SYSLOG_HANDLER, Files.toString(cfg, Charsets.UTF_8));
    }
}
