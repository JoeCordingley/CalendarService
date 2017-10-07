package com.joecordingley.calendar

import DayUtil._
import java.time.DayOfWeek._
import java.time.{LocalDate, LocalTime}
import java.time.Month._
import java.util.TimeZone

import Routine._
/**
  * Created by joe on 21/09/17.
  */

object Schedules {

  val example1 = {
    val startDate = LocalDate.of(2017,JANUARY,1)
    val timeZone = TimeZone.getDefault
    val sevenInTheEvening = LocalTime.of(19,0)
    val schedule = every(MONDAY,nonWorkingDayStrategy = MoveLater).from(startDate).at(sevenInTheEvening)
  }

}
