import Dependencies._

lazy val api = project.in(file("."))
  .withId(Projects.api.project)
  .settings(
    PB.targets in Compile := Seq(scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value),
    libraryDependencies ++= Seq(
    )
  )
