#!/usr/bin/env bash
dos2unix .env;
export $(xargs < .env);

sbt -jvm-debug "8021" -Dhttps.port="10021" "run 80";