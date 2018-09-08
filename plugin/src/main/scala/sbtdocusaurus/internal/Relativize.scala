package sbtdocusaurus.internal

import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.JavaConverters._

object Relativize {

  def htmlSite(site: Path): Unit = {
    Files.walkFileTree(
      site,
      new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          if (file.getFileName.toString.endsWith(".html")) {
            processHtmlFile(site, file)
          }
          super.visitFile(file, attrs)
        }
      }
    )
  }

  // actual host name doesn't matter
  private val baseUri = URI.create("http://example.com/")

  def processHtmlFile(site: Path, file: Path): Unit = {
    val originRelativeUri = relativeUri(site.relativize(file))
    val originUri = baseUri.resolve(originRelativeUri)
    val originPath = Paths.get(originUri.getPath).getParent
    def relativizeAttribute(a: Element, attribute: String): Unit = {
      val absoluteHref = URI.create(a.attr(s"abs:$attribute"))
      if (absoluteHref.getHost == baseUri.getHost) {
        val hrefPath = Paths.get(absoluteHref.getPath)
        val relativeHref = {
          val relativePath = originPath.relativize(hrefPath)
          val absolutePath = file.getParent.resolve(relativePath)
          val isDirectory = Files.isDirectory(absolutePath)
          if (isDirectory) relativePath.resolve("index.html")
          else relativePath
        }
        val fragment =
          if (absoluteHref.getFragment == null) ""
          else "#" + absoluteHref.getFragment
        val newHref = relativeUri(relativeHref).toString + fragment
        a.attr(attribute, newHref)
      }
    }
    val doc = Jsoup.parse(file.toFile, StandardCharsets.UTF_8.name(), originUri.toString)
    def relativizeElement(element: String, attribute: String): Unit =
      doc.select(element).forEach { element =>
        relativizeAttribute(element, attribute)
      }
    relativizeElement("a", "href")
    relativizeElement("link", "href")
    relativizeElement("img", "src")
    val renderedHtml = doc.outerHtml()
    Files.write(file, renderedHtml.getBytes(StandardCharsets.UTF_8))
  }

  private def relativeUri(relativePath: Path): URI = {
    require(!relativePath.isAbsolute, relativePath)
    val names = relativePath.iterator().asScala
    val uris = names.map { name =>
      new URI(null, null, name.toString, null)
    }
    URI.create(uris.mkString("", "/", ""))
  }
}
