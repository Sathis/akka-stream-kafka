package com.example

import akka.util.ByteString
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import spray.json._

case class AccountTransfer(id: Long, from: String, to: String, amount: Double)

class JsonSerializer[T: JsonFormat] extends Serializer[T] {

  override def serialize(topic: String, data: T): Array[Byte] =
    data.toJson.compactPrint.getBytes
}

class JsonDeserializer[T: JsonFormat] extends Deserializer[T] {

  override def deserialize(topic: String, data: Array[Byte]): T =
    ByteString(data).utf8String.parseJson.convertTo[T]
}
