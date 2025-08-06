##!/usr/bin/env bash
#if [[ ! $(lsof -i:3306 -t | wc -l) -gt 0 ]]; then
#	echo "Starting mysql"
#	mysqld_safe --defaults-file=/home/shreyas/.asdf/installs/mysql/8.0.31/conf/my.cnf > mysql.log 2>&1 &
#fi
#
#while [[ ! $(lsof -i:3306 -t | wc -l) -gt 0 ]];
#do
#	echo "Waiting for mysql"
#done
#
#if [[ ! $(lsof -i:9200 -t | wc -l) -gt 0 ]]; then
#	echo "Starting Elastic"
#	elasticsearch -d > elasticsearch.log 2>&1 &
#fi
#
#while [[ ! $(lsof -i:9200 -t | wc -l) -gt 0 ]];
#do
#	echo "Waiting for elastic"
#done

#set -o allexport;
#source .env;
#set +o allexport;
sbt -jvm-debug "8021" -Dlogger.file=conf/logback.xml -Dhttp.address="0.0.0.0" "run 9021";

#kill -9 $(lsof -i:9200 -t);
#kill -9 $(lsof -i:3306 -t);