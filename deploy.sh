if ! lsof -i :$PORT > /dev/null; then
    echo "Starting"
	./dist/bin/myapi -jvm-debug $DEBUG_PORT -Dhttp.port=$PORT > myapi.log 2>&1
	echo "$JAVA_HOME" >> server.log
    echo "$PATH" >> server.log
    java --version >> server.log
fi