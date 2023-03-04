/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package misc

import org.scalatest.NonImplicitAssertions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EqualSpec extends AnyWordSpec with Matchers with NonImplicitAssertions {

  import Equal._

  "Calling ===" should {
    "be true for nonequal Strings and all other equal objects" in {
      "1" === "2" shouldBe true
      1 === 1 shouldBe true
      1.0 === 1.0 shouldBe true
    }
    "be false for equal Strings and all other nonequal objects" in {
      "1" === "1" shouldBe false
      1 === 2 shouldBe false
    }
    "not compile for different types" in {
      """ "d" === 2 """ shouldNot compile
    }
  }
}
