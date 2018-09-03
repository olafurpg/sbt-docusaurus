package docs

object Main {
  def main(args: Array[String]): Unit = {
    val settings = mdoc
      .MainSettings()
      .withSiteVariables(
        Map(
          "VERSION" -> BuildInfo.version.replaceFirst("\\+.*", "")
        )
      )
      .withArgs(args.toList)
    val exit = mdoc.Main.process(settings)
    sys.exit(exit)
  }
}
