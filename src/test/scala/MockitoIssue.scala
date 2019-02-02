import org.mockito.Mockito._
import scala.collection.AbstractIterator

object MockitoIssue {
  def main(args: Array[String]) {
    def from(start: Int) = new AbstractIterator[Int] {
      private var i = start

      override def hasNext: Boolean = true

      override def next(): Int = {
        val result = i
        i += 1
        result // <-- NPE
      }
    }

    val it = spy(from(1))
    println("[main] exercising 1")
    val result = it.map(_ + 1)
    println("[main] verifying 1")
    verify(it, never).next()
    println("[main] exercising 2")
    result.next()
    println("[main] verifying 2")
    verify(it, times(1)).next()
    println("[app] done")
  }
}
