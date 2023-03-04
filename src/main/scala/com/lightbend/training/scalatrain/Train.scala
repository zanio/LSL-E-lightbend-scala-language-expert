/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

case class TravelStats(totalDelay: Int, totalEarlyArrivalTime: Int)

case class Train(info: TrainInfo, schedule: Seq[(Time, Station)]) {
  require(schedule.size >= 2, "schedule must contain at least two elements")
  require(isIncreasing(schedule.map(_._1)), "schedule must be strictly increasing in time")

  val stations: Seq[Station] =
    // Could also be expressed in short notation: schedule map (_._2)
    schedule.map(stop => stop._2)

  def timeAt(station: Station): Option[Time] =
    // Could also be expressed in notation: schedule find (_._2 == station) map (_._1)
    schedule.find(stop => stop._2 == station).map(found => found._1)

  val backToBackStations: Seq[(Station, Station)] =
    stations.zip(stations.tail)

  def travelStats(travelTimeSchedule: Seq[Time]): TravelStats = {
    require(travelTimeSchedule.size >= 2, "A train travel time schedule should have at least two elements")
    require(isIncreasing(travelTimeSchedule), "Travel time schedule should be strictly increasing in time")
    import scala.math.max

    val (totalDelay, totalEATime) = (schedule.map(_._1) zip travelTimeSchedule).tail.foldLeft((0, 0)) {
      case ((accDelay, accEarlyArrival), (schedTime, actualTime)) if (actualTime > schedTime) =>
        (accDelay + (actualTime - schedTime), accEarlyArrival)

      case ((accDelay, accEarlyArrival), (schedTime, actualTime)) =>
        (accDelay, accEarlyArrival + (schedTime - actualTime))
    }
    TravelStats(totalDelay, totalEATime)
  }

  val departureTimes: Map[Station, Time] =
    schedule.view.map(_.swap).to(Map)

}

object Station {
  implicit def stringToStation(s: String): Station =
    Station(s)
}

case class Station(name: String) extends AnyVal

sealed abstract class TrainInfo {
  def number: Int
}
case class InterCityExpress(number: Int, hasWifi: Boolean = false) extends TrainInfo
case class RegionalExpress(number: Int) extends TrainInfo
case class BavarianRegional(number: Int) extends TrainInfo
