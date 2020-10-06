package com.example

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringSerializer}
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

import scala.concurrent.ExecutionContext


object KafkaStreamingDemo extends App {

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

  val accountTransfer = Consumer
    .committableSource(kafkaConsumerSettings, Subscriptions.topics(topic))
    .map(elem => elem.record)


  val producerSettings =  ProducerSettings(system,
    Some(new StringSerializer), Some(new JsonSerializer[AccountTransfer]))
    .withBootstrapServers(kafkaBootstrapServers)

  val transferSuspicionSink = Producer.plainSink(producerSettings)

  val filterFlow: Flow[ConsumerRecord[Integer, AccountTransfer],
    ProducerRecord[String, AccountTransfer], NotUsed] =
    Flow[ConsumerRecord[Integer, AccountTransfer]]
      .filter(consumerRecord => consumerRecord.value().amount > 10000)
      .map(record => new ProducerRecord("word-count", record.value().id.toString, record.value()))

  /* val done = Consumer
     .plainSource(kafkaConsumerSettings, Subscriptions.topics(topic))
     .filter(consumerRecord => consumerRecord.value().amount > 10000)
     .map(record => new ProducerRecord("word-count",
       record.value().id.toString, record.value()))
     .runWith(Producer.plainSink(producerSettings))
 */

  val done = RunnableGraph.fromGraph(GraphDSL.create(accountTransfer) { implicit builder =>accountTransferShape =>
    import GraphDSL.Implicits._

    val broadcast = builder.add(
      Broadcast[ConsumerRecord[Integer, AccountTransfer]](2))

    val filterFlowShape = builder.add(filterFlow)
    val transferSuspicionShape = builder.add(transferSuspicionSink)
    val ignoreSinkShape = builder.add(
      Sink.foreach[ConsumerRecord[_, AccountTransfer]](record => println(record.toString)))

    accountTransferShape ~> broadcast ~> filterFlowShape ~> transferSuspicionShape
    broadcast ~> ignoreSinkShape
    ClosedShape
  }).run()

  /*done onComplete  {
    case Success(_) => println("Done"); system.terminate()
    case Failure(err) => println(err.toString); system.terminate()
  }*/
}
