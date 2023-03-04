/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import java.lang.{IllegalArgumentException => IAE}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TimeDSLSpec extends AnyWordSpec with Matchers {

  import TimeDSL._

  "Implicitly converting a String into a Time" should {
    "create a properly initialized Time" in {
      ("9:45": Time) shouldEqual Time(9, 45)
    }
  }

  "Calling am on an Int" should {
    "throw an IllegalArgumentException for the Int value not within (0,12]" in {
      an[IAE] should be thrownBy { 0 am }
    }
    "return a correct Time" in {
      (1 am) shouldEqual Time(1)
      (11 am) shouldEqual Time(11)
    }
  }

  "Calling pm on an Int" should {
    "throw an IllegalArgumentException for the Int value not within (0,12]" in {
      an[IAE] should be thrownBy { 0 pm }
      an[IAE] should be thrownBy { 13 pm }
    }
    "return a correct Time" in {
      (1 pm) shouldEqual Time(13)
      (12 pm) shouldEqual Time(12)
    }
  }

  "Calling am on a Time" should {
    "throw an IllegalArgumentException for the Time value not within [01:00,12:59]" in {
      an[IAE] should be thrownBy { 0  ::  0 am }
      an[IAE] should be thrownBy { 0  :: 59 am }
      an[IAE] should be thrownBy { 13 ::  0 am }
    }
    "return a correct Time" in {
      (1  ::  1 am) shouldEqual Time(1, 1)
      (11 :: 59 am) shouldEqual Time(11, 59)
      (12 ::  0 am) shouldEqual Time()
      (12 :: 30 am) shouldEqual Time(0, 30)
    }
  }

  "Calling pm on a Time" should {
    "throw an IllegalArgumentException for the Time value not within [01:00,12:59]" in {
      an[IAE] should be thrownBy { 0  ::  0 pm }
      an[IAE] should be thrownBy { 0  :: 59 pm }
      an[IAE] should be thrownBy { 13 ::  0 pm }
    }
    "return a correct Time" in {
      (12 :: 1 pm) shouldEqual Time(12, 1)
      (12 :: 0 pm) shouldEqual Time(12)
      (4 :: 15 pm) shouldEqual Time(16, 15)
    }
  }

  "Calling :: on an Int value" should {
    "return a correct Time" in {
      1 :: 30 shouldEqual Time(1, 30)
    }
  }

}
