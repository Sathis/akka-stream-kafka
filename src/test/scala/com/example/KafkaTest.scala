package com.example

import akka.actor.ActorSystem
import akka.kafka.testkit.ConsumerResultFactory
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.must.Matchers

class KafkaTest extends TestKit(ActorSystem("example"))
  with AnyFlatSpecLike
  with Matchers
  with BeforeAndAfterAll {
    val startOffSet = 0

  "test" should "success" in {
    val elements = (0 to 10).map { elem =>
      val nextOffset = startOffSet + elem
      ConsumerResultFactory.committableMessage(
        new ConsumerRecord(
          "topic", 0, nextOffset,
          "key", s"value is $elem"),
        ConsumerResultFactory.committableOffset(
          "groupId", "topic", 0, nextOffset, s"metadata $elem")
      )
    }

    Source(elements)
      .to(Sink.foreach(println)).run()
  }
}
