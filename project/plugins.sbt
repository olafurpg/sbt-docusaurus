unmanagedSourceDirectories.in(Compile) +=
  baseDirectory.in(ThisBuild).value.getParentFile / "plugin" / "src" / "main" / "scala"
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.2.1")
addSbtPlugin(
  "io.get-coursier" % "sbt-coursier" % coursier.util.Properties.version
)
