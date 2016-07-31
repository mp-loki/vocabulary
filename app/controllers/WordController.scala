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
import play.api.cache.Cache
import play.api.Play.current
import play.mvc.Results._
import helpers.Auth0Config

class WordController extends Controller {

    val faire = Word("faire", "verb", "fr")
    val chercher = Word("chercher", "verb", "fr")
    val etre = Word("être", "verb", "fr")
    val avoir = Word("avoir", "verb", "fr")

    val toDo = Word("do", "verb", "en")
    val search = Word("search", "verb", "en")
    val be = Word("be", "verb", "en")
    val have = Word("have", "verb", "en")

    val frenchWords = Json.toJson(List(avoir, chercher, etre, faire))
    val enWords = List(be, toDo, have, search)

    implicit val wordWrites = Json.writes[Word]
    implicit val translationGuessWrites = Json.writes[TranslationGuess]

    val translations = Vector(Translation(faire, toDo), Translation(etre, be), Translation(chercher, search), Translation(avoir, have))

    def getWords = frenchWords :: enWords

    def getGuessOptions(translation: Translation, words: List[Word]): TranslationGuess = {
        val incorrectAnswers = words.filter(x => x.logos != translation.translated.logos).take(3)
        val options = scala.util.Random.shuffle(translation.translated :: incorrectAnswers)
        TranslationGuess(translation.translatee, options, options.indexOf(translation.translated))
    }

    val quizz = for {
        translation <- translations
    } yield getGuessOptions(translation, enWords)

    def words = AuthenticatedAction { request =>
        //val idToken = request.session.get("idToken").get
        //val profile = Cache.getAs[JsValue](idToken + "profile").get
        Ok(views.html.words(Json.toJson(quizz)))
    }

    def login = Action {
        Ok(views.html.index(Auth0Config.get()))
    }

    def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
        Action { request =>
            (request.session.get("idToken").flatMap { idToken =>
                Cache.getAs[JsValue](idToken + "profile")
            } map { profile =>
                f(request)
            }).orElse {
                Some(Redirect(routes.WordController.login()))
            }.get
        }
    }

    def getQuizz = Action {
        Ok(Json.stringify(Json.toJson(quizz)))
    }

    def getQuizzTemplate = Action {
        Ok(views.html.template())
    }
}