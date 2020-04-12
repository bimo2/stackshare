package io.bimo2.stackshare

import org.mongodb.scala.bson.ObjectId
import scala.concurrent.Await

import org.mongodb.scala._
import org.mongodb.scala.model._
import scala.concurrent.duration._

trait NoSQLModel[A] {

  def toNoSQL(model: A): Document
  def toModel(document: Document): A
}

object NoSQLService {

  private val client = MongoClient()
  private val database = client.getDatabase("stackshare")
  private val timeout = 10.seconds

  def getMetrics(): (Long, Long, Long) = {
    val usersMetric = Await.result(users().countDocuments().toFuture(), timeout)
    val positionsMetric = Await.result(positions().countDocuments().toFuture(), timeout)
    val companiesMetric = Await.result(companies().countDocuments().toFuture(), timeout)

    (usersMetric, positionsMetric, companiesMetric)
  }

  def writeUser(user: User): Unit = {
    val _id = new ObjectId(user.id.getOrElse(new ObjectId().toString()))
    val document = User.toNoSQL(user)
    val filter = Document("_id" -> _id)
    val options = UpdateOptions().upsert(true)

    Await.result(users().replaceOne(filter, document, options).toFuture(), timeout)
  }

  def findUsers(): Vector[User] = {
    val documents = Await.result(users().find().toFuture(), timeout)

    Vector[User]() ++ documents.map { user =>
      User.toModel(user)
    }
  }

  def destroy(): Unit = {
    Await.result(users().drop().toFuture(), timeout)
    Await.result(positions().drop().toFuture(), timeout)
    Await.result(companies().drop().toFuture(), timeout)

    Await.result(database.createCollection("users").toFuture(), timeout)
    Await.result(database.createCollection("positions").toFuture(), timeout)
    Await.result(database.createCollection("companies").toFuture(), timeout)

    val usersIndex = Document("username" -> 1)
    // val positionsIndex = Document()
    // val companiesIndex = Document()
    val options = IndexOptions().unique(true)

    Await.result(users().createIndex(usersIndex, options).toFuture(), timeout)
  }

  private def users(): MongoCollection[Document] = {
    database.getCollection("users")
  }

  private def positions(): MongoCollection[Document] = {
    database.getCollection("positions")
  }

  private def companies(): MongoCollection[Document] = {
    database.getCollection("companies")
  }
}
