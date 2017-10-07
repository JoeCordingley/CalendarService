package com.joecordingley.calendar

import java.time.LocalDate
import java.time.Month._
import com.joecordingley.calendar.EnglandHolidays._

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by joe on 12/08/17.
  */
class EnglandHolidaysTest extends FreeSpec with Matchers {

  "new years day bank holiday" - {
    "2017" - {
      "should be Monday 2nd January" in {
        val expected = LocalDate.of(2017,JANUARY,2)
        val actual = newYearsDayBankHoliday(2017)
        actual should equal (expected)
      }
    }
    "2016" - {
      "should be Friday 1st January" in {
        val expected = LocalDate.of(2016,JANUARY,1)
        val actual = newYearsDayBankHoliday(2016)
        actual should equal (expected)
      }

    }
  }
  "Good Friday" - {
    "2017" - {
      "should be Friday April 14th" in {
        val expected = LocalDate.of(2017,APRIL,14)
        val actual = goodFriday(2017)
        actual should equal (expected)

      }
    }

  }
  "spring bank holiday" - {
    "2017" - {
      "should be 29th May" in {
        val expected = LocalDate.of(2017,MAY,29)
        val actual = springBankHoliday(2017)
        actual should equal (expected)
      }
    }
  }

}
