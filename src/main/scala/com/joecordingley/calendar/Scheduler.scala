package com.joecordingley.calendar


import java.util.{Timer, TimerTask}
import java.util.Date
import java.time._
import java.util.concurrent.ScheduledFuture

import com.joecordingley.calendar.Scheduler.Task

import scala.concurrent.Await
import scala.concurrent.{ExecutionContext, Future}
/**
  * Created by joe on 13/08/17.
  */
object Scheduler {
  type Task = () => Unit

  def addSchedule(s:Stream[ScheduledTask])(implicit ec: ExecutionContext):Unit = {
    val f1s = s.map(task => DelayedFuture(task.instant) map (_ => task.task()))
    StreamUtil.sequence2(f1s)
    ()
  }

//  def delayedTask(instant: Instant)(task: Task):Future[Task] = DelayedFuture(instant).map(_ => task)
//  def StreamFutures(s:Stream[ScheduledTask]):Stream[Future[Task]] = {
//    def go(previousFuture:Future[Task],s:Stream[ScheduledTask]):Stream[Future[Task]] = s match {
//      case Stream.Empty => Stream.empty
//      case schedule #:: schedules => {
//        val nextFuture = for {
//          tlast <- previousFuture
//          tnext <- delayedTask()
//
//        }
//      }
//    }
//  }

}
case class ScheduledTask(instant: Instant, task:Task)
