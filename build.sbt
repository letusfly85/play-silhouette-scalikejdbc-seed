name := "play-silhouette-scalikejdbc-js"

organization := "io.wonder-soft"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= {
  val silhouetteVersion = "5.0.3"
  val scalikeJDBCV = "3.0.2"
  Seq(
    guice,
    ehcache,

    //silhouette dependencies
    "com.mohiva" %% "play-silhouette" % silhouetteVersion,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
    "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
    "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion,
    "com.mohiva" %% "play-silhouette-testkit" % silhouetteVersion,

    //webjar dependencies
    "org.webjars" %% "webjars-play" % "2.6.1",
    "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
    "org.webjars" % "jquery" % "3.2.1",
    "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",

    //injection dependencies
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.iheart" %% "ficus" % "1.4.1",
    "net.codingwell" %% "scala-guice" % "4.1.0",

    //ScalikeJDBC dependencies
    "org.scalikejdbc" %% "scalikejdbc"                     % scalikeJDBCV,
    "org.scalikejdbc" %% "scalikejdbc-config"              % scalikeJDBCV,
    "org.scalikejdbc" %% "scalikejdbc-play-initializer"    % "2.6.0-scalikejdbc-3.0",
    "org.scalikejdbc" %% "scalikejdbc-test" % scalikeJDBCV % Test,
    "mysql" % "mysql-connector-java" % "5.1.33",

    "com.iheart" %% "ficus" % "1.4.1",
    "com.typesafe.play" %% "play-mailer" % "6.0.1",
    "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x",

    // swagger dependencies
    "io.swagger" %% "swagger-play2" % "1.6.1-SNAPSHOT",

    // specification dependencies
    "com.typesafe.play" %% "play-specs2" % "2.6.7" % Test,
    "org.specs2" %% "specs2" % "2.5" % Test
  )
}

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.letusfly85.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.letusfly85.binders._"

routesGenerator := InjectedRoutesGenerator

routesImport += "utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:implicitConversions",
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

//********************************************************
// Scalariform settings
//********************************************************

import scalariform.formatter.preferences._

//defaultScalariformSettings

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)


//********************************************************
// ScalikeJDBC settings
//********************************************************
scalikejdbcSettings