ThisBuild / scalaVersion := "3.2.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

val zioVersion = "2.0.9"
val zioHTTPVersion = "0.0.4"
val zioJsonVersion = "0.4.2"
val quillVersion = "4.6.0.1"
val zioLoggingVersion = "2.1.9"
val zioConfigVersion = "3.0.7"
val postgresqlVersion = "42.5.4"
val slf4jVersion = "2.0.5"
val flywayVersion = "9.15.1"

lazy val root = (project in file("."))
    .settings(
        name := "CraftingZIO",
        libraryDependencies ++= Seq(
            "dev.zio" %% "zio" % zioVersion,
            "dev.zio" %% "zio-streams" % zioVersion,

            "dev.zio" %% "zio-json" % zioJsonVersion,

            "dev.zio" %% "zio-logging" % zioLoggingVersion,
            "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion,
            "org.slf4j" % "slf4j-simple" % slf4jVersion,

            "dev.zio" %% "zio-config" % zioConfigVersion,
            "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
            "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,

            "dev.zio" %% "zio-http" % zioHTTPVersion,

            "io.getquill" %% "quill-jdbc-zio" % quillVersion,
            "org.postgresql" % "postgresql" % postgresqlVersion,
            "org.flywaydb" % "flyway-core" % flywayVersion,

            "dev.zio" %% "zio-test" % zioVersion % Test,
            "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
        ),
        testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )
