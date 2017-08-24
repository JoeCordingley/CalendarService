package com.joecordingley.calendar

import org.scalatest.{FreeSpec, Matchers}
import java.time._
import java.time.DayOfWeek._
import java.time.Month._
/**
  * Created by joe on 11/08/17.
  */

class DayUtilTest extends FreeSpec with Matchers{
  import DayUtil._
  def weekendDay(d:LocalDate):Boolean = d.getDayOfWeek == SATURDAY || d.getDayOfWeek == SUNDAY
  def weekDay(d:LocalDate):Boolean = !weekendDay(d)
  def christmasDay(year:Int):LocalDate = LocalDate.of(year,DECEMBER,25)

  "first" - {
    "weekDay" - {
      "onOrBefore" - {
        "Saturday 12th August 2017 should be Friday 11th August 2017" in {
          val input = LocalDate.of(2017,AUGUST,12)
          val expected = LocalDate.of(2017,AUGUST,11)
          val actual = first(weekDay).onOrBefore(input)
          actual should equal(expected)
        }
        "Christmas Day" - {
          val input = christmasDay _
          "2017 should be Monday 25th of December" in {
            val expected = LocalDate.of(2017,DECEMBER,25)
            val actual = first(weekDay).onOrBefore(input)(2017)
            actual should equal(expected)
          }
          "2016 should be Friday 23rd of December" in {
            val expected = LocalDate.of(2016,DECEMBER,23)
            val actual = first(weekDay).onOrBefore(input)(2016)
            actual should equal(expected)
          }
        }
      }
      "onOrAfter" - {
        "Saturday 12th August 2017 should be Monday 14th August 2017" in {
          val input = LocalDate.of(2017,AUGUST,12)
          val expected = LocalDate.of(2017,AUGUST,14)
          val actual = first(weekDay).onOrAfter(input)
          actual should equal(expected)

        }
        "Christmas Day" - {
          "2017 should be Monday 25th of December" in {
            val expected = LocalDate.of(2017,DECEMBER,25)
            val actual = first(weekDay).onOrAfter(christmasDay _)(2017)
            actual should equal(expected)
          }
          "2016 should be Monday 26rd of December" in {
            val expected = LocalDate.of(2016,DECEMBER,26)
            val actual = first(weekDay).onOrAfter(christmasDay _)(2016)
            actual should equal(expected)
          }
        }
      }
      "in" - {
        "August" - {
          "2017 should be Tuesday 1st of August 2017" in {
            val expected = LocalDate.of(2017,AUGUST,1)
            val actual = first(weekDay).in(AUGUST)(2017)
            actual should equal(expected)
          }
        }
        "July" - {
          "2017" - {
            "should be Monday 3rd of July 2017" in {
              val expected = LocalDate.of(2017,JULY,3)
              val actual = first(weekDay).in(JULY)(2017)
              actual should equal(expected)
            }
          }
        }

      }
    }
    "MONDAY" - {
      "in" - {
        "August" - {
          "2017" - {
            "should be Monday 7th August 2017" in {
              val expected = LocalDate.of(2017,AUGUST,7)
              val actual = first(MONDAY).in(AUGUST)(2017)
              actual should equal(expected)
            }
          }
        }
      }
    }
  }
  "second" - {
    "Tuesday" - {
      "in" - {
        "August" - {
          "2017" - {
            "should be Tuesday 8th August 2017" in {
              val expected = LocalDate.of(2017,AUGUST,8)
              val actual = second(TUESDAY).in(AUGUST)(2017)
              actual should equal(expected)
            }
          }
        }
      }
    }
  }
  "last" - {
    "weekday" - {
      "in" - {
        "February" - {
          "2016" - {
            "should be Monday 29th of February" in {
              val expected = LocalDate.of(2016,FEBRUARY,29)
              val actual = last(weekDay).in(FEBRUARY)(2016)
              actual should equal (expected)
            }
          }
        }
      }
    }

  }


}
