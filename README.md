[![Build Status](https://travis-ci.org/klaeufer/issue10709-scala.svg?branch=master)](https://travis-ci.org/klaeufer/issue10709-scala)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=klaeufer_issue10709-scala&metric=alert_status)](https://sonarcloud.io/dashboard?id=klaeufer_issue10709-scala)

[This issue](https://github.com/scala/bug/issues/10709)
has been fixed as of Scala 2.12.5.

This project also includes some examples of using Mockito to express expectations declaratively.
For example, we can test whether the `Iterator.map` method is lazy in terms of
invoking `next` on the original object only when we invoke `next` on the result of `map`:

    @Test def mapIsLazyUsingSpyFrom(): Unit = {
      val it = spy(Iterator.continually("hello"))
      val result = it.map(_.length)
      verify(it, never).next()
      result.next()
      verify(it, times(1)).next()
    }

For a more in-depth discussion on this topic,
please refer to this technical report (5 pages):

*Tests as Maintainable Assets Via Auto-generated Spies:* \
*A case study involving the Scala collections library's Iterator trait* \
Konstantin Läufer, John O'Sullivan, and George K. Thiruvathukal \
In Proc. 10th ACM SIGPLAN Scala Symposium, July 2019, London, UK.
https://arxiv.org/abs/1808.09630

Also, make sure to use reference types as to instantiate any SUTs based on generic Scala traits; see also

https://github.com/mockito/mockito/issues/1605
