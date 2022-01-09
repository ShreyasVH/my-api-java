#!/usr/bin/env bash
sbt -jvm-debug "8021" -Dhttps.port="10021" "run 9021";