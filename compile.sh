rm -rf myapi-1.0.0
sbt clean compile dist
unzip target/universal/myapi-1.0.0.zip