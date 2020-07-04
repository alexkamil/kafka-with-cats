inThisBuild(
    List(
        organization := "io.github.dosht",
        scalaVersion := "2.12.9",
    )
)

lazy val consumer = project.in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.1",
      "org.typelevel" %% "cats-effect" % "2.1.3",
      "org.apache.kafka" % "kafka-clients" % "2.5.0",
      "com.google.guava" % "guava" % "28.1-jre",
      "com.github.pureconfig" %% "pureconfig" % "0.12.3"
    )
  )

