rm -rf dist
sbt clean compile dist
unzip target/universal/$REPO_NAME-$REPO_VERSION.zip > /dev/null
mv $REPO_NAME-$REPO_VERSION dist