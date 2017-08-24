package com.joecordingley.calendar

import com.joecordingley.calendar.DayUtil._
import java.time._
import com.joecordingley.calendar.Holidays._

/**
  * Created by joe on 12/08/17.
  */
object Routine {

  def nthOfMonth(day:Int)(month:Month)(year:Int):LocalDate = LocalDate.of(year,month,day)
  def nthOfMonthBroughtForward(day:Int)(month:Month)(year:Int):LocalDate = bringForwardIfNotWorkingDay(nthOfMonth(day)(month)(year))
  def nthOfMonthMovedLater(day:Int)(month: Month)(year:Int):LocalDate = moveLaterIfNotWorkingDay(nthOfMonth(day)(month)(year))
  def nthOfEveryMonth(n:Int,nonWorkingDayStrategy: NonWorkingDayStrategy):DaysFrom =
    every(dayOfMonthToPredicate(nthOfMonth(n)),nonWorkingDayStrategy)
  def bringForwardIfNotWorkingDay(date:LocalDate):LocalDate = first(workingDay).onOrBefore(date)
  def moveLaterIfNotWorkingDay(date:LocalDate):LocalDate = first(workingDay).onOrAfter(date)
  val firstWorkingDayOfEveryMonth: DaysFrom = nthOfEveryMonth(1,MoveLater)
  def lastWorkingDayOfTheMonth(month:Month)(year:Int): LocalDate = bringForwardIfNotWorkingDay(lastDayOfTheMonth(month)(year))
  val isLastWorkingDayOfTheMonth: DayPredicate = dayOfMonthToPredicate(lastWorkingDayOfTheMonth)
  val lastWorkingDayOfEveryMonth: DaysFrom = allDays(_).filter(isLastWorkingDayOfTheMonth)
  def every(dayPredicate: DayPredicate,nonWorkingDayStrategy: NonWorkingDayStrategy=Keep):DaysFrom = {
    val x: DaysFrom = allDays(_) filter dayPredicate
    nonWorkingDayStrategy match {
      case Keep => x
      case Remove => x(_) filter workingDay
      case BringForward => x(_) map bringForwardIfNotWorkingDay
      case MoveLater => x(_) map moveLaterIfNotWorkingDay
    }
  }

}

sealed trait NonWorkingDayStrategy
case object BringForward extends NonWorkingDayStrategy
case object MoveLater extends NonWorkingDayStrategy
case object Remove extends NonWorkingDayStrategy
case object Keep extends NonWorkingDayStrategy
