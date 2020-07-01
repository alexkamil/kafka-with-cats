import Dependencies._
import Projects._

lazy val consumer = project.in(file("."))
  .withId(Projects.producer.project)
  .dependsOn(kafkaCats, api)
  .settings(
    libraryDependencies ++= Seq(
      Cats.catsCore,
      Cats.catsEffect,
      Core.pureConfig
    )
  )
