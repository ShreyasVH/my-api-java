#!/usr/bin/env bash
sbt -jvm-debug "8021" -Dlogger.file=conf/logger.xml -Dhttp.address="0.0.0.0" -Dhttps.port="10021" "run 9021";