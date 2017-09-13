package com.joecordingley.calendar

import java.time.temporal.TemporalAmount
import java.time.Instant

import scala.concurrent.{ExecutionContext, Future, Promise}
import java.util.{Date, Timer, TimerTask}

import scala.util.Try

/**
  * Created by joe on 14/08/17.
  */
trait DelayedCancellable[+T]{
  self =>
  def future:Future[T]
  def cancel:Try[Unit]
  def map[A](f:T=>A)(implicit executionContext: ExecutionContext):DelayedCancellable[A] = new DelayedCancellable[A] {

    override def cancel: Try[Unit] = self.cancel

    override def future: Future[A] = self.future map f

  }
}

object CancellationException extends Exception

object DelayedCancellable {
  def apply[T](instant: Instant,t:T):DelayedCancellable[T] = {
    val date = new Date(instant.toEpochMilli)
    val timer = new Timer()
    val promise = Promise[T]
    val timerTask = new TimerTask {
      override def run(): Unit = promise.success(t)
    }
    timer.schedule(timerTask,date)
    new DelayedCancellable[T] {

      override def cancel =  Try{
        timer.cancel()
        promise.failure(CancellationException)
        ()
      }.recover{
        case _:IllegalStateException => throw new IllegalStateException("Future already completed")
      }

      override def future: Future[T] = promise.future
    }
  }

}

object Futures {
  def splitFuture[A,B,C](future: Future[A])(f:A=>(B,C))(implicit executionContext: ExecutionContext):(Future[B],Future[C]) = {
    val fbc = future map f
    (fbc map (_._1), fbc map (_._2))
  }

}
