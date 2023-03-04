/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import java.lang.{IllegalArgumentException => IAE}

import com.lightbend.training.scalatrain.TestData._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class TrainSpec extends AnyWordSpec with Matchers {

  "travelStats" should {
    "throw an IllegalArgumentException for a travel time schedule with 0 or 1 elements" in {
      an[IAE] should be thrownBy green.travelStats(Vector())
      an[IAE] should be thrownBy green.travelStats(Vector(Time(2,30)))
    }

		"throw an IllegalArgumentException for a travel time schedule not strictly increasing in time" in {
      an[IAE] should be thrownBy
          green.travelStats(Vector(Time(9), Time(8)))
    }

    "return zero total delay and total transfer time when train arrives/leaves exactly on schedule" in {
      val actual = green.schedule.map { case (t, _) => t }
      green.travelStats(actual) shouldEqual TravelStats(0, 0)
    }
  }

  "For a systematic late arrival of N minutes in each station, travelStats" should {
    "return a zero total transfer time and a total delay equal to N * (number of stations - 1)" in {
      val actualGreen = green.schedule.map { case (t, _) => Time.fromMinutes(t.asMinutes + 5)}
      green.travelStats(actualGreen) shouldEqual TravelStats(5 * (actualGreen.size - 1), 0)

      val actualBlue = blue.schedule.map { case (t, _) => Time.fromMinutes(t.asMinutes + 5)}
      blue.travelStats(actualBlue) shouldEqual TravelStats(5 * (actualBlue.size - 1), 0)
    }
  }

  "For a constant early arrival of N minutes in each station, travelStats" should {
    "return a zero total delay and a total transfer time equal to N * (number of stations - 1) " in {
      val actualGreen = green.schedule.map { case (t, _) => Time.fromMinutes(t.asMinutes - 5)}
      green.travelStats(actualGreen) shouldEqual TravelStats(0, 5 * (actualGreen.size - 1))

      val actualBlue = blue.schedule.map { case (t, _) => Time.fromMinutes(t.asMinutes - 5)}
      blue.travelStats(actualBlue) shouldEqual TravelStats(0, 5 * (actualBlue.size - 1))
    }
  }

  "Train 'green'" should {
    "stop in Nuremberg" in {
      green.timeAt(stuttgart) shouldEqual Some(greenStuttgartTime)
    }
    "not stop in Essen" in {
      green.timeAt(essen) shouldEqual None
    }
  }

  "Train 'red'" should {
    "stop in Munich" in {
      red.timeAt(munich) shouldEqual Some(redMunichTime)
    }
    "not stop in Stuttgart" in {
      red.timeAt(stuttgart) shouldEqual None
    }
  }

  "Creating a Train" should {
    "throw an IllegalArgumentException for a schedule with 0 or 1 elements" in {
      an[IAE] should be thrownBy Train(InterCityExpress(724), Vector())
      an[IAE] should be thrownBy Train(InterCityExpress(724), Vector(greenMunichTime -> munich))
    }
    "throw an IllegalArgumentException for a schedule not strictly increasing in time" in {
      an[IAE] should be thrownBy
        Train(
          InterCityExpress(724),
          Vector(greenMunichTime -> munich, greenMunichTime -> dusseldorf))
    }
  }

  "stations" should {
    "be initialized correctly" in {
      green.stations shouldEqual Vector(frankfurt, bonn, stuttgart, munich)
    }
  }

  "backToBackStations" should {
    "be initialized correctly" in {
      green.backToBackStations shouldEqual Vector(frankfurt -> bonn, bonn -> stuttgart, stuttgart -> munich)
    }
  }

  "departureTimes" should {
    "be initialized correctly" in {
			green.departureTimes shouldEqual Map(
        frankfurt -> greenFrankfurtTime,
        bonn -> greenBonnTime,
        stuttgart -> greenStuttgartTime,
        munich -> greenMunichTime
      )
    }
  }
}
