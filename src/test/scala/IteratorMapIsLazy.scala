import org.junit.Assert._
import org.junit.Test
import org.mockito.Mockito._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar

import scala.collection.AbstractIterator

class IteratorMapIsLazy extends AssertionsForJUnit with MockitoSugar {

  @Test def mapIsLazyUsingMutableState(): Unit = {
    var counter = 0
    val it = Iterator.continually { counter += 1; counter }
    val result = it.map(_ + 1)
    assertEquals("counter should be zero", 0, counter)
    result.next()
    assertEquals("counter should be one", 1, counter)
  }

  @Test def mapIsLazyUsingSpy(): Unit = {
    val it = spy(Iterator.from(1))
    val result = it.map(_ + 1)
    verify(it, never).next() // <-- NPE in Iterator.next
    result.next()
    verify(it, times(1)).next()
  }
}
