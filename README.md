# Description
A Gopher server built in Scala.

The aim is a simple server which serves a directory on the filesystem.


## Logging
Log level can be configured with the system property:

    org.slf4j.simpleLogger.defaultLogLevel

Must be one of ("trace", "debug", "info", "warn", or "error"). If
not specified, defaults to "info". 

The logger will also look for a file simplelogger.properties in the
classloader path, and search it for applicable properties.
