package com.joecordingley.calendar

import java.time.Instant

import cats.free.Free
import cats.free.Free.liftF
import akka.actor.ActorRef
import com.joecordingley.calendar.ScheduleManager.ScheduleKey
import com.joecordingley.calendar.Scheduler.{Payload, TimeAndMessageStream}

/**
  * Created by joe on 20/09/17.
  */
sealed trait SchedulerAlg[A]
case class AddSchedule(schedule: Schedule) extends SchedulerAlg[Unit]
case class DeleteSchedule(key:ScheduleKey) extends SchedulerAlg[Unit]
case object GetKeys extends SchedulerAlg[List[ScheduleKey]]

case class Schedule(key:ScheduleKey,times:Stream[Instant],receiver:ActorRef,payload: Payload)

object SchedulerFrees {
  type FreeSchedduler[A] = Free[SchedulerAlg,A]
  def addSchedule(schedule: Schedule):FreeSchedduler[Unit] = liftF(AddSchedule(schedule))
  def deleteSchedule(key: ScheduleKey):FreeSchedduler[Unit] = liftF(DeleteSchedule(key))
  def getKeys:FreeSchedduler[List[ScheduleKey]] = liftF(GetKeys)
}
