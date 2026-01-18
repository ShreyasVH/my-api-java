name := "myapi"

version := "1.0.0"

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  jdbc,
  javaJpa,
  "com.mysql" % "mysql-connector-j" % "9.5.0",
  "org.projectlombok" % "lombok" % "1.18.42" % "provided",
  "org.hibernate.orm" % "hibernate-core" % "7.2.0.Final",
  "co.elastic.clients" % "elasticsearch-java" % "9.2.3"
)

val jacksonV = "2.14.3"

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonV,
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonV,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonV,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonV,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % jacksonV
)

Compile / javacOptions ++= Seq("-proc:full")

lazy val root = (project in file(".")).enablePlugins(PlayJava)