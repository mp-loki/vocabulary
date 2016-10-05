package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.words._

import scala.concurrent.{ Future, ExecutionContext }
/** A repository for words */
@Singleton
class WordRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  private class WordsTable(tag: Tag) extends Table[Word](tag, "words") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def logos = column[String]("logos")
    def partOfSpeech = column[String]("partOfSpeech")
    def language = column[String]("language")
    def * = (id, logos, partOfSpeech, language) <> ((Word.apply _).tupled, Word.unapply)
  }

  private val words = TableQuery[WordsTable]

  def create(logos: String, partOfSpeech: String, language: String): Future[Word] = db.run {
    (words.map(w => (w.logos, w.partOfSpeech, w.language))
      returning words.map(_.id)
      into ((wordObj, id) => Word(id, wordObj._1, wordObj._2, wordObj._3))) += (logos, partOfSpeech, language)
  }
  def list(): Future[Seq[Word]] = db.run {
    words.result
  }
}