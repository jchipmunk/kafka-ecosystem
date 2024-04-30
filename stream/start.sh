#!/usr/bin/env bash

# Exit immediately if a *pipeline* returns a non-zero status. (Add -x for command tracing)
set -e

DEFAULT_JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$APP_HOME/dumps/"
DEFAULT_JAVA_OPTS="$DEFAULT_JAVA_OPTS -XX:+ExitOnOutOfMemoryError"

if [[ -z "$LOG_OPTS" ]]; then
  LOG_OPTS="-Dlogback.configurationFile=$APP_HOME/config/logback.xml"
fi
: ${LOG_DIR:="$APP_HOME/logs"}
LOG_OPTS="-Dlog.dir=$LOG_DIR $LOG_OPTS"

if [[ -z "$HEAP_OPTS" ]]; then
  HEAP_OPTS="-Xms256m -Xmx2G"
fi

if [[ -z "$PERF_OPTS" ]]; then
  PERF_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35"
  # MaxInlineLevel=15 is the default since JDK 14 and can be removed once older JDKs are no longer supported
  PERF_OPTS="$PERF_OPTS -XX:+ExplicitGCInvokesConcurrent -XX:MaxInlineLevel=15 -Djava.awt.headless=true"
fi

: ${DEBUG:="false"}
if [[ "$DEBUG" == true ]]; then
  echo "JMX options: $JMX_OPTS"
  echo "Log options: $LOG_OPTS"
  echo "Heap options: $HEAP_OPTS"
  echo "Perf options: $PERF_OPTS"
  echo "Java options: $JAVA_OPTS"
  set -x
fi

exec java ${DEFAULT_JAVA_OPTS} ${JMX_OPTS} ${LOG_OPTS} ${HEAP_OPTS} ${PERF_OPTS} ${JAVA_OPTS} -jar "$APP_HOME/kafka-streams.jar" "$@"
