import Dependencies._

lazy val kafkaCats = project.in(file("."))
  .withId(Projects.kafkaCats.project)
  .settings(
    libraryDependencies ++= Seq(
      Cats.catsCore,
      Cats.catsEffect,
      Kafka.kafkaClient,
      Core.guava,
    )
  )
