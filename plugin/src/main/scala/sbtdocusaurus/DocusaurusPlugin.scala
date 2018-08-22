package sbtdocusaurus

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import sbt.Def
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import sys.process._

object DocusaurusPlugin extends AutoPlugin {
  override def requires: Plugins = JvmPlugin

  def redirectHtml(url: String): String = {
    s"""
       |<!DOCTYPE HTML>
       |<html lang="en-US">
       |    <head>
       |        <meta charset="UTF-8">
       |        <meta http-equiv="refresh" content="0; url=$url">
       |        <script type="text/javascript">
       |            window.location.href = "$url"
       |        </script>
       |        <title>Page Redirection</title>
       |    </head>
       |    <body>
       |        <!-- Note: don't tell people to `click` the link, just tell them that it is a link. -->
       |        If you are not redirected automatically, follow this <a href='$url'>link</a>.
       |    </body>
       |</html>
      """.stripMargin
  }

  def installSSH(): Unit = {
    val env = sys.env
    env.get("GITHUB_DEPLOY_KEY").foreach { githubDeployKey =>
      println("Setting up ssh...")
      val email = env("USER_EMAIL")
      val travisBuildNumber = env("TRAVIS_BUILD_NUMBER")
      val traviscommit = env("TRAVIS_COMMIT")
      val userName = s"$travisBuildNumber@$traviscommit"
      val ssh = file(sys.props("user.home")) / ".ssh"
      val knownHosts = ssh / "known_hosts"
      val deployKeyFile = ssh / "id_rsa"
      ssh.mkdirs()
      (s"ssh-keyscan -t rsa github.com" #>> knownHosts).execute()
      s"git config --global user.email '$email'".execute()
      s"git config --global user.name '$userName'".execute()
      "git config --global push.default simple".execute()
      (s"echo $githubDeployKey" #| "base64 --decode" #> deployKeyFile).execute()
      s"chmod 600 $deployKeyFile".execute()
      """bash -c 'eval "$(ssh-agent -s)"' """.execute()
      s"ssh-add $deployKeyFile".execute()
    }
  }

  object autoImport {
    val docusaurusProjectName =
      taskKey[String]("The siteConfig.js `projectName` setting value")
    val docusaurusCreateSite =
      taskKey[File]("Create static build of docusaurus site")
    val docusaurusPublishGhpages =
      taskKey[Unit]("Publish docusaurus site to GitHub pages")
  }
  import autoImport._

  def website: Def.Initialize[File] = Def.setting {
    baseDirectory.in(ThisBuild).value / "website"
  }

  def listJarFiles(root: Path): List[(File, String)] = {
    val files = List.newBuilder[(File, String)]
    Files.walkFileTree(
      root,
      new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          val relpath = root.relativize(file)
          files += (file.toFile -> relpath.toString)
          super.visitFile(file, attrs)
        }
      }
    )
    files.result()
  }

  def gitUser(): String =
    sys.env.getOrElse("GIT_USER", {
      "git config user.email".!!.trim
    })

  override def projectSettings: Seq[Def.Setting[_]] = List(
    mainClass.in(Compile) := Some("mdoc.Main"),
    docusaurusProjectName := moduleName.value.stripSuffix("-docs"),
    docusaurusPublishGhpages := {
      installSSH()
      Process(List("yarn", "install"), cwd = website.value).execute()
      Process(
        List("yarn", "run", "publish-gh-pages"),
        cwd = website.value,
        "GIT_USER" -> gitUser(),
        "USE_SSH" -> "true"
      ).execute()
    },
    docusaurusCreateSite := {
      Process(List("yarn", "install"), cwd = website.value).execute()
      Process(List("yarn", "run", "build"), cwd = website.value).execute()
      val redirectUrl = "/" + docusaurusProjectName.value
      val html = redirectHtml(redirectUrl)
      val out = website.value / "build"
      IO.write(out / "index.html", html)
      out
    },
    doc := {
      docusaurusCreateSite.dependsOn(run.in(Compile).toTask(" ")).value
    },
    publish := {
      publish.dependsOn(docusaurusPublishGhpages).value
    },
    packageDoc.in(Compile) := {
      val directory = doc.value
      val jar = target.value / "docusaurus.jar"
      val files = listJarFiles(directory.toPath)
      IO.jar(files, jar, new java.util.jar.Manifest())
      jar
    }
  )

  implicit class XtensionStringProcess(command: String) {
    def execute(): Unit = {
      Process(command).execute()
    }
  }
  implicit class XtensionProcess(command: ProcessBuilder) {
    def execute(): Unit = {
      val exit = command.!
      assert(exit == 0, s"command returned $exit: $command")
    }
  }
}
