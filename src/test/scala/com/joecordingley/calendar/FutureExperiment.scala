package com.joecordingley.calendar

import java.time.Instant
import java.time.Duration

import scala.concurrent.Future

/**
  * Created by joe on 14/08/17.
  */
object FutureExperiment extends App{
  import scala.concurrent.ExecutionContext.Implicits.global

  def streamFutures(s:Stream[Instant]):Stream[Future[Unit]] = {
    def go(previousFuture:Future[Unit],s:Stream[Instant]):Stream[Future[Unit]]= s match {
      case Stream.Empty => Stream.empty
      case i #:: is => {
        val nextFuture =  for {
          _ <- previousFuture
          _ <- DelayedFuture(i)
        } yield ()
        nextFuture #:: go(nextFuture,is)
      }
    }
    go(Future.successful(()),s)
  }
  def taskFutures(s:Stream[Future[Unit]]):Future[Unit] = s match {
    case Stream.Empty => Future.successful(())
    case f #:: fs => {
      f.foreach(_=>println(Instant.now()))
      f.flatMap(_=>taskFutures(fs))
    }
  }
  
  val now = Instant.now()

  val times = (1 to 1000000)
    .map(now plusNanos  _)
    .toStream
  val s = streamFutures(times)
  taskFutures(s)
  Thread.sleep(1000)




}
