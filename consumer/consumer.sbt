import Dependencies._
import Projects._

lazy val consumer = project.in(file("."))
  .withId(Projects.consumer.project)
  .dependsOn(
    api,
    kafkaCats % "compile -> compile; test -> test; it -> test",
    producer % "test -> test; it -> test")
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      Cats.catsCore,
      Cats.catsEffect,
      Core.pureConfig,
      Testing.scalaTest % Test,
      Testing.scalaTest % IntegrationTest,
    )
  )
