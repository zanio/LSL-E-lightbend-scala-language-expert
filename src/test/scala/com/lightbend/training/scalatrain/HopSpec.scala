/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import java.lang.{IllegalArgumentException => IAE}

import com.lightbend.training.scalatrain.TestData._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class HopSpec extends AnyWordSpec with Matchers {

  "Creating a Hop" should {
    "throw an IllegalArgumentException for equal from and to" in {
      an[IAE] should be thrownBy Hop.createHop(munich, munich, green)
    }
    "throw an IllegalArgumentException for from and to not back-to-back stations of train" in {
      an[IAE] should be thrownBy Hop.createHop(munich, stuttgart, green)
    }
  }

  "departureTime" should {
    "be initialized correctly" in {
      Hop.createHop(dusseldorf, frankfurt, blue).departureTime shouldEqual blueDusseldorfTime
    }
  }

  "arrivalTime" should {
    "be initialized correctly" in {
      Hop.createHop(dusseldorf, frankfurt, blue).arrivalTime shouldEqual blueFrankfurtTime
    }
  }
}
