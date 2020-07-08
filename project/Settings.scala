import sbt.Def

object Settings {

  lazy val commonScalacOptions: Def.Initialize[Seq[String]] = Def.setting {
    Seq(
      "-feature",               // Emit warning and location for usages of features that should be imported explicitly.
      "-language:higherKinds",  // Allow higher-kinded types
    )
  }

}
