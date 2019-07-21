name := "myapi"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  javaEbean,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.5.4",
  "org.projectlombok" % "lombok" % "1.16.4",
  "com.google.inject" % "guice" % "4.0",
  "redis.clients" % "jedis" % "2.9.0",
//  "org.apache.logging.log4j" % "log4j-api" % "2.7",
//  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.elasticsearch" % "elasticsearch" % "5.6.3" exclude("io.netty", "netty"),
  "org.elasticsearch.client" % "transport" % "5.6.3" exclude("io.netty", "netty"),
  "commons-lang" % "commons-lang" % "2.3",
  "commons-collections" % "commons-collections" % "3.2.1",
  "commons-io" % "commons-io" % "2.4",
  "commons-codec" % "commons-codec" % "1.6"
)


lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

fork in run := false