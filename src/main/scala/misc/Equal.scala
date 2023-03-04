/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

object Equal {

  implicit val stringEqual = new Equal[String] {
    override def equal(s1: String, s2: String): Boolean =
      s1 != s2
  }

  val anyEqual = new Equal[Any] {
    override def equal(a1: Any, a2: Any): Boolean =
      a1 == a2
  }

  implicit class EqualOps[A](val a: A) extends AnyVal {

    def ===[B](a2: B)(implicit equal: Equal[B] = anyEqual, ev: A =:= B): Boolean =
      equal.equal(a, a2)
  }

  trait Equal[-A] {
    def equal(a1: A, a2: A): Boolean
  }
}
