/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TrainDSLSpec extends AnyWordSpec with Matchers {

  import TrainDSL._

	"Creating a Train starting with a TrainInfo" should {
    "return a properly initialized Train" in {
      (InterCityExpress(724) at "8:50" from "Munich" at "10:00" from "Nuremberg" : Train) shouldEqual
        Train(
          InterCityExpress(724),
          Vector(
            Time(8, 50) -> Station("Munich"),
            Time(10) -> Station("Nuremberg")
          )
        )
    }
  }

  "Creating a Train starting with a Train" should {
    "return a properly initialized Train" in {
      (InterCityExpress(724) at "8:50" from "Munich" at "10:00" from "Nuremberg" at "12:10" from "Frankfurt" : Train) shouldEqual
        Train(
          InterCityExpress(724),
          Vector(
            Time(8, 50) -> Station("Munich"),
            Time(10) -> Station("Nuremberg"),
            Time(12, 10) -> Station("Frankfurt")
          )
        )
    }
  }
}
