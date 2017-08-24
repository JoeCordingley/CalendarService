name := "CalendarService"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "de.jollyday" % "jollyday" % "0.5.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.quartz-scheduler" % "quartz" % "2.1.3"
)
        
