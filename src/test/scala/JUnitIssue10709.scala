import org.junit.Test
import org.mockito.Mockito._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar

class JUnitIssue10709 extends AssertionsForJUnit with MockitoSugar {

  @Test def `scan is lazy enough`(): Unit = {
    val input = spy(Iterator(1, 2, 3))
    val expected = Array(0, 1, 3, 6)
    val result = input.scanLeft(0)(_ + _)
    for (i <- expected.indices) {
      assert(result.next() === expected(i))
      verify(input, times(i)).next()
    }
  }
}
