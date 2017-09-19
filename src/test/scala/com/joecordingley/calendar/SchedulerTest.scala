package com.joecordingley.calendar

import java.time.Instant

import akka.actor.{ActorSystem, Props}
import org.scalatest.{FreeSpec, Matchers, Payloads}
import akka.testkit.TestProbe
import scala.concurrent.duration._
import com.joecordingley.calendar.Scheduler.{Payload, TimeAndMessage, UpdateSchedule}

/**
  * Created by joe on 19/09/17.
  */
class SchedulerTest extends FreeSpec with Matchers {

  implicit private val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  private implicit val system = ActorSystem("SchedulerTest")
  "scheduler should schedule" in {
    val sender = TestProbe()
    implicit val senderRef = sender.ref
    val props = Props(classOf[Scheduler], senderRef, executionContext)
    val scheduler = system.actorOf(props)
    val oneSecondFromNow = Instant.now().plusSeconds(1)
    case object TestMessage extends Payload
    val timeAndMessage = TimeAndMessage(oneSecondFromNow,TestMessage)
    val schedule = Stream(timeAndMessage)
    scheduler ! UpdateSchedule(schedule)
    sender.expectMsg(TestMessage)
  }

  "scheduler should be able to cancel" in {
    val sender = TestProbe()
    implicit val senderRef = sender.ref
    val props = Props(classOf[Scheduler], senderRef, executionContext)
    val scheduler = system.actorOf(props)
    val oneSecondFromNow = Instant.now().plusSeconds(1)
    case object TestMessage extends Payload
    val timeAndMessage = TimeAndMessage(oneSecondFromNow,TestMessage)
    val schedule = Stream(timeAndMessage)
    scheduler ! UpdateSchedule(schedule)
    scheduler ! UpdateSchedule(Stream.empty)
    sender.expectNoMsg(2 seconds)
  }

  "scheduler should be able to schedule multiple messages" in {
    val sender = TestProbe()
    implicit val senderRef = sender.ref
    val props = Props(classOf[Scheduler], senderRef, executionContext)
    val scheduler = system.actorOf(props)
    val oneSecondFromNow = Instant.now().plusSeconds(1)
    val twoSecondsFromNow = Instant.now().plusSeconds(2)
    case object TestMessage1 extends Payload
    case object TestMessage2 extends Payload
    val timeAndMessage1 = TimeAndMessage(oneSecondFromNow,TestMessage1)
    val timeAndMessage2 = TimeAndMessage(twoSecondsFromNow,TestMessage2)
    val schedule = Stream(timeAndMessage1,timeAndMessage2)
    scheduler ! UpdateSchedule(schedule)
    sender.expectMsg(TestMessage1)
    sender.expectMsg(TestMessage2)
  }

}
