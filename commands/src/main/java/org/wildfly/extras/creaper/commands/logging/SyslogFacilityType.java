package org.wildfly.extras.creaper.commands.logging;

public enum SyslogFacilityType {

    LOCAL_USE_5("local-use-5"),
    LINE_PRINTER("line-printer"),
    NETWORK_NEWS("network-news"),
    LOCAL_USE_4("local-use-4"),
    LOCAL_USE_7("local-use-7"),
    SYSLOGD("syslogd"),
    FTP_DAEMON("ftp-daemon"),
    LOCAL_USE_6("local-use-6"),
    LOCAL_USE_1("local-use-1"),
    LOG_ALERT("log-alert"),
    LOCAL_USE_0("local-use-0"),
    KERNEL("kernel"),
    LOCAL_USE_3("local-use-3"),
    LOCAL_USE_2("local-use-2"),
    log_audit("log-audit"),
    NTP("ntp"),
    SECURITY_2("security2"),
    SECURITY("security"),
    USER_LEVEL("user-level"),
    SYSTEM_DAEMONS("system-daemons"),
    UUCP("uucp"),
    CLOCK_DAEMON_2("clock-daemon2"),
    MAIL_SYSTEM("mail-system"),
    CLOCK_DAEMON("clock-daemon");

    private final String value;

    SyslogFacilityType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
