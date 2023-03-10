/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QueueSpec extends AnyWordSpec with Matchers {

  "Queue" should {
    "be covariant" in {
      class Animal
      class Bird extends Animal
      val bird = new Bird
      val qb: Queue[Bird] = Queue(bird)
      val qa: Queue[Animal] = qb
      qa.dequeue shouldBe (bird, Queue[Animal]())

    }
  }

  "Calling equals" should {
    "be true for identical objects" in {
      val queue = Queue(1, 2, 3)
      queue == queue shouldBe true
    }
    "be true for equal objects" in {
      Queue(1, 2, 3) == Queue(1, 2, 3) shouldBe true
    }
    "be true for nonequal objects" in {
      Queue(1, 2) == Queue(1, 2, 3) shouldBe false
    }
    "be false when comparing to a non-Queue object" in {
      Queue(1, 2, 3) == "definitely not a Queue" shouldBe false
    }
  }

  "The implementation details of Queue" should {
    "not be visible from the outside" in {
      "val q = new Queue(List(1,2,3)).elements" shouldNot compile
    }
  }

	"Queue's constructor" should {
    "be private" in {
      "val q = new Queue(List(1, 2, 3))" shouldNot compile
    }
  }

  "Calling hashCode" should {
    "return the same value for equal objects" in {
      Queue(1, 2, 3).## shouldEqual Queue(1, 2, 3).##
    }
  }

  "Calling toString" should {
    "return the class name and the elements in parentheses" in {
      Queue(1, 2, 3).toString shouldEqual "Queue(1, 2, 3)"
    }
  }

  "Calling dequeue" should {
    "throw an UnsupportedOperationException for an empty queue" in {
      an[UnsupportedOperationException] should be thrownBy Queue().dequeue
    }
    "return the first element and a new Queue without the dequeued element" in {
      Queue(1, 2, 3).dequeue shouldEqual (1, Queue(2, 3))
    }
  }

  "Calling enqueue" should {
    "return a new Queue with the new element enqueued at the end" in {
      (Queue(1, 2) enqueue 3.0) shouldEqual Queue(1, 2, 3.0)
    }
  }
}
