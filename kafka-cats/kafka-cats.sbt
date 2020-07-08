import Dependencies._

lazy val kafkaCats = project.in(file("."))
  .withId(Projects.kafkaCats.project)
  .settings(scalacOptions ++= Settings.commonScalacOptions.value)
  .settings(
    libraryDependencies ++= Seq(
      Cats.catsCore,
      Cats.catsEffect,
      Kafka.kafkaClient,
      Core.guava,
      Kafka.embeddedKafka % Test,
      Testing.scalaTest % Test
    )
  )
