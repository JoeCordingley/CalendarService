package com.joecordingley.calendar

import java.time.Instant

import akka.actor.FSM.Failure

import scala.util.Success
import org.scalatest.concurrent.{Futures, PatienceConfiguration, ScalaFutures}

import scala.concurrent.duration._
import org.scalatest.{FreeSpec, Matchers}


/**
  * Created by joe on 19/09/17.
  */
class DelayedFutureTest extends FreeSpec with Matchers with ScalaFutures{

  "DelayedFuture should return" in {
    val twoSecondAway:Instant = Instant.now().plusSeconds(2)
    case object TestObject
    val d = DelayedCancellable(twoSecondAway,TestObject)
    val f = d.future
    assert(!f.isReadyWithin(1 second))
    assert(f isReadyWithin (3 seconds))
    val timeout = PatienceConfiguration.Timeout(3 seconds)
    whenReady(f,timeout){ r =>
      r should be (TestObject)
    }
  }

  "DelayedFuture should cancel" in {
    val twoSecondAway = Instant.now().plusSeconds(2)
    case object TestObject
    val d = DelayedCancellable(twoSecondAway,TestObject)
    val f = d.future
    d.cancel should be (Success(()))
    whenReady(f.failed){e=>
      e should be (CancellationException)
    }

  }

  "DelayedFuture should not cancel twice" in {
    val twoSecondAway = Instant.now().plusSeconds(2)
    case object TestObject
    val d = DelayedCancellable(twoSecondAway,TestObject)
    d.cancel
    val result = d.cancel
    assert(result.isFailure)
    result.failed.get shouldBe an [IllegalStateException]

  }

  "DelayedFuture should not cancel after finish" in {
    val oneSecondAway = Instant.now().plusSeconds(1)
    case object TestObject
    val d = DelayedCancellable(oneSecondAway,TestObject)
    val f = d.future
    val timeout = PatienceConfiguration.Timeout(2 seconds)
    whenReady(f,timeout){_ =>
      val result = d.cancel
      assert(result.isFailure)
      result.failed.get shouldBe an [IllegalStateException]
    }

  }

}
