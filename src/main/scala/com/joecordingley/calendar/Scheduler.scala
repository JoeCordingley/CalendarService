package com.joecordingley.calendar


import java.util.{Timer, TimerTask}
import java.util.Date
import java.time._
import java.util.concurrent.ScheduledFuture
import scala.concurrent.Await

import scala.concurrent.{ExecutionContext, Future}
/**
  * Created by joe on 13/08/17.
  */
object Scheduler {

  def addSchedule(s:Stream[ScheduledTask])(implicit ec: ExecutionContext):Unit = {
    val f1s = s.map(task => DelayedFuture(task.instant) map (_ => task.task()))
    StreamUtil.sequence2(f1s)
    ()
  }

}
case class ScheduledTask(instant: Instant, task:()=>Unit)
