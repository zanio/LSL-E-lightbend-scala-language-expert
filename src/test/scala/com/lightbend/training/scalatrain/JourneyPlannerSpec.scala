/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import java.lang.{IllegalArgumentException => IAE}

import com.lightbend.training.scalatrain.TestData._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.language.implicitConversions

class JourneyPlannerSpec extends AsyncWordSpec with Matchers {
  
  "stations" should {
    "be initialized correctly" in {
      planner.stations shouldEqual Set(munich, dusseldorf, frankfurt, stuttgart, essen, bonn)
    }
  }

  "hops" should {
    "be initialized correctly" in {
      planner.hops shouldEqual Map(
        dusseldorf -> Set(Hop.createHop(dusseldorf, essen, red), Hop.createHop(dusseldorf, frankfurt, blue)),
        essen -> Set(Hop.createHop(essen, bonn, red)),
        bonn -> Set(Hop.createHop(bonn, stuttgart, green), Hop.createHop(bonn, munich, red), Hop.createHop(bonn, dusseldorf, purple)),
        frankfurt -> Set(Hop.createHop(frankfurt, bonn, green), Hop.createHop(frankfurt, munich, orange)),
        stuttgart -> Set(Hop.createHop(stuttgart, munich, green), Hop.createHop(stuttgart, frankfurt, orange))
      )
    }
  }

  "Calling trainsAt" should {
    "return the correct trains" in {
      planner.trainsAt(munich) shouldEqual Set(green, red, orange)
      planner.trainsAt(stuttgart) shouldEqual Set(green, orange)
    }
  }

  "Calling stopsAt" should {
    "return the correct stops" in {
      planner.stopsAt(munich) shouldEqual Set(greenMunichTime -> green, redMunichTime -> red, orangeMunichTime -> orange)
    }
  }

  "Calling isShortTrip" should {
    "return false for more than one station in between" in {
      planner.isShortTrip(essen, frankfurt) shouldBe false
      planner.isShortTrip(dusseldorf, stuttgart) shouldBe false
    }
    "return true for zero or one stations in between" in {
      planner.isShortTrip(frankfurt, munich) shouldBe true
      planner.isShortTrip(stuttgart, frankfurt) shouldBe true
      planner.isShortTrip(dusseldorf, frankfurt) shouldBe true
      planner.isShortTrip(dusseldorf, essen) shouldBe true
    }
  }

  "Calling connections" should {
    "throw an IllegalArgumentException for equal from and to" in {
      an[IAE] should be thrownBy planner.connections(munich, munich, Time())
    }
    "return the correct connections" in {
      planner.connections(munich, frankfurt, Time()) shouldEqual Set()

      planner.connections(stuttgart, munich, orangeStuttgartTime) shouldEqual Set(
        Vector(
          Hop.createHop(stuttgart, frankfurt, orange), 
          Hop.createHop(frankfurt, munich, orange)
        )
      )

      planner.connections(stuttgart, munich, greenStuttgartTime) shouldEqual Set(
        Vector(
          Hop.createHop(stuttgart, frankfurt, orange), 
          Hop.createHop(frankfurt, munich, orange)
        ),
        Vector(
          Hop.createHop(stuttgart, munich, green)
        )
      )
      
      planner.connections(dusseldorf, munich, Time(4)) shouldEqual Set(
        Vector(
          Hop.createHop(dusseldorf, frankfurt, blue),
          Hop.createHop(frankfurt, munich, orange)
        ),
        Vector(
          Hop.createHop(dusseldorf, essen, red),
          Hop.createHop(essen, bonn, red),
          Hop.createHop(bonn, munich, red)
        ),
        Vector(
          Hop.createHop(dusseldorf, essen, red),
          Hop.createHop(essen, bonn, red),
          Hop.createHop(bonn, stuttgart, green),
          Hop.createHop(stuttgart, munich, green)
        ),
        Vector(
          Hop.createHop(dusseldorf, frankfurt, blue),
          Hop.createHop(frankfurt, bonn, green),
          Hop.createHop(bonn, stuttgart, green),
          Hop.createHop(stuttgart, munich, green)
        ),
        Vector(
          Hop.createHop(dusseldorf, essen, red),
          Hop.createHop(essen, bonn, red),
          Hop.createHop(bonn, stuttgart, green),
          Hop.createHop(stuttgart, frankfurt, orange),
          Hop.createHop(frankfurt, munich, orange)
        )
      )
    }
  }

  "Calling connections asynchronously" should {
    "return the correct connections" in {
      val futureEmptyConnections = planner.connectionsAsync(munich, frankfurt, Time())
      futureEmptyConnections.map(connections => assert(connections.isEmpty))

      val futureConnections = planner.connectionsAsync(dusseldorf, munich, Time(4))
      futureConnections.map { connections =>
        assert(connections == Set(
          Vector(
            Hop.createHop(dusseldorf, frankfurt, blue),
            Hop.createHop(frankfurt, munich, orange)
          ),
          Vector(
            Hop.createHop(dusseldorf, essen, red),
            Hop.createHop(essen, bonn, red),
            Hop.createHop(bonn, munich, red)
          ),
          Vector(
            Hop.createHop(dusseldorf, essen, red),
            Hop.createHop(essen, bonn, red),
            Hop.createHop(bonn, stuttgart, green),
            Hop.createHop(stuttgart, munich, green)
          ),
          Vector(
            Hop.createHop(dusseldorf, frankfurt, blue),
            Hop.createHop(frankfurt, bonn, green),
            Hop.createHop(bonn, stuttgart, green),
            Hop.createHop(stuttgart, munich, green)
          ),
          Vector(
            Hop.createHop(dusseldorf, essen, red),
            Hop.createHop(essen, bonn, red),
            Hop.createHop(bonn, stuttgart, green),
            Hop.createHop(stuttgart, frankfurt, orange),
            Hop.createHop(frankfurt, munich, orange)
          )
        ))
      }
    }
  }
}
