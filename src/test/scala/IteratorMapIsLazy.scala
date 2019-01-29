import org.junit.Assert._
import org.junit.Test
import org.mockito.Mockito._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar

import scala.collection.AbstractIterator

class IteratorMapIsLazy extends AssertionsForJUnit with MockitoSugar {

  @Test def mapIsLazyAbstractIterator(): Unit = {
    var counter = 0
    val it = new AbstractIterator[Int] { def hasNext = true; def next = { counter += 1; counter } }
    val result = it.map(_ + 1)
    assertEquals("counter should be zero", 0, counter)
    result.next()
    assertEquals("counter should be one", 1, counter)
  }

  @Test def mapIsLazyContinually(): Unit = {
    var counter = 0
    val it = Iterator.continually { counter += 1; counter }
    val result = it.map(_ + 1)
    assertEquals("counter should be zero", 0, counter)
    result.next()
    assertEquals("counter should be one", 1, counter)
  }

  // https://github.com/scala/scala/blob/v2.12.6/src/library/scala/collection/Iterator.scala#L455
  @Test def mapIsLazyUsingSpyFrom(): Unit = {
    val it = spy(Iterator.from(1))
    val result = it.map(_ + 1)
    verify(it, never).next() // <-- NPE in Iterator.next
    result.next()
    verify(it, times(1)).next()
  }

  @Test def mapIsLazyUsingSpyFromInlinedPrivateThisORIG(): Unit = {
    def from(start: Int) = new AbstractIterator[Int] {
      private[this] var i = start
      def hasNext: Boolean = true
      def next(): Int = {
        val result = i
        i += 1
        result // <-- NPE
      }
    }
    val it = spy(from(1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }

  @Test def mapIsLazyUsingSpyFromInlinedPrivateThisLazyVal(): Unit = {
    def from(start: Int) = new AbstractIterator[Int] {
      private[this] var i = start
      def hasNext: Boolean = true
      def next(): Int = {
        lazy val result = i // lazy val does not help here
        i += 1
        result // <-- NPE
      }
    }
    val it = spy(from(1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }

  @Test def mapIsLazyUsingSpyFromInlinedPrivateThisThunk(): Unit = {
    def from(start: Int) = new AbstractIterator[Int] {
      private[this] var i = () => start // thunking avoids NPE below
      def hasNext: Boolean = true // but out of the question for performance reasons
      def next(): Int = {
        val result = i()
        i = () => (i() + 1)
        result
      }
    }
    val it = spy(from(1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }

  @Test def mapIsLazyUsingSpyFromInlinedPrivateORIG(): Unit = {
    def from(start: Int) = new AbstractIterator[Int] {
      private var i = start
      def hasNext: Boolean = true
      def next(): Int = {
        val result = i
        i += 1
        result // <-- NPE
      }
    }
    val it = spy(from(1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }

  @Test def mapIsLazyUsingSpyFromInlinedPrivateLazyVal(): Unit = {
    def from(start: Int) = new AbstractIterator[Int] {
      private var i = start
      def hasNext: Boolean = true
      def next(): Int = {
        lazy val result = i // <-- lazy val avoids NPE below
        i += 1
        result
      }
    }
    val it = spy(from(1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }

  // https://github.com/scala/scala/blob/v2.12.6/src/library/scala/collection/Iterator.scala#L455
  @Test def mapIsLazyUsingSpyIterate(): Unit = {
    val it = spy(Iterator.iterate(1)(_ + 1))
    val result = it.map(_ + 1)
    verify(it, never).next()
    result.next()
    verify(it, times(1)).next()
  }
}
