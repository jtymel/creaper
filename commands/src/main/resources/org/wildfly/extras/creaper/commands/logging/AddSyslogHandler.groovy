def handlerExists = logging.'syslog-handler'.find { it.@name == name }

handlerAttrs = ['name': name]

if (nn(enabled)) handlerAttrs['enabled'] = enabled

def newHandlerDef = {
    'syslog-handler'(handlerAttrs) {
        if (nn(appName)) {
            'app-name'(value: appName)
        }
        if (nn(facility)) {
            'facility'(value: facility)
        }
        if (nn(hostname)) {
            'hostname'(value: hostname)
        }
        if (nn(level)) {
            'level'(name: level)
        }
        if (nn(port)) {
            'port'(value: port)
        }
        if (nn(serverAddress)) {
            'server-address'(value: serverAddress)
        }
        if (nn(syslogFormat)) {
            'formatter' {
                'syslog-format'('syslog-type': syslogFormat)
            }
        }
    }
}

if (!handlerExists) {
    logging.appendNode newHandlerDef
} else if (replaceExisting) {
    handlerExists.replaceNode newHandlerDef
} else {
    throw new IllegalStateException(String.format("syslog handler with name %s already exists. If you want to replace existing handler, please set replaceExisting.", name))
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
