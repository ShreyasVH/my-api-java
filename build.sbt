name := "myapi"

version := "1.0.0"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  "com.mysql" % "mysql-connector-j" % "9.5.0",
  "org.projectlombok" % "lombok" % "1.18.8",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.2.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)