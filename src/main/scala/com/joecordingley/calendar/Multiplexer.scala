package com.joecordingley.calendar

import akka.actor.{Actor, ActorRef}
import com.joecordingley.calendar.Scheduler.Payload
import Multiplexer._

class Multiplexer(initialState:State) extends Actor{

  var state:State = initialState
  override def receive: Receive = {
    case UpdateState(newState) => state = newState
    case x:Payload => state.receivers.foreach{
      case(predicate,actorRef) if predicate(x) => actorRef ! x
    }

  }


}
object Multiplexer {
  case class State(receivers:Map[ReceiverPredicate,ActorRef])
  case class UpdateState(state: State) extends MultiplexerMessage

  case class ReceiverState(receiver: ActorRef,predicates:Map[ScheduleKey,ReceiverPredicate])
  sealed trait ReceiverKey
  sealed trait ScheduleKey
  type ReceiverPredicate = Payload => Boolean
  trait MultiplexerMessage
  case class AddReceiver(receiverKey: ReceiverKey,actorRef: ActorRef) extends MultiplexerMessage
  case class AddSchedule(scheduleKey: ScheduleKey,receiverPredicate: ReceiverPredicate) extends MultiplexerMessage
  case class RemoveSchedule(scheduleKey: ScheduleKey) extends MultiplexerMessage
  case class RemoveReceiver(receiverKey: ReceiverKey) extends MultiplexerMessage
  case object GetSchedules


}
