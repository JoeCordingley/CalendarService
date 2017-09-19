package com.joecordingley.calendar

import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source


import akka.stream._
import akka.stream.scaladsl._


import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

/**
  * Created by joe on 15/09/17.
  */
object StreamsTry extends App{



  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)
//  val done: Future[Done] = source.runForeach(i => println(i))
  implicit val ec = system.dispatcher
//  done.onComplete(_ => system.terminate())

  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc* next)

/*  val result : Future[IOResult] =
    factorials
      .map(num => ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("factorials.txt")))*/
  def lineSink(filename: String) : Sink[String, Future[IOResult]] =
    Flow[String]
    .map(s => ByteString(s+"\n"))
    .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
  val result = factorials.map(_.toString).runWith(lineSink("factorial2.txt"))
  factorials.zipWith(Source(0 to 100))((num, idx)=>s"$idx! = $num")
    .throttle(1,1.second, 1, ThrottleMode.shaping)
    .runForeach(println)

  result.onComplete(_ => system.terminate())



}
