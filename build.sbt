name := "myapi"

version := "1.0.0"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  "mysql" % "mysql-connector-java" % "8.0.17",
  "org.projectlombok" % "lombok" % "1.18.8",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.2.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)