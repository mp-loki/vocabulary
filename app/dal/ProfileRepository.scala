package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Profile

import scala.concurrent.{ Future, ExecutionContext }
import scala.util.Success
import models.UserDetails
import scala.util.Failure

import play.api.Logger

/** A repository for profiles */
@Singleton
class ProfileRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  private class ProfilesTable(tag: Tag) extends Table[Profile](tag, "profiles") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def name = column[String]("name")
    def nativeLang = column[String]("nativeLang")
    def * = (id, email, name, nativeLang) <> ((Profile.apply _).tupled, Profile.unapply)
    
    def findByEmail(email: String)(implicit session: Session) = {
    		
    		val action = profiles.filter { p => p.email === email }.take(1).result
    		db.run(action)
    				
    }
  }

  private val profiles = TableQuery[ProfilesTable]
  
  def findByEmail(email: String): Future[Option[Profile]] = db.run {
    profiles.filter ( p => p.email === email ).result.headOption
  }
  
  def createProfileIfNotExists(userDetails: UserDetails) = {
    val profile: Future[Option[Profile]] = findByEmail(userDetails.email)
    profile.onComplete {
      case Success(value) => value match {
        case Some(profile) => Logger.info(s"Profile ${profile.email} already exists")
        case None => create(userDetails)
      }
      case Failure(t) => Logger.error("An error has occured: " + t.getMessage)
    }
  }

  def create(userDetails: UserDetails): Future[Profile] = db.run {
    (profiles.map(p => (p.email, p.name, p.nativeLang))
      returning profiles.map(_.id)
      into ((profileObj, id) => Profile(id, profileObj._1, profileObj._2, profileObj._2))) += (userDetails.email, userDetails.name, userDetails.locale)
  }
  def list(): Future[Seq[Profile]] = db.run {
    profiles.result
  }
}