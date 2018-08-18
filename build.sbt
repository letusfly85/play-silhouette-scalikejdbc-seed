name := "play-silhouette-scalikejdbc-seed"

organization := "io.wonder.soft"

version := "1.0.0-SNAPSHOT"

lazy val root =  (project in file(".")).enablePlugins(PlayScala, ScalikejdbcPlugin, FlywayPlugin)

scalaVersion := "2.12.6"

resolvers += Resolver.jcenterRepo

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= {
  val silhouetteVersion = "5.0.5"
  val spec2V = "4.3.3"
  val scalikeJDBCV = "3.3.0"
  val logbackV = "1.2.3"
  val logbackJsonV = "0.1.5"
  val jacksonV = "2.8.9" // 2.9.3 not working with logback-json dependencies
  Seq(
    guice,
    ehcache,
    ws,
    specs2 % Test,

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

    //ScalikeJDBC dependencies
    "org.scalikejdbc" %% "scalikejdbc"                     % scalikeJDBCV,
    "org.scalikejdbc" %% "scalikejdbc-config"              % scalikeJDBCV,
    "org.scalikejdbc" %% "scalikejdbc-play-initializer"    % "2.6.0-scalikejdbc-3.3",
    "org.scalikejdbc" %% "scalikejdbc-test" % scalikeJDBCV % Test,
    "mysql" % "mysql-connector-java" % "5.1.46",

    "com.iheart" %% "ficus" % "1.4.1",
    "com.typesafe.play" %% "play-mailer" % "6.0.1",
    "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x",

    // swagger dependencies
    "io.swagger" %% "swagger-play2" % "1.6.1-SNAPSHOT",

    // logback dependencies
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "ch.qos.logback" % "logback-core" % logbackV,
    "ch.qos.logback" % "logback-classic" % logbackV,
    "ch.qos.logback.contrib" % "logback-json-core" % logbackJsonV,
    "ch.qos.logback.contrib" % "logback-json-classic" % logbackJsonV,
    "ch.qos.logback.contrib" % "logback-jackson" % logbackJsonV,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
    "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV,

      // specification dependencies
    "org.specs2" %% "specs2-core" % spec2V % Test,
    "org.specs2" %% "specs2-mock" % spec2V % Test,
    "org.specs2" %% "specs2-common" % spec2V % Test,
    "org.specs2" %% "specs2-junit" % spec2V % Test,
    "org.specs2" %% "specs2-matcher" % spec2V % Test,
    "org.scalaz" %% "scalaz-core" % "7.2.24",
    "org.mockito" % "mockito-core" % "2.21.0" % Test
  )
}

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
scalacOptions in Test ~= { (options: Seq[String]) =>
  options filterNot (_ == "-Ywarn-dead-code")
}
fork in Test := true
parallelExecution in (Test, test) := false

//********************************************************
// Scalariform settings
//********************************************************
import scalariform.formatter.preferences._

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)

//********************************************************
// FlyWay settings
//********************************************************
import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("conf/database.flyway.conf")).resolve()

flywayDriver := conf.getString("db.default.driver")

flywayUrl := conf.getString("db.default.url")

flywayUser := conf.getString("db.default.username")

flywayPassword := conf.getString("db.default.password")

flywayLocations := Seq("filesystem:conf/db/migration")

flywayTarget := conf.getString("migration.target.version")

flywayBaselineVersion := "0.0.0"

//********************************************************
// assembly settings
//********************************************************
import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "typesafe", xs @ _*) => MergeStrategy.last
  case PathList("org", "quartz-scheduler", xs @ _*) => MergeStrategy.last
  case PathList("net", "sf.ehcache", xs @ _*) => MergeStrategy.last
  case PathList(ps @ _*) if ps.last endsWith "public-api-types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "reference-overrides.conf" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "messages" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "libjnidispatch.jnilib"  => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "libjnidispatch.so" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "jnidispatch.dll" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

logLevel := Level.Info
