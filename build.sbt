inThisBuild(
  List(
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
    sbtPlugin := true
  )

lazy val docs = project
  .in(file("plugin-docs"))
  .settings(
    moduleName := "sbt-docusaurus-docs",
    libraryDependencies ++= List(
      "com.geirsson" % "mdoc" % "0.4.0" cross CrossVersion.full
    )
  )
  .enablePlugins(DocusaurusPlugin)
