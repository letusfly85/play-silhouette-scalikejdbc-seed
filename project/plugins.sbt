logLevel := sbt.Level.Info

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

//mysql
libraryDependencies += "mysql" % "mysql-connector-java"  % "5.1.33"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.0.+")