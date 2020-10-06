package com.example

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

object Test extends App {
  import scala.concurrent.duration._

  implicit val system = ActorSystem("Test")

  Source(List(1, 2, 3)).map(elem => {
    Thread.sleep(2.seconds.toMillis)
    elem + 1
  }).async
    .map(elem => {
      Thread.sleep(2.seconds.toMillis)
      println(s"value is $elem")
      elem * 2
    })
    .runWith(Sink.foreach[Int](println))

}
