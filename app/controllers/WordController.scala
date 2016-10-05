package controllers

import models.words._
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.http.{ MimeTypes, HeaderNames }
import play.api.libs.ws.WS
import play.api.mvc.{ Results, Action, Controller }
import play.api.libs.json._
import play.mvc.Results._
import javax.inject.Inject
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

class WordController @Inject() (cache: CacheApi, repo: WordRepository, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val faire = Word(1, "faire", "verb", "fr")
  val chercher = Word(2, "chercher", "verb", "fr")
  val etre = Word(3, "être", "verb", "fr")
  val avoir = Word(4, "avoir", "verb", "fr")

  val toDo = Word(5, "do", "verb", "en")
  val search = Word(6, "search", "verb", "en")
  val be = Word(7, "be", "verb", "en")
  val have = Word(8, "have", "verb", "en")

  val frenchWords = Json.toJson(List(avoir, chercher, etre, faire))
  val enWords = List(be, toDo, have, search)

  implicit val wordWrites = Json.writes[Word]
  implicit val translationGuessWrites = Json.writes[TranslationGuess]
  
  val translations = Vector(Translation(faire, toDo), Translation(etre, be), Translation(chercher, search), Translation(avoir, have))

  //def getWords = frenchWords :: enWords
  
  /**
   * A REST endpoint that gets all the people as JSON.
   */
  def getWords = Action.async {
  	repo.list().map { words =>
      Ok(Json.toJson(words))
    }
  }
  
  def getGuessOptions(translation: Translation, words: List[Word]): TranslationGuess = {
    val incorrectAnswers = words.filter(x => x.logos != translation.translated.logos).take(3)
    val options = scala.util.Random.shuffle(translation.translated :: incorrectAnswers)
    TranslationGuess(translation.translatee, options, options.indexOf(translation.translated))
  }

  val quizz = for {
    translation <- translations
  } yield getGuessOptions(translation, enWords)

  def words = AuthenticatedAction { request =>
    Ok(views.html.words(Json.toJson(quizz)))
  }

  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        cache.get[JsValue](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Redirect(routes.UserController.login()))
      }.get
    }
  }

  val createWordForm: Form[CreateWordForm] = Form {
    mapping(
      "logos" -> nonEmptyText,
      "partOfSpeech" -> nonEmptyText,
      "language" -> nonEmptyText)(CreateWordForm.apply)(CreateWordForm.unapply)
  }

  def addWord = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    createWordForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.addWords(errorForm)))
      },
      // There were no errors in the from, so create the person.
      word => {
        repo.create(word.logos, word.partOfSpeech, word.language).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.WordController.addWords)
        }
      })
  }
  /**
   * A REST endpoint that gets all the people as JSON.
   */

  def getQuizz = Action {
    Ok(Json.stringify(Json.toJson(quizz)))
  }

  def getQuizzTemplate = Action {
    Ok(views.html.template())
  }
  def addWords = Action {
    Ok(views.html.addWords(createWordForm))
  }
}

case class CreateWordForm(logos: String, partOfSpeech: String, language: String)