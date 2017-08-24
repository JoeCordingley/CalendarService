package com.joecordingley.calendar

import java.time._
import java.time.DayOfWeek._

/**
  * Created by joe on 10/08/17.
  */
object DayUtil {

  type DayPredicate = LocalDate => Boolean
  type DayOfYear = Int => LocalDate
  type DayOfMonth = Month => Int => LocalDate
  type DaysFrom = LocalDate => Stream[LocalDate]
  implicit class DaysFromMixins(daysFrom: DaysFrom) {
    def from(date:LocalDate): Stream[LocalDate] = daysFrom(date)
  }

  implicit def dayOfWeekToPredicate(d:DayOfWeek):DayPredicate = _.getDayOfWeek == d
  implicit def dayOfYearToPredicate(dayOfYear: DayOfYear):DayPredicate = day => dayOfYear(day.getYear) == day
  implicit def dayOfMonthToPredicate(dayOfMonth: DayOfMonth):DayPredicate = day => dayOfMonth(day.getMonth)(day.getYear) == day

  def nextDay:LocalDate=>LocalDate = _.plusDays(1)
  def previousDay:LocalDate => LocalDate = _.minusDays(1)

  val weekendDay: DayPredicate = { date =>
    val day = date.getDayOfWeek
    day == SATURDAY || day == SUNDAY
  }

  val weekday: DayPredicate = !weekendDay(_)
  val day:DayPredicate = _ => true

  val allDays: DaysFrom = Stream.iterate(_)(nextDay)
  val weekDays:DaysFrom = allDays(_) filter weekday

  def first(dayPredicate: DayPredicate):Count = Count(dayPredicate,1)
  def second(dayPredicate: DayPredicate):Count = Count(dayPredicate,2)
  def third(dayPredicate: DayPredicate):Count = Count(dayPredicate,3)
  def last(dayPredicate: DayPredicate):Last = Last(dayPredicate)

  private def iterate(date:LocalDate,count:Int,f:LocalDate => LocalDate,dayPredicate: DayPredicate) : LocalDate =
    (dayPredicate(date),count) match {
      case (true,1) => date
      case (true,_) => iterate(f(date),count-1,f,dayPredicate)
      case (false,_) => iterate(f(date),count,f,dayPredicate)
    }

  case class Last(dayPredicate: DayPredicate) {
    def in(month:Month)(year:Int):LocalDate = iterate(lastDayOfTheMonth(month)(year),1,previousDay,dayPredicate)
  }

  def lastDayOfTheMonth(month: Month)(year:Int)= {
    val isLeapYear = Year.isLeap(year)
    val amountOfDaysInMonth = month.length(isLeapYear)
    LocalDate.of(year,month,amountOfDaysInMonth)
  }

  case class Count(dayPredicate: DayPredicate, count:Int) {

    def onOrAfter(date:LocalDate):LocalDate = iterate(date,count,nextDay,dayPredicate)
    def onOrAfter(date:DayOfYear)(year: Int):LocalDate = onOrAfter(date(year))
    def onOrAfter(date:DayOfMonth)(month: Month)(year:Int):LocalDate = onOrAfter(date(month)(year))
    def onOrBefore(date:LocalDate):LocalDate = iterate(date,count,previousDay,dayPredicate)
    def onOrBefore(date:DayOfYear)(year:Int):LocalDate = onOrBefore(date(year))
    def onOrBefore(date:DayOfMonth)(month:Month)(year: Int):LocalDate = onOrBefore(date(month)(year))
    def in(month: Month)(year:Int):LocalDate = iterate(firstOfMonth(month,year),count,nextDay,dayPredicate)

    private def firstOfMonth(month: Month,year:Int) = LocalDate.of(year,month,1)
  }

}
