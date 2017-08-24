package com.joecordingley.calendar

import java.time._
import java.time.DayOfWeek._
import java.time.Month._

import de.jollyday.util.CalendarUtil
import DayUtil._

object Holidays {


  val newYearsDay: DayOfYear = first(day).in(JANUARY)
  val newYearsDayBankHoliday: DayOfYear = first(weekday).onOrAfter(newYearsDay)
  val easterSunday:DayOfYear = new CalendarUtil().getEasterSunday(_)
  val goodFriday : DayOfYear= first(FRIDAY).onOrBefore(easterSunday)
  val easterMonday: DayOfYear = first(MONDAY).onOrAfter(easterSunday)
  val earlyMayBankHoliday:DayOfYear = first(MONDAY).in(MAY)
  val springBankHoliday:DayOfYear = last(MONDAY).in(MAY)
  val augustBankHoliday:DayOfYear = first(MONDAY).in(AUGUST)
  val christmasDay:DayOfYear = LocalDate.of(_,DECEMBER,25)
  val christmasDayBankHoliday: DayOfYear = first(weekday).onOrAfter(christmasDay)
  val boxingDayBankHoliday: DayOfYear = first(weekday).onOrAfter(christmasDayBankHoliday)

  val englandBankHolidays:List[DayOfYear] = List(
    newYearsDayBankHoliday,
    goodFriday,
    easterMonday,
    earlyMayBankHoliday,
    springBankHoliday,
    augustBankHoliday,
    christmasDayBankHoliday,
    boxingDayBankHoliday
  )
  val bankHolidayPredicates: List[DayPredicate] = englandBankHolidays map dayOfYearToPredicate

  val bankHoliday: DayPredicate = date => bankHolidayPredicates.foldLeft(false)(_||_(date))

  val workingDay: DayPredicate = date => weekday(date) && !bankHoliday(date)
  val nonWorkingDay: DayPredicate = !workingDay(_)
  val workingDaysFrom: DaysFrom = weekDays(_).filterNot(bankHoliday)

}
