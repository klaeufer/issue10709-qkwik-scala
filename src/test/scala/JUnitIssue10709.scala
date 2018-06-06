import scala.collection.{ AbstractIterator, GenIterable, IterableLike }
import scala.runtime.ScalaRunTime.stringOf

import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito._

class JUnitIssue10709 {

  @Test def `scan is lazy enough`(): Unit = {
    val results = collection.mutable.ListBuffer.empty[Int]
    val it = new AbstractIterator[Int] {
      var cur = 1
      val max = 3
      override def hasNext = {
        results += -cur
        cur < max
      }
      override def next() = {
        val res = cur
        results += -res
        cur += 1
        res
      }
    }
    val xy = it.scanLeft(10)((sum, x) => {
      results += -(sum + x)
      sum + x
    })
    val scan = collection.mutable.ListBuffer.empty[Int]
    for (i <- xy) {
      scan += i
      results += i
    }
    assertSameElements(List(10, 11, 13), scan)
    assertSameElements(List(10, -1, -1, -11, 11, -2, -2, -13, 13, -3), results)
  }

  @Test def `scan is lazy enough w/ spy`(): Unit = {
    val input = spy(Iterator(1, 2, 3))
    val expected = Array(0, 1, 3, 6)
    val result = input.scanLeft(0)(_ + _)
    for (i <- expected.indices) {
      assertEquals(expected(i), result.next())
      verify(input, times(i)).next()
    }
  }

  // copied from test/junit/scala/tools/testing/AssertUtil.scala

  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: GenIterable[B], message: String = ""): Unit =
    if (!(expected sameElements actual))
      fail(
        f"${if (message.nonEmpty) s"$message " else ""}expected:<${stringOf(expected)}> but was:<${stringOf(actual)}>")

  /**
   * Convenient for testing iterators.
   */
  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: Iterator[B]): Unit =
    assertSameElements(expected, actual.toList, "")
}
