name := "CalendarService"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "de.jollyday" % "jollyday" % "0.5.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.quartz-scheduler" % "quartz" % "2.1.3",
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % Test,
  "org.typelevel" %% "cats-core" % "1.0.0-MF",
  "org.typelevel" %% "cats-free" % "1.0.0-MF"
)
        
