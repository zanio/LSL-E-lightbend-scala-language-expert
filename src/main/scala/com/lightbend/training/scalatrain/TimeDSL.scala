/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

object TimeDSL {

  private def twelveHourTimeRule(time: Time): Boolean =
    time >= Time(1) && time <= Time(12, 59)

  private def am(time: Time): Time = {
    require(twelveHourTimeRule(time), "time must be [01:00,12:59]!")
    time match {
      case Time(12, m) => Time(0, m)
      case t => t
    }
  }

  private def pm(time: Time): Time = {
    require(twelveHourTimeRule(time), "time must be within [01:00,12:59]!")
    time match {
      case t @ Time(12, m) => t
      case _ => time.copy(hours = time.hours + 12)
    }

  }

  implicit class IntOps(val n: Int) extends AnyVal {

    def ::(hours: Int): Time =
      Time(hours, n)

    def am: Time =
      TimeDSL.am(Time(n))

    def pm: Time =
      TimeDSL.pm(Time(n))
  }

  implicit class TimeOps(val time: Time) extends AnyVal {

    def am: Time =
      TimeDSL.am(time)

    def pm: Time =
      TimeDSL.pm(time)
  }
}
