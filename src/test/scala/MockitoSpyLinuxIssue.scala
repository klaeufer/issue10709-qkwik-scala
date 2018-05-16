import org.junit.Test
import org.mockito.Mockito._

import scala.collection.AbstractIterator

class MockitoSpyLinuxIssue {

  // https://github.com/scala/scala/blob/v2.12.6/src/library/scala/collection/Iterator.scala#L455
  @Test def testIteratorFrom(): Unit = {
    val it = spy(Iterator.from(1))
    verify(it, never).next() // FIXME NPE on Linux but not MacOS
  }

  @Test def testAbstractIteratorPrivate(): Unit = {
    val i0 = new AbstractIterator[Int] {
      private var i = 1
      def hasNext: Boolean = true
      def next(): Int = { val result = i; i += 1; result }
    }
    val it = spy(i0)
    verify(it, never).next() // FIXME NPE on Linux but not MacOS
  }

  @Test def testAbstractIteratorPrivateThis(): Unit = {
    val i0 = new AbstractIterator[Int] {
      private[this] var i = 1
      def hasNext: Boolean = true
      def next(): Int = { val result = i; i += 1; result }
    }
    val it = spy(i0)
    verify(it, never).next() // OK
  }

  @Test def testIteratorPrivate(): Unit = {
    val i0 = new Iterator[Int] {
      private var i = 1
      def hasNext: Boolean = true
      def next(): Int = { val result = i; i += 1; result }
    }
    val it = spy(i0)
    verify(it, never).next() // OK
  }

  @Test def testIteratorPrivateThis(): Unit = {
    val i0 = new Iterator[Int] {
      private[this] var i = 1
      def hasNext: Boolean = true
      def next(): Int = { val result = i; i += 1; result }
    }
    val it = spy(i0)
    verify(it, never).next() // FIXME NPE on Linux but not MacOS
  }

  @Test def testNongeneric(): Unit = {
    val v = new IntSupplier { def get: Int = 1 }
    val s = spy(v)
    verify(s, never).get // OK
  }

  @Test def testNongenericAbstract(): Unit = {
    val v = new AbstractIntSupplier { def get: Int = 1 }
    val s = spy(v)
    verify(s, never).get // OK
  }

  @Test def testGeneric(): Unit = {
    val v = new Supplier[Int] { def get: Int = 1 }
    val s = spy(v)
    verify(s, never).get // FIXME NPE on Linux but not MacOS
  }

  @Test def testGenericAbstract(): Unit = {
    val v = new AbstractSupplier[Int] { def get: Int = 1 }
    val s = spy(v)
    verify(s, never).get // FIXME NPE on Linux but not MacOS
  }
}

trait IntSupplier {
  def get: Int
}

abstract class AbstractIntSupplier extends IntSupplier

trait Supplier[+T] {
  def get: T
}

abstract class AbstractSupplier[+T] extends Supplier[T]
