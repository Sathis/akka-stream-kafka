package com.example

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.{CommitterSettings, ConsumerSettings}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.IntegerDeserializer
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

import scala.concurrent.{ExecutionContext, Future}

object AccountTransferStreaming {

  implicit val system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "KafkaToSink")

  implicit val executionContext: ExecutionContext = system.executionContext

  val topic = "word"
  private val groupId = "test5"
  private val kafkaBootstrapServers = "localhost:9092"

  implicit val format: JsonFormat[AccountTransfer] = jsonFormat4(AccountTransfer)
  val kafkaConsumerSettings: ConsumerSettings[Integer, AccountTransfer] = ConsumerSettings(system,
    new IntegerDeserializer, new JsonDeserializer[AccountTransfer])
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  def callService(transfer: AccountTransfer): Future[Done] =  Future {
      Done
  }

  val committerSettings: CommitterSettings = CommitterSettings(system)

  /*Consumer.committableSource(
    kafkaConsumerSettings, Subscriptions.topics(topic))
    .filter(msg => msg.record.value().amount > 10000)
    .mapAsync(10) { msg =>
      callService(msg.record.value()).map( _  => msg.committableOffset)
    }
    .via(Committer.flow(committerSettings))
      .toMat(Committer.sink(committerSettings))(DrainingControl.apply)
*/
}
