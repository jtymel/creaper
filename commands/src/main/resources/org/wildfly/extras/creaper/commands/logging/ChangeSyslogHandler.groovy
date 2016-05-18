def handler = logging.'syslog-handler'.find { it.@name == name }

if (!handler) {
    throw new IllegalStateException(String.format("syslog handler with name %s does not exists.", name))
}

if (nn(enabled)) {
    handler.@enabled = enabled
}

//if (nn(enabled)) {
////    handler.enabled = enabled
////    handler.replaceNode {
////        'syslog-handler'(name: name, enabled: enabled)
////    }
//    if (handler.@enabled.size() > 0) {
//        handler(enabled: enabled)
//    } else {
//       handler.@enabled = enabled
//    }
//}

if (nn(appName)) {
    if (handler.'app-name'.size() > 0) {
        handler.'app-name'.@value = appName
    } else {
        handler.appendNode {
            'app-name'(value: appName)
        }
    }
}

if (nn(facility)) {
    if (handler.facility.size() > 0) {
        handler.facility.@value = facility
    } else {
        handler.appendNode {
            'facility'(value: facility)
        }
    }
}

if (nn(hostname)) {
    if (handler.hostname.size() > 0) {
        handler.hostname.@value = hostname
    } else {
        handler.appendNode {
            'hostname'(value: hostname)
        }
    }
}

if (nn(level)) {
    if (handler.level.size() > 0) {
        handler.level.@name = level
    } else {
        handler.appendNode {
            'level'(name: level)
        }
    }
}

if (nn(port)) {
    if (handler.port.size() > 0) {
        handler.port.@value = port
    } else {
        handler.appendNode {
            'port'(value: port)
        }
    }
}

if (nn(serverAddress)) {
    if (handler.'server-address'.size() > 0) {
        handler.'server-address'.@value = serverAddress
    } else {
        handler.appendNode {
            'server-address'(value: serverAddress)
        }
    }
}

if (nn(syslogFormat)) {
    if (handler.formatter.size() > 0) {
        handler.formatter.'syslog-format'.@'syslog-type' = syslogFormat
    } else {
        handler.appendNode {
            'formatter' {
                'syslog-format'('syslog-type': syslogFormat)
            }
        }
    }
}

/**
 * Checking if parameter is not null.
 * We can't use if(object) ... as object could be null or false
 * and we need to differentiate such states
 */
def nn(Object... object) {
    if (object == null) return false
    return object.any { it != null }
}
