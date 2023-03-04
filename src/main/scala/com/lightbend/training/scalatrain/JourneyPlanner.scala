/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

import scala.concurrent.{Future, ExecutionContext}

class JourneyPlanner(trains: Set[Train]) {

  val stations: Set[Station] =
    trains.flatMap(train => train.stations)

  val hops: Map[Station, Set[Hop]] = {
    val hops = for {
      train <- trains
      (from, to) <- train.backToBackStations
    } yield Hop.createHop(from, to, train)
    hops groupBy (_.from)
  }

  def trainsAt(station: Station): Set[Train] =
    trains.filter(train => train.stations.contains(station))

  def stopsAt(station: Station): Set[(Time, Train)] =
    for {
      train <- trains
      time <- train.timeAt(station)
    } yield (time, train)

  def isShortTrip(from: Station, to: Station): Boolean =
    trains.exists(train =>
      train.stations.dropWhile(station => station != from) match {
        case `from` +: `to` +: _      => true
        case `from` +: _ +: `to` +: _ => true
        case _                        => false
      }
    )

  // Select one of the alternative implementations of the connections methods
  val connections: (Station, Station, Time) => Set[Vector[Hop]] = connectionsTailrec_1 _

  // Tail recursive implementation 1
  def connectionsTailrec_1(from: Station, to: Station, departureTime: Time): Set[Vector[Hop]] = {
    require(from != to, "from and to must not be equal!")

    @scala.annotation.tailrec
    def connections(foundConnections: Set[Vector[Hop]], searching: Vector[Vector[Hop]]): Set[Vector[Hop]] = {
      searching match {
        case hopsSoFar +: remaining =>
          val lastHop = hopsSoFar.last
          if (lastHop.to == to)
            connections(foundConnections + hopsSoFar, remaining)
          else {
            val soFarStations = hopsSoFar.head.from +: hopsSoFar.map(_.to)
            val nextHops = hops.getOrElse(lastHop.to, Set())
              .filter(hop => (hop.departureTime >= lastHop.arrivalTime) && !(soFarStations contains hop.to))
            connections(foundConnections, remaining ++ nextHops.map(hop => hopsSoFar :+ hop).toVector)
          }
        case _ =>
          foundConnections
      }
    }
    val nextHops = hops.getOrElse(from, Set()).filter (_.departureTime >= departureTime)
    connections(Set.empty[Vector[Hop]], nextHops.map(hop => Vector(hop)).toVector)
  }

  // Tail recursive implementation 1 with optimisation: no recalculation of soFarStations
  def connectionsTailrecMemo_1(from: Station, to: Station, departureTime: Time): Set[Vector[Hop]] = {
    require(from != to, "from and to must not be equal!")

    @scala.annotation.tailrec
    def connections(foundConnections: Set[Vector[Hop]], searching: Vector[(Vector[Hop], Set[Station])]): Set[Vector[Hop]] = {
      searching match {
        case (hopsSoFar, soFarStations) +: remaining =>
          val lastHop = hopsSoFar.last
          if (lastHop.to == to)
            connections(foundConnections + hopsSoFar, remaining)
          else {
            val nextHops = hops.getOrElse(lastHop.to, Set())
              .filter(hop => (hop.departureTime >= lastHop.arrivalTime) && !(soFarStations contains hop.to))
            connections(foundConnections, remaining ++ nextHops.map(hop => (hopsSoFar :+ hop, soFarStations + lastHop.to)).toVector)
          }
        case _ =>
          foundConnections
      }
    }
    val nextHops = hops.getOrElse(from, Set()).filter (_.departureTime >= departureTime)
    connections(Set.empty[Vector[Hop]], nextHops.map(hop => (Vector(hop), Set(hop.from, hop.to))).toVector)
  }

  // Tail recursive implementation 2
  def connectionsTailrec_2(from: Station, to: Station, departureTime: Time): Set[Vector[Hop]] = {
    require(from != to, "from and to must not be equal!")

    @scala.annotation.tailrec
    def connections(foundConnections: Set[Vector[Hop]], searching: Set[Vector[Hop]]): Set[Vector[Hop]] = {
      if (searching.isEmpty)
        foundConnections
      else {
        val (hopsSoFar, remaining) = (searching.head, searching.tail)
        val lastHop = hopsSoFar.last
        val soFarStations = hopsSoFar.head.from +: hopsSoFar.map(_.to)
        val nextHops =
          hops.getOrElse(lastHop.to, Set())
            .filter(hop => (hop.departureTime >= lastHop.arrivalTime) && !(soFarStations contains hop.to))
        if (nextHops.isEmpty)
          connections(foundConnections, remaining)
        else {
          val (toStationReached, toStationNotReached) = nextHops.partition(hop => hop.to == to)
          connections(foundConnections ++ toStationReached.map(hop => hopsSoFar :+ hop),
                      remaining ++ toStationNotReached.map(hop => hopsSoFar :+ hop)
          )
        }
      }
    }
    val nextHops = hops.getOrElse(from, Set()).filter (_.departureTime >= departureTime)
    val (toStationReached, toStationNotReached) = nextHops.partition(hop => hop.to == to)
    connections(toStationReached.map(Vector(_)), toStationNotReached.map(hop => Vector(hop)))
  }

  def log(msg: String): Unit = {
    println(s"${System.currentTimeMillis()} - ${Thread.currentThread.getName}: $msg")
  }

  // An asynchronous version using Futures
  def connectionsAsync(from: Station, to: Station, departureTime: Time)(implicit ec: ExecutionContext): Future[Set[Vector[Hop]]] = {
    require(from != to, "from and to must not be equal!")
    def connections(soFarF: Future[Vector[Hop]]): Future[Set[Vector[Hop]]] = {

      soFarF.flatMap { soFar =>
        //log(s"connections: ${soFar}")
        if (soFar.last.to == to)
          Future.successful(Set(soFar))
        else {
          val soFarStations = soFar.head.from +: (soFar map (_.to))
          val nextHops = hops.getOrElse(soFar.last.to, Set()) filter (hop =>
            (hop.departureTime >= soFar.last.arrivalTime) && !(soFarStations contains hop.to)
            )
          val posConnections = nextHops.map(hop => soFar :+ hop)
          Future.sequence(posConnections.map(posConnection => connections(Future.successful(posConnection)))).map(_.flatten)
        }
      }
    }
    val nextHops = hops.getOrElse(from, Set()) filter (_.departureTime >= departureTime)
    val posConnections = nextHops.map(Vector(_))

    Future.sequence(posConnections.map(posConnection => connections(Future.successful(posConnection)))).map(_.flatten)
  }

  // Non-tail recursive solution
  def connectionsNonTailrec(from: Station, to: Station, departureTime: Time): Set[Vector[Hop]] = {
    require(from != to, "from and to must not be equal!")
    def connections(soFar: Vector[Hop]): Set[Vector[Hop]] = {
      if (soFar.last.to == to)
        Set(soFar)
      else {
        val soFarStations = soFar.head.from +: (soFar map (_.to))
        val nextHops = hops.getOrElse(soFar.last.to, Set()) filter (hop =>
          (hop.departureTime >= soFar.last.arrivalTime) && !(soFarStations contains hop.to)
        )
        nextHops flatMap (hop => connections(soFar :+ hop))
      }
    }
    val nextHops = hops.getOrElse(from, Set()) filter (_.departureTime >= departureTime)
    nextHops flatMap (hop => connections(Vector(hop)))
  }
}

object Hop {
  def createHop(from: Station, to: Station, train: Train): Hop = new Hop(from, to, train.info) {
    require(from != to, "From and To stations must be distinct")
    require(train.backToBackStations.contains(from -> to), "From and To stations must be back to back")
    override val departureTime: Time = train.departureTimes(from)
    override val arrivalTime: Time = train.departureTimes(to)
  }
}

abstract class Hop protected (val from: Station, val to: Station, val trainInfo: TrainInfo) {
  val departureTime: Time
  val arrivalTime: Time

  override def toString: String = s"Hop($from, $to, $trainInfo)"

  override def hashCode: Int = (from, to, trainInfo).##

  override def equals(that: Any): Boolean = {
    that match {
      case other: Hop =>
        (from == other.from) &&
          (to == other.to) &&
          (trainInfo == other.trainInfo)
      case _ => false
    }
  }
}
