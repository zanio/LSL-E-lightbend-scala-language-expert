/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

object Queue {

  def apply[A](elements: A*): Queue[A] =
    new Queue(elements.toVector)
}

class Queue[+A] private (private val elements: Seq[A]) {

  def dequeue: (A, Queue[A]) =
    elements match {
      case element +: elements => (element, new Queue(elements))
      case _       => throw new UnsupportedOperationException("Cannot dequeue from an empty queue")
    }

  def enqueue[B >: A](element: B): Queue[B] =
    new Queue(elements :+ element)

  override def equals(other: Any): Boolean =
    other match {
      case that: Queue[_] => (this eq that) || (this.elements == that.elements)
      case _              => false
    }

  override def hashCode: Int =
    elements.hashCode

  override def toString: String =
    s"Queue(${elements mkString ", "})"
}

