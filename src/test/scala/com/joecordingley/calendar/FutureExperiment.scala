package com.joecordingley.calendar

import java.time.Instant
import java.time.Duration

/**
  * Created by joe on 14/08/17.
  */
object FutureExperiment extends App{
  import scala.concurrent.ExecutionContext.Implicits.global

//  val futures = (1 to 100)
//    .toStream
//    .map(_.seconds)
//    .map(DelayedFuture(_))
//  futures.foreach(_.foreach(_ => println("hello")))
//  Thread.sleep(Long.MaxValue)
  val now = Instant.now()
  def task = println("there")
  val  i = 1
  i
  val tasks = (1 to 100000)
    .map(i => ScheduledTask(now.plus(Duration.ofMillis(i)),() =>task))
    .toStream
  Scheduler.addSchedule(tasks)

  Thread.sleep(Long.MaxValue)



}
