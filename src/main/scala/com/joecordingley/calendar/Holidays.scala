package com.joecordingley.calendar

import java.time._
import java.time.DayOfWeek._
import java.time.Month._

import de.jollyday.util.CalendarUtil
import DayUtil._
import cats.data.Reader

object EnglandHolidays extends HolidayRepository{


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

  override def holidays:List[DayOfYear] = List(
    newYearsDayBankHoliday,
    goodFriday,
    easterMonday,
    earlyMayBankHoliday,
    springBankHoliday,
    augustBankHoliday,
    christmasDayBankHoliday,
    boxingDayBankHoliday
  )

}
object Holidays {
  type HolidayReader[A] = Reader[HolidayRepository,A]
  def workingDayReader:HolidayReader[DayPredicate] = Reader(_.workingDay)
/*  def bankHoliday: HolidayReader[DayPredicate] = for {
    holidays <- getHolidays
  } yield {date:LocalDate =>
    (holidays map dayOfYearToPredicate).foldLeft(false)(_||_(date))
  }
  def getHolidays: HolidayReader[List[DayOfYear]]= Reader(_.holidays)
  def workingDayR: HolidayReader[DayPredicate]= for {
    bh <- bankHoliday
  } yield { date:LocalDate =>
    weekday(date) && !bh(date)
  }
  def nonWorkingDay: HolidayReader[DayPredicate] = workingDay.map(predicate => !predicate(_))*/
}
trait HolidayRepository {
  def holidays:List[DayOfYear]
  def bankHoliday: DayPredicate = date =>(holidays map dayOfYearToPredicate).foldLeft(false)(_||_(date))
  def workingDay: DayPredicate = date => weekday(date) && !bankHoliday(date)
  def nonWorkingDay:DayPredicate = !workingDay(_)
}
