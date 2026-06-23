name := "myapi"

version := "1.0.0"

scalaVersion := "3.8.4"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  jdbc,
  javaJpa,
  "com.mysql" % "mysql-connector-j" % "9.7.0",
  "org.projectlombok" % "lombok" % "1.18.46" % "provided",
  "org.hibernate.orm" % "hibernate-core" % "7.4.2.Final",
  "co.elastic.clients" % "elasticsearch-java" % "9.4.2"
)

val pekkoVersion = "1.6.0"

// Pekko dependencies used by Play
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor"                 % pekkoVersion,
  "org.apache.pekko" %% "pekko-actor-typed"           % pekkoVersion,
  "org.apache.pekko" %% "pekko-stream"                % pekkoVersion,
  "org.apache.pekko" %% "pekko-slf4j"                 % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion
)

Compile / javacOptions ++= Seq("-proc:full")

lazy val root = (project in file(".")).enablePlugins(PlayJava)