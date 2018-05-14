import org.junit.Test
import org.mockito.Mockito._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar

class FurtherSpyExplorations extends AssertionsForJUnit with MockitoSugar {

  // https://github.com/scala/scala/blob/v2.12.6/src/library/scala/collection/Iterator.scala#L455
  @Test def mapIsLazy(): Unit = {
    val input = spy(Iterator("hello", "world", "what", "up"))
    val expected = Array(5, 5, 4, 2)
    val result = input.map(_.length)
    verify(input, never).next()
    for (i <- expected.indices) {
      assert(result.next() === expected(i))
      verify(input, times(i + 1)).next()
    }
  }

  def preservesIteratorProtocol[T, U]() = ???
}
