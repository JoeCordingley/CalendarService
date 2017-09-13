/*package com.joecordingley.calendar

import java.time.Instant
import java.time.Duration


import scala.concurrent.{Future, Promise}

/**
  * Created by joe on 14/08/17.
  */
object FutureExperiment extends App{
  import scala.concurrent.ExecutionContext.Implicits.global

/*  def streamFutures(s:Stream[Instant]):Stream[Future[Unit]] = {
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
  }*/

/*  def taskFutures(s:Stream[Future[Unit]]):Future[Unit] = s match {
    case Stream.Empty => Future.successful(())
    case f #:: fs => {
      f.foreach(_=>println(Instant.now()))
      f.flatMap(_=>taskFutures(fs))
    }
  }*/
  def taskFutures(s:Stream[Future[Task]]):Future[Unit] = s match {
    case Stream.Empty => Future.successful(())
    case f #:: fs => {
      f.foreach(_())
      f.flatMap(_=>taskFutures(fs))
    }
  }



  def delayedTask(instant: Instant,task: Task):Future[Task] = DelayedFuture(instant).map(_=> task)
  def streamFutures(s:Stream[ScheduledTask]):Stream[Future[Task]] = {
    def go(previousFuture:Future[Unit],s:Stream[ScheduledTask]):Stream[Future[Task]] = s match {
      case Stream.Empty => Stream.empty
      case ScheduledTask(instant,task) #:: is => {
        val nextFuture = for {
          _ <- previousFuture
          task <- delayedTask(instant,task)

        } yield task
        nextFuture #:: go(nextFuture.map(_ => ()),is)
      }
    }
    go(Future.unit,s)
  }
//  def delayedCancellableTask(instant: Instant, task: Task):CancellableFuture[Task] =
//    DelayedFuture.cancellable(instant).map(_=>task)


//  def streamFutures(s:Stream[ScheduledTask]):Stream[CancellableFuture[Task]] = {
//    def go(previousFuture:Future[Unit],s:Stream[ScheduledTask]):Stream[CancellableFuture[Task]] = s match {
//      case Stream.Empty => Stream.empty
//      case ScheduledTask(instant,task) #:: is => {
//        val nextFuture = for {
//          _ <- previousFuture
//          task <- delayedCancellableTask(instant,task).future
//        } yield task
//
//        nextFuture #:: go(nextFuture.map(_ => ()),is)
//      }
//    }
//    go(Future.unit,s)
//  }
  
  val now = Instant.now()

/*  val times = (1 to 1000000)
    .map(now plusNanos  _)
    .map(ScheduledTask(_,()=>println(Instant.now)))
    .toStream*/
  val times = Stream.iterate(now)(_ plusMillis 10)
    .map(ScheduledTask(_,()=>println(Instant.now)))
  val s = streamFutures(times)
  val f = taskFutures(s)
  f.flatMap(_=>Future.failed(new Exception))
  Thread.sleep(1000000)






}*/
