#!/usr/bin/env bash
set -o allexport;
source .env;
set +o allexport;
sbt -jvm-debug "8021" -Dlogger.file=conf/logger.xml -Dhttp.address="0.0.0.0" "run 9021";