object Main extends App {
  val lines = scala.io.Source.stdin.getLines
  val results = lines.scanLeft(0)((count, line) => count + line.length)
  results.foreach(println)
}
