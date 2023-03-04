/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

object TestData {

  val dusseldorf = Station("Düsseldorf")

  val essen = Station("Essen")

  val bonn = Station("Bonn")

  val frankfurt = Station("Frankfurt")

  val stuttgart = Station("Stuttgart")

  val munich = Station("Munich")

  val greenFrankfurtTime = Time(10, 15)

  val greenBonnTime = Time(11, 30)

  val greenStuttgartTime = Time(14, 20)

  val greenMunichTime = Time(16, 30)

  val redDusseldorfTime = Time(6, 0)

  val redEssenTime = Time(6, 50)

  val redBonnTime = Time(7, 15)

  val redMunichTime = Time(14)

  val orangeStuttgartTime = Time(15)

  val orangeFrankfurtTime = Time(17, 20)

  val orangeMunichTime = Time(19, 50)

  val blueDusseldorfTime = Time(8)

  val blueFrankfurtTime = Time(10)

  val purpleBonnTime = Time(7, 17)

  val purpleDusseldorftime = Time(7, 58)

  val purple = Train(
    RegionalExpress(320),
    Vector(
      purpleBonnTime -> bonn,
      purpleDusseldorftime -> dusseldorf
    )
  )

  val orange = Train(
    InterCityExpress(9000, hasWifi = true),
    Vector(
      orangeStuttgartTime -> stuttgart,
      orangeFrankfurtTime -> frankfurt,
      orangeMunichTime -> munich
    )
  )

  val green = Train(
    InterCityExpress(724),
    Vector(
      greenFrankfurtTime -> frankfurt,
      greenBonnTime -> bonn,
      greenStuttgartTime -> stuttgart,
      greenMunichTime -> munich
    )
  )

  val red = Train(
    InterCityExpress(726),
    Vector(
      redDusseldorfTime -> dusseldorf,
      redEssenTime -> essen,
      redBonnTime -> bonn,
      redMunichTime -> munich
    )
  )

  val blue = Train(
    RegionalExpress(135),
    Vector(
      blueDusseldorfTime -> dusseldorf,
      blueFrankfurtTime -> frankfurt
    )
  )

  val planner = new JourneyPlanner(Set(green, red, orange, blue, purple))
}

