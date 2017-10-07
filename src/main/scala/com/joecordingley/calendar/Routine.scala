package com.joecordingley.calendar

import com.joecordingley.calendar.DayUtil._
import com.joecordingley.calendar.Holidays._
import java.time._


/**
  * Created by joe on 12/08/17.
  */
object Routine {


//  def nthOfMonth(day:Int)(month:Month)(year:Int):LocalDate = LocalDate.of(year,month,day)
  def nthOfMonth(n:Int): DayOfMonth = month => year => LocalDate.of(year,month,n)
//  def nthOfMonthBroughtForward(day:Int)(month:Month)(year:Int):HolidayReader[LocalDate] = bringForwardIfNotWorkingDay.map(_(nthOfMonth(day)(month)(year)))
//  def nthOfMonthMovedLater(day:Int)(month: Month)(year:Int):HolidayReader[LocalDate] = moveLaterIfNotWorkingDay map (_(nthOfMonth(day)(month)(year)))
  def nthOfEveryMonth(n:Int,nonWorkingDayStrategy: NonWorkingDayStrategy)(implicit holidayRepository: HolidayRepository):DaysFrom =
    every(nthOfMonth(n),nonWorkingDayStrategy)
  def bringForwardIfNotWorkingDay(day:LocalDate)(implicit holidayRepository: HolidayRepository):LocalDate =
    first(holidayRepository.workingDay).onOrBefore(day)
  def moveLaterIfNotWorkingDay(implicit holidayRepository: HolidayRepository):LocalDate=>LocalDate =
    first(holidayRepository.workingDay).onOrAfter(_)
  def firstWorkingDayOfEveryMonth(implicit holidayRepository:HolidayRepository): DaysFrom =
    nthOfEveryMonth(1,MoveLater)
  def lastWorkingDayOfTheMonth(implicit holidayRepository: HolidayRepository): DayOfMonth = month => year =>
    bringForwardIfNotWorkingDay(lastDayOfTheMonth(month)(year))
  def lastWorkingDayOfEveryMonth(implicit holidayRepository: HolidayRepository): DaysFrom =
    allDays(_) filter lastWorkingDayOfTheMonth
  def every(dayPredicate: DayPredicate)(implicit holidayRepository: HolidayRepository):DaysFrom =
    allDays(_) filter dayPredicate
  def every(dayPredicate: DayPredicate,nonWorkingDayStrategy: NonWorkingDayStrategy)
           (implicit holidayRepository: HolidayRepository):DaysFrom = {
    val unchanged = every(dayPredicate)
    nonWorkingDayStrategy match {
      case KeepUnchanged => unchanged
      case MoveLater => unchanged(_) map moveLaterIfNotWorkingDay
      case BringForward => unchanged(_) map bringForwardIfNotWorkingDay
      case Remove => unchanged(_) filter holidayRepository.workingDay
    }
  }

}

sealed trait NonWorkingDayStrategy
case object BringForward extends NonWorkingDayStrategy
case object MoveLater extends NonWorkingDayStrategy
case object Remove extends NonWorkingDayStrategy
case object KeepUnchanged extends NonWorkingDayStrategy
