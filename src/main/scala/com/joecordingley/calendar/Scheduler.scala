package com.joecordingley.calendar

import java.time.Instant

import akka.actor.{Actor, ActorRef}
import Scheduler._

import scala.concurrent.ExecutionContext

/**
  * Created by joe on 12/09/17.
  */
class Scheduler(receiver: ActorRef)(implicit val executionContext: ExecutionContext) extends Actor{

  var state:State = Idle

  implicit val instantOrdering: Ordering[Instant] = Ordering.by(_.toEpochMilli)

  implicit val timeAndMessageOrdering: Ordering[TimeAndMessage] = Ordering.by(_.instant)
  import Ordered.orderingToOrdered

  override def receive: Receive = {
    case m:SchedulerMessage=> (m,state) match {
      case (message:Payload, ScheduleState(_,_, schedule)) => {
        receiver ! message
        state = initialize(schedule)
      }
      case (_:Payload,Idle) => println("oh noes") //TODO
      case (UpdateSchedule(newSchedule), scheduleState: ScheduleState) => state = reinitialize(newSchedule, scheduleState)
      case (UpdateSchedule(schedule), Idle) => state = initialize(schedule)
    }
  }

  def initialize(schedule: TimeAndMessageStream):State = schedule match {
    case Stream.Empty => Idle
    case x#:: xs => ScheduleState(
      delayedCancellable = scheduleOne(x),
      currentlyWaitingFor = x,
      restOfTheSchedule = xs
    )

  }

  def reinitialize(newSchedule:TimeAndMessageStream, scheduleState:ScheduleState):State = {
    val currentCancellable = scheduleState.delayedCancellable
    val currentlyWaitingFor = scheduleState.currentlyWaitingFor

    newSchedule match {
      case timeAndMessage #::_ if timeAndMessage >= currentlyWaitingFor =>
        scheduleState.copy(restOfTheSchedule = newSchedule)
      case _ => cancelAndSchedule(currentCancellable,newSchedule)
    }
  }

  def cancelAndSchedule(cancellable: DelayedCancellable[Payload], schedule:TimeAndMessageStream):State = {
    cancellable.cancel getOrElse println("argh") //TODO
    initialize(schedule)
  }

  def scheduleOne(w: TimeAndMessage):DelayedCancellable[Payload] ={
    val cancellable = DelayedCancellable(w.instant,w.message)
    cancellable.future.foreach{case message => self ! message}
    cancellable
  }

}
object Scheduler {
  sealed trait State
  case object Idle extends State
  type TimeAndMessageStream = Stream[TimeAndMessage]
  case class TimeAndMessage(instant: Instant, message: Payload)
  sealed trait SchedulerMessage
  case class UpdateSchedule(schedule: TimeAndMessageStream) extends SchedulerMessage
  trait Payload extends SchedulerMessage
  case class ScheduleState(delayedCancellable: DelayedCancellable[Payload],
                           currentlyWaitingFor: TimeAndMessage,
                           restOfTheSchedule: TimeAndMessageStream
                          ) extends State {
    val wholeSchedule: Stream[TimeAndMessage] = currentlyWaitingFor #:: restOfTheSchedule
  }
}


