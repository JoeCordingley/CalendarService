package com.joecordingley.calendar

import java.time.Instant

import akka.actor.{Actor, ActorRef, FSM}
import Scheduler._

import scala.concurrent.ExecutionContext
import scala.util.{Failure,Success}

/**
  * Created by joe on 12/09/17.
  */
class Scheduler(implicit val executionContext: ExecutionContext) extends Actor{

  var state:State = Idle

  implicit val instantOrdering: Ordering[Instant] = Ordering.by(_.toEpochMilli)

  implicit val whenToSendWhoWhatOrdering: Ordering[WhenToSendWhoWhat] = Ordering.by(_.instant)
  import Ordered.orderingToOrdered


  override def receive: Receive = {
    case m:SchedulerMessage=> (m,state) match {
      case (WhoToSendWhat(receiver, message), ScheduleState(_,_, schedule)) => {
        receiver ! message
        state = initialize(schedule)
      }
      case (WhoToSendWhat(_,_),Idle) => println("oh noes") //TODO
      case (AddSchedule(newSchedule), scheduleState: ScheduleState) => state = merge(newSchedule, scheduleState)
      case (AddSchedule(schedule), Idle) => state = initialize(schedule)
      case (RemoveSchedules(_),Idle) => ()
      case (RemoveSchedules(predicate),scheduleState:ScheduleState) => state = remove(predicate,scheduleState)
    }
  }

  def remove(predicate:Predicate, scheduleState: ScheduleState):State = {
    val currentMessage = scheduleState.currentlyWaitingFor.whoToSendWhat.message

    val newSchedule = scheduleState.restOfTheSchedule.filterNot{
      case WhenToSendWhoWhat(_,WhoToSendWhat(_,message)) => predicate(message)
    }

    if (predicate(currentMessage))
      cancelAndSchedule(scheduleState.delayedCancellable,newSchedule)
    else
      scheduleState.copy(restOfTheSchedule = newSchedule)

  }


  def initialize(schedule: Schedule):State = schedule match {
    case Stream.Empty => Idle
    case x#:: xs => ScheduleState(
      delayedCancellable = scheduleOne(x),
      currentlyWaitingFor = x,
      restOfTheSchedule = xs
    )

  }

  def merge(newSchedule:Schedule, scheduleState:ScheduleState):State = {
    val currentCancellable = scheduleState.delayedCancellable
    val currentlyWaitingFor = scheduleState.currentlyWaitingFor

    newSchedule match {
      case w#::_ if w >= currentlyWaitingFor =>
        ScheduleState(
          delayedCancellable = currentCancellable,
          currentlyWaitingFor = currentlyWaitingFor,
          restOfTheSchedule = StreamUtil.merge(scheduleState.restOfTheSchedule,newSchedule)
        )
      case w#::_ if w < currentlyWaitingFor =>
        val mergedSchedule = StreamUtil.merge(newSchedule,scheduleState.wholeSchedule)
        cancelAndSchedule(currentCancellable,mergedSchedule)
      case Stream.Empty => scheduleState
    }
  }

  def cancelAndSchedule(cancellable: DelayedCancellable[WhoToSendWhat],schedule:Schedule):State = {
    cancellable.cancel getOrElse println("argh") //TODO

    initialize(schedule)
  }

  def scheduleOne(w: WhenToSendWhoWhat):DelayedCancellable[WhoToSendWhat] ={
    val cancellable = DelayedCancellable(w.instant,w.whoToSendWhat)
    cancellable.future.foreach{case whoToSendWhat => self ! whoToSendWhat}
    cancellable
  }

}
object Scheduler {
  sealed trait State
  case object Idle extends State
  case class ScheduleState(delayedCancellable: DelayedCancellable[WhoToSendWhat],
                           currentlyWaitingFor: WhenToSendWhoWhat,
                           restOfTheSchedule: Schedule) extends State {
    val wholeSchedule: Stream[WhenToSendWhoWhat] = currentlyWaitingFor #:: restOfTheSchedule
  }
  type Schedule = Stream[WhenToSendWhoWhat]
  case class WhenToSendWhoWhat(instant: Instant,whoToSendWhat: WhoToSendWhat)
  sealed trait SchedulerMessage
  case class WhoToSendWhat(receiver: ActorRef, message: Message) extends SchedulerMessage
  case class AddSchedule(schedule: Schedule) extends SchedulerMessage
  type Predicate = Message => Boolean
  case class RemoveSchedules(predicate:Predicate) extends SchedulerMessage
  trait Message


}

