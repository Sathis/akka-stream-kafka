package com.example

import akka.Done
import play.api.libs.json.Json
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object Demo extends App {

  val f: Future[Int] = Future {
    Thread.sleep(2.seconds.toMillis)
    42
  }


  val f1: Future[Int] = Future.failed(new Exception("error"))
/*  f.transform[String] { f =>
    f match {
      case Success(value) => Try(value.toString)
      case Failure(exception) => Try(exception.getMessage)
    }
  }*/


  val finalFuture = f1.recoverWith {
    case e: Exception => Future(Int.MaxValue)
  }.transform[String]((f: Try[Int]) =>
    f match {
      case Success(value) => Try(value.toString)
      case Failure(exception) => Try(exception.getMessage)
    })
    finalFuture onComplete {
      case Success(value) => println(value)
      case Failure(exception) => println(exception.getMessage)
    }
  implicit val addressFormat: JsonFormat[Address] = jsonFormat2(Address)
  implicit val customerFormat: JsonFormat[Customer] = jsonFormat3(Customer)
  implicit val addFormat = Json.format[Address]

  val customer: Customer = """
    | {"name": "sathish", "age": 35,
    | "address": {"street": "vellala street", "city": "chennai"}}
    |""".stripMargin
    .parseJson.convertTo[Customer]
  println(customer.name)

  val jsResult = Json.parse(
    """
      |{"name": "sathish", "age": 35,
      |"address": {"street": "vellala street", "city": "chennai"}}
      |""".stripMargin) \ "address"

val str = "hello"
  println(str.take(2))
  val address = jsResult.get.as[Address]

  println(address.city)
  Await.result(finalFuture, 5.seconds)

  case class Customer(name: String, age: Int, address: Address)

  case class Address(street: String, city: String)

  call(Handler.handle)


  def call(h: String => Future[Done]) = h("hello")
  object Handler {
    def handle(message: String): Future[Done] = ???
  }
}
