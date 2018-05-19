import org.junit.Test
import org.mockito.Mockito._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.mockito.MockitoSugar
import org.junit.Assert._
import org.scalatest._
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.junit.Assert
import Assert._
import scala.reflect.ClassTag
import scala.runtime.ScalaRunTime.stringOf
import scala.runtime.ScalaRunTime.stringOf
import scala.collection.{ GenIterable, IterableLike }
import scala.collection.JavaConverters._
import scala.collection.mutable
import java.lang.ref._
import java.lang.reflect._
import java.util.IdentityHashMap
import Seq.empty

import scala.collection.mutable

class FurtherSpyExplorations2 extends AssertionsForJUnit with MockitoSugar {

  //IteratorTests.scala
  @Test def groupedIteratorShouldNotAskForUnneededElement_Original(): Unit = {
    var counter = 0
    val it = new Iterator[Int] {
      var i = 0;
      def hasNext = {
        println("hasNext " + counter)
        counter = i; true
      };
      def next = {
        println("next " + counter)
        i += 1; i
      }
    }
    val slidingIt = it sliding 2
    slidingIt.next
    assertEquals("Counter should be one, that means we didn't look further than needed", 1, counter)
  }

  @Test def groupedIteratorShouldNotAskForUnneededElement_Spy(): Unit = {

    val it = spy(Iterator.from(1))

    val slidingIt = it sliding 2

    slidingIt.next

    verify(it, times(3)).hasNext

    verify(it, times(2)).next()

  }


  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: GenIterable[B], message: String = ""): Unit =
    if (!(expected sameElements actual))
      fail(
        f"${ if (message.nonEmpty) s"$message " else "" }expected:<${ stringOf(expected) }> but was:<${ stringOf(actual) }>"
      )

  /** Convenient for testing iterators.
    */
  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: Iterator[B]): Unit =
    assertSameElements(expected, actual.toList, "")

  // scala/bug#9623
  @Test def noExcessiveHasNextInJoinIterator_Original: Unit = {
    var counter = 0
    val exp = List(1,2,3,1,2,3)
    def it: Iterator[Int] = new Iterator[Int] {
      val parent = List(1,2,3).iterator
      def next(): Int = parent.next
      def hasNext: Boolean = { counter += 1; parent.hasNext }
    }
    // Iterate separately
    val res = new mutable.ArrayBuffer[Int]
    it.foreach(res += _)
    it.foreach(res += _)
    assertSameElements(exp, res)
    assertEquals(8, counter)
    // JoinIterator
    counter = 0
    res.clear
    (it ++ it).foreach(res += _)
    assertSameElements(exp, res)
    assertEquals(8, counter) // was 17
    // ConcatIterator
    counter = 0
    res.clear
    (Iterator.empty ++ it ++ it).foreach(res += _)
    assertSameElements(exp, res)
    assertEquals(8, counter) // was 14
  }

  @Test def noExcessiveHasNextInJoinIterator_Spy: Unit = {

    val exp = List(1,2,3,1,2,3)

    val it = spy(Iterator(1,2,3))




  }



}
