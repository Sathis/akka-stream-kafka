name := "akka-stream-kafka"


scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.8"

lazy val AkkaVersion = "2.6.8"
lazy val AlpakkaVersion = "2.0.1"
lazy val AlpakkaKafkaVersion = "2.0.4"

publishArtifact := false
skip in publish := true

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,
  "org.scalatest" %% "scalatest-flatspec" % "3.2.0" % Test,
  "com.typesafe.akka" %% "akka-stream-kafka-testkit" % "2.0.5" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  // for JSON in Scala
  "io.spray" %% "spray-json" % "1.3.5",
  "com.typesafe.play" %% "play-json" % "2.9.0",
  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
  "org.testcontainers" % "elasticsearch" % "1.14.3",
  "org.testcontainers" % "kafka" % "1.14.3"
)
