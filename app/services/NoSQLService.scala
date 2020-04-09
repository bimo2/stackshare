package io.bimo2.stackshare

import scala.concurrent.Await

import org.mongodb.scala._
import scala.concurrent.duration._

object NoSQLService {

  private val client = MongoClient()
  private val database = client.getDatabase("stackshare")
  private val timeout = 10.seconds

  def destroy(): Unit = {
    Await.result(users().drop().toFuture(), timeout)
    Await.result(positions().drop().toFuture(), timeout)
    Await.result(companies().drop().toFuture(), timeout)

    database.createCollection("users")
    database.createCollection("positions")
    database.createCollection("companies")
  }

  private def users(): MongoCollection[Document] = database.getCollection("users")

  private def positions(): MongoCollection[Document] = database.getCollection("positions")

  private def companies(): MongoCollection[Document] = database.getCollection("companies")
}
