#!/usr/bin/env bash

# Exit immediately if a *pipeline* returns a non-zero status. (Add -x for command tracing)
set -e
if [[ "$DEBUG" == true ]]; then
  set -x
  printenv
fi

# Process some known arguments to run Kafka Streams application
case $1 in
  start)
    shift
    exec ${APP_HOME}/start.sh ${APP_HOME}/config/server.properties
    ;;
esac

# Otherwise just run the specified command
exec "$@"
