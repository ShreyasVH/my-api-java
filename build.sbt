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
  "org.hibernate.orm" % "hibernate-core" % "7.3.8.Final",
  "co.elastic.clients" % "elasticsearch-java" % "9.4.2"
)

Compile / javacOptions ++= Seq("-proc:full")

lazy val root = (project in file(".")).enablePlugins(PlayJava)