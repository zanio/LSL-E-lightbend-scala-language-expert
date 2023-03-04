/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

object TrainDSL {

  sealed trait TrainBuildState
  trait TrainInfoFrom extends TrainBuildState
  trait TrainInfo2From extends TrainBuildState

  sealed trait TrainValidatedOrSingleStation
  trait TrainValidated extends TrainValidatedOrSingleStation
  trait TrainInfoAt extends TrainValidatedOrSingleStation

  case class TrainState[+TrainBuildState] private[TrainDSL](info: TrainInfo,
                                                            schedule: Vector[(Time, Station)] = Vector.empty[(Time, Station)],
                                                            initialDepartureTime: Option[Time] = None)

  implicit class TrainInfoOps(val ti: TrainInfo) extends AnyVal {
    def at(departureTime: Time): TrainState[TrainInfoFrom] =
      TrainState[TrainInfoFrom](info = ti, initialDepartureTime = Some(departureTime))
  }

  implicit class TrainInfoFromOps(val base: TrainState[TrainInfoFrom]) extends AnyVal {
    def from(station: Station): TrainState[TrainInfoAt] =
      TrainState[TrainInfoAt](info = base.info, schedule = Vector((base.initialDepartureTime.get, station)))
  }

  implicit class TrainValidatedOrSingleStationOps(val ti: TrainState[TrainValidatedOrSingleStation]) extends AnyVal {
    def at(departureTime: Time): TrainState[TrainInfo2From] =
      TrainState[TrainInfo2From](info = ti.info, schedule = ti.schedule, initialDepartureTime = Some(departureTime))
  }

  implicit class TrainInfo2FromOps(val base: TrainState[TrainInfo2From]) extends AnyVal {
    def from(station: Station): TrainState[TrainValidated] =
      TrainState[TrainValidated](info = base.info, schedule = base.schedule :+ (base.initialDepartureTime.get, station))
  }

  implicit def trainValidatedToTrain(tsh: TrainState[TrainValidated]): Train = {
    Train(tsh.info, tsh.schedule)
  }
}

object TrainDSL_Standard {

  implicit class TrainInfoOps(val info: TrainInfo) extends AnyVal {
    def at(time: Time): TrainInfoFromOps =
      new TrainInfoFromOps(info, time)
  }

  class TrainInfoFromOps private[TrainDSL_Standard] (info: TrainInfo, time: Time) {
    def from(station: Station): TrainInfoAtOps =
      new TrainInfoAtOps(info, Vector(time -> station))
  }

  class TrainInfoAtOps private[TrainDSL_Standard] (info: TrainInfo, schedule: Seq[(Time, Station)]) {
    def at(time: Time): TrainFromOps =
      new TrainFromOps(info, schedule, time)
  }

  class TrainFromOps private[TrainDSL_Standard] (info: TrainInfo, schedule: Seq[(Time, Station)], time: Time) {
    def from(station: Station): Train =
      Train(info, schedule :+ time -> station)
  }

  implicit class TrainOps(val train: Train) extends AnyVal {
    def at(time: Time): TrainFromOps =
      new TrainFromOps(train.info, train.schedule, time)
  }
}

