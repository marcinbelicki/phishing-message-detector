import com.typesafe.sbt.packager.docker.{
  DockerChmodType,
  DockerPermissionStrategy
}

name         := """phishing-message-detector"""
organization := "pl.belicki"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

dockerBaseImage          := "openjdk:21"
dockerChmodType          := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown
dockerRepository         := sys.env.get("DOCKER_REPOSITORY")

scalaVersion := "2.13.16"

semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision
scalacOptions += {
  if (scalaVersion.value.startsWith("2.12"))
    "-Ywarn-unused-import"
  else
    "-Wunused:imports"

}

libraryDependencies ++= List(
  guice,
  ws,
  "org.scalatestplus.play"       %% "scalatestplus-play"   % "7.0.2" % Test,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.20.0"
) ::: List(
  "com.typesafe.slick" %% "slick"                      % "3.6.1",
  "com.typesafe.slick" %% "slick-testkit"              % "3.6.1"   % Test,
  "com.typesafe.slick" %% "slick-hikaricp"             % "3.6.1",
  "org.postgresql"      % "postgresql"                 % "42.7.7",
  "org.testcontainers"  % "postgresql"                 % "1.21.3"  % Test,
  "org.flywaydb"        % "flyway-core"                % "11.12.0",
  "org.flywaydb"        % "flyway-database-postgresql" % "11.12.0" % "runtime"
) ::: List(
  "com.github.cb372" %% "scalacache-core"  % "0.28.0",
  "com.github.cb372" %% "scalacache-guava" % "0.28.0"
)
