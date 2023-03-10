/*
 * Copyright © 2012 - 2017 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.scalatrain

object Time {

  private val TimePattern = """(\d{1,2}):(\d{1,2})""".r

  def fromMinutes(minutes: Int): Time =
    Time(minutes / 60, minutes % 60)

  implicit def stringToTime(s: String): Time = {
    val TimePattern(hours, minutes) = s
    Time(hours.toInt, minutes.toInt)
  }
}

case class Time(hours: Int = 0, minutes: Int = 0) extends Ordered[Time] {
  require(hours >= 0 && hours <= 23, "hours must be within 0 and 23")
  require(minutes >= 0 && minutes <= 59, "minutes must be within 0 and 59")

  val asMinutes: Int =
    hours * 60 + minutes

  override lazy val toString: String =
    f"$hours%02d:$minutes%02d"

  def minus(that: Time): Int =
    this.asMinutes - that.asMinutes

  def -(that: Time): Int =
    minus(that)

  override def compare(that: Time): Int =
    this - that
}
