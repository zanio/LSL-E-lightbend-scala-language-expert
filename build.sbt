
lazy val `LSL-E-lightbend-scala-language-expert` =
  (project in file(".")).settings(
    scalaVersion := Version.scalaVersion
  )
  .settings(CommonSettings.commonSettings: _*)

(Compile / runMain) := (`LSL-E-lightbend-scala-language-expert` / Compile / runMain).evaluated
