logLevel := sbt.Level.Info

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.18")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")

addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.0")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.0.0")
resolvers += "Flyway" at "https://flywaydb.org/repo"

//mysql
libraryDependencies += "mysql" % "mysql-connector-java"  % "5.1.46"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.3.+")
