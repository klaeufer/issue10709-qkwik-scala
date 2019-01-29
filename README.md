[![Build Status](https://travis-ci.org/klaeufer/issue10709-scala.svg?branch=master)](https://travis-ci.org/klaeufer/issue10709-scala)

[This issue](https://github.com/scala/bug/issues/10709)
has been fixed as of Scala 2.12.5.

This project has morphed into a set of use cases for Mockito with Scala
that we consider useful but have been affected by issues
somewhere in the toolchain.

- Linux 4.15.0 (kernel version)
- Java 11
- Scala 2.12.8
- Mockito *inline* 2.23.4

*IMPORTANT: These examples depend on `mockito-inline` for the
ability to spy on instances of final classes.*

In Mockito, we can express expectations declaratively.
For example, we can test whether `Iterator.map` method is lazy
in terms of invoking `next` on the original
only when we invoke `next` on the result of `map`:

    @Test def mapIsLazyUsingSpyFrom(): Unit = {
      val it = spy(Iterator.from(1))
      val result = it.map(_ + 1)
      verify(it, never).next() // <-- NPE in Iterator.next
      result.next()
      verify(it, times(1)).next()
    }

Unfortunately, this results in a `NullPointerException`
at the first invocation of `verify` caused by `result` being `null`
in the definition of `next`:

    def from(start: Int) = new AbstractIterator[Int] {
      private var i = start
      def hasNext: Boolean = true
      def next(): Int = {
        val result = i
        i += 1
        result // <-- NPE
      }
    }

It works if we define `result` as a `lazy val`.

*This suggests that there might be some field initialization ordering issue between
the original object and the generated spy.*

For an exploration of different combinations of `private` versus `private[this]`
with `lazy val` versus thunking, please look at this test suite:

[src/test/scala/IteratorMapIsLazy](src/test/scala/IteratorMapIsLazy.scala)

For more context and examples, please refer to this brief technical report (5 pages):

*Auto-generated Spies Increase Test Maintainability* \
Konstantin Läufer, John O'Sullivan, and George K. Thiruvathukal \
https://arxiv.org/abs/1808.09630
