inThisBuild(
  List(
    scalaVersion := "2.12.8",
    organization := "com.geirsson",
    homepage := Some(url("https://github.com/olafurpg/sbt-docusaurus")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "olafurpg",
        "Ólafur Páll Geirsson",
        "olafurpg@gmail.com",
        url("https://geirsson.com")
      )
    )
  )
)

skip in publish := true

lazy val plugin = project
  .settings(
    sbtPlugin := true,
    moduleName := "sbt-docusaurus",
    libraryDependencies ++= List(
      "org.jsoup" % "jsoup" % "1.11.3",
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalameta" %% "testkit" % "4.0.0-M11" % Test
    )
  )

lazy val docs = project
  .in(file("plugin-docs"))
  .settings(
    moduleName := "sbt-docusaurus-docs",
    mainClass.in(Compile) := Some("docs.Main"),
    resolvers += Resolver.sonatypeRepo("releases"),
    buildInfoPackage := "docs",
    buildInfoKeys := Seq[BuildInfoKey](
      version
    )
  )
  .enablePlugins(DocusaurusPlugin, BuildInfoPlugin)
