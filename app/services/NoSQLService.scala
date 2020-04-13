package io.bimo2.stackshare

import org.mongodb.scala.bson.ObjectId
import scala.concurrent.Await

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
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
    val document = User.toNoSQL(user)
    val filter = Document("username" -> user.username)
    val options = ReplaceOptions().upsert(true)

    Await.result(users().replaceOne(filter, document, options).toFuture(), timeout)
  }

  def findUsers(): Vector[User] = {
    val sort = ascending("username")
    val documents = Await.result(users().find().sort(sort).toFuture(), timeout)

    Vector[User]() ++ documents.map { document =>
      User.toModel(document)
    }
  }

  def findUser(id: String): User = {
    val query = equal("username", id)
    val documents = Await.result(users().find(query).toFuture(), timeout)

    User.toModel(documents.head)
  }

  def dropUser(id: String): Unit = {
    val query = equal("username", id)

    Await.result(users().deleteOne(query).toFuture(), timeout)
  }

  def writePosition(position: Position): Unit = {
    val document = Position.toNoSQL(position)
    val filter = Document("url" -> position.url)
    val options = ReplaceOptions().upsert(true)

    Await.result(positions().replaceOne(filter, document, options).toFuture(), timeout)
  }

  def findPositions(): Vector[Position] = {
    val sort = ascending("domain")
    val documents = Await.result(positions().find().sort(sort).toFuture(), timeout)

    Vector[Position]() ++ documents.map { document =>
      Position.toModel(document)
    }
  }

  def findPosition(id: String): Position = {
    val query = equal("_id", new ObjectId(id))
    val documents = Await.result(positions().find(query).toFuture(), timeout)

    Position.toModel(documents.head)
  }

  def dropPosition(id: String): Unit = {
    val query = equal("url", id)

    Await.result(positions().deleteOne(query).toFuture(), timeout)
  }

  def writeCompany(company: Company): Unit = {
    val document = Company.toNoSQL(company)
    val filter = Document("domain" -> company.domain)
    val options = ReplaceOptions().upsert(true)

    Await.result(companies().replaceOne(filter, document, options).toFuture(), timeout)
  }

  def findCompanies(): Vector[Company] = {
    val sort = ascending("domain")
    val documents = Await.result(companies().find().sort(sort).toFuture(), timeout)

    Vector[Company]() ++ documents.map { document =>
      Company.toModel(document)
    }
  }

  def findCompany(id: String): Company = {
    val query = equal("domain", id)
    val documents = Await.result(companies().find(query).toFuture(), timeout)

    Company.toModel(documents.head)
  }

  def dropCompany(id: String): Unit = {
    val query = equal("domain", id)

    Await.result(companies().deleteOne(query).toFuture(), timeout)
  }

  def destroy(): Unit = {
    Await.result(users().drop().toFuture(), timeout)
    Await.result(positions().drop().toFuture(), timeout)
    Await.result(companies().drop().toFuture(), timeout)

    Await.result(database.createCollection("users").toFuture(), timeout)
    Await.result(database.createCollection("positions").toFuture(), timeout)
    Await.result(database.createCollection("companies").toFuture(), timeout)

    val usersIndex = Document("username" -> 1)
    val positionsIndex = Document("url" -> 1)
    val companiesIndex = Document("domain" -> 1)
    val options = IndexOptions().unique(true)

    Await.result(users().createIndex(usersIndex, options).toFuture(), timeout)
    Await.result(positions().createIndex(positionsIndex, options).toFuture(), timeout)
    Await.result(companies().createIndex(companiesIndex, options).toFuture(), timeout)
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
