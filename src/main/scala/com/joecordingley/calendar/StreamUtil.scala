package com.joecordingley.calendar

import Ordered.orderingToOrdered
import scala.concurrent.{ExecutionContext, Future}
/**
  * Created by joe on 12/08/17.
  */
object StreamUtil {

  def merge[A](s1: Stream[A],s2: Stream[A])(implicit ordering: Ordering[A]): Stream[A] = (s1,s2) match {
    case (Stream.Empty,_) => s2
    case (_,Stream.Empty) => s1
    case (a#::as,b#::bs) => {
      if (a < b)
        a #:: merge(as,s2)
      else
        b #:: merge(s1,bs)
    }
  }
  def map2sequentially[A,B,C](f1: ()=>Future[A], f2: ()=>Future[B])(f: (A,B) => C)(implicit ec:ExecutionContext): Future[C] = {
    for {
      a <- f1()
      _ = println("here")
      b <- f2()
    } yield f(a,b)
  }
  def sequentially[A](s: Stream[()=>Future[A]])(implicit ec:ExecutionContext):Stream[Future[A]]
    = s.head() #:: s.zip(s.tail).map{case(a,b) => map2sequentially(a,b){case (a,b) => b}}
  def seq[A](s:Stream[Future[Unit]])(implicit executionContext: ExecutionContext):Future[Unit] = {
    for {
      a <- s.head
      bs <- seq(s.tail)
    } yield ()

  }
  def sequence[A](s:Stream[Future[A]])(implicit executionContext: ExecutionContext):Future[Stream[A]] =
    s.foldRight[Future[Stream[A]]](Future.successful(Stream.empty))(map2(_,_)(_#::_))
  def sequence2[A](s:Stream[Future[A]])(implicit executionContext: ExecutionContext):Future[Stream[A]] = for {
    a <- s.head
    bs <- sequence2(s.tail)
  } yield a #:: bs
  def map2[A,B,C](f1:Future[A],f2:Future[B])(op:(A,B) => C)(implicit executionContext: ExecutionContext):Future[C] =
    for {
      a <- f1
      b <- f2
    } yield op(a,b)
}

