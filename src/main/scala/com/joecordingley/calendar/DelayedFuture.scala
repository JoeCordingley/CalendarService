package com.joecordingley.calendar

import java.time.temporal.TemporalAmount
import java.time.{Instant, LocalDateTime, OffsetDateTime}

import scala.concurrent.{Future, Promise}
import java.util.{Date, Timer, TimerTask}
import java.time.{Duration => JavaDuration}

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Created by joe on 14/08/17.
  */
object DelayedFuture {

  def apply(instant:Instant):Future[Unit] = {
    val date = new Date(instant.toEpochMilli)
    val timer = new Timer()
    val promise = Promise[Unit]()
    val timerTask = new TimerTask {
      override def run(): Unit = promise.success(())
    }
    timer.schedule(timerTask,date)
    promise.future
  }
  def apply(duration: FiniteDuration):Future[Unit] = {
    val now = Instant.now()
    val javaDuration:TemporalAmount = JavaDuration.ofNanos(duration.toNanos)
    val futureInstant = now.plus(javaDuration)
    DelayedFuture(futureInstant)
  }


}
