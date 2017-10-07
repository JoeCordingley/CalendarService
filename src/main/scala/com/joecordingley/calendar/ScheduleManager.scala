package com.joecordingley.calendar

import java.time.Instant

import ScheduleManager._
import akka.actor.ActorRef
import com.joecordingley.calendar.Scheduler.{TimeAndMessageStream, TimeAndMessage}
import cats.data.Reader

import scala.util.Try
/**
  * Created by joe on 19/09/17.
  */
case class ScheduleManager(scheduleMap: Map[ScheduleKey,TimeAndMessageStream], scheduler:ActorRef) {

  implicit val instantOrdering: Ordering[Instant] = Ordering.by(_.toEpochMilli)

  implicit val timeAndMessageOrdering: Ordering[TimeAndMessage] = Ordering.by(_.instant)
  val schedules = scheduleMap.values.foldLeft(Stream.empty[TimeAndMessage])(StreamUtil.merge(_,_))
  scheduler ! schedules

  def addSchedule(scheduleKey: ScheduleKey,schedule: TimeAndMessageStream):ScheduleManager =
    run(scheduleMap +(scheduleKey -> schedule))

  def removeSchedule(scheduleKey: ScheduleKey):ScheduleManager = run(scheduleMap - scheduleKey)

  def updateSchedule(scheduleKey: ScheduleKey, schedule: TimeAndMessageStream) = {
    val newMap = scheduleMap - scheduleKey + (scheduleKey -> schedule)
    run(newMap)
  }
  private def run(map:Map[ScheduleKey,TimeAndMessageStream]):ScheduleManager = ScheduleManager(map,scheduler)

}
object ScheduleManager {
  sealed trait ScheduleKey
  trait Actors {
    def scheduler:ActorRef
  }

  def apply(scheduler: ActorRef): ScheduleManager = new ScheduleManager(Map.empty,scheduler)
  val appp:Reader[Actors,ScheduleManager] = Reader((a:Actors)=>a.scheduler).map(ScheduleManager(Map.empty,_))
}
