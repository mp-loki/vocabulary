package controllers

import play.api.mvc._
import play.api.libs.json._
import models.words._

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
    implicit val wordWrites = Json.writes[Word]
    implicit val translationGuessWrites = Json.writes[TranslationGuess]
    val enWords = List(be, toDo, have, search)

    val translations = Vector(Translation(faire, toDo), Translation(etre, be), Translation(search, chercher), Translation(avoir, have))

    def getWords = frenchWords :: enWords

    def getGuessOptions(translation: Translation, words: List[Word]): TranslationGuess = {
        val options = words.filter(x => x.logos != translation.translated.logos).take(3)
        TranslationGuess(translation.translatee, scala.util.Random.shuffle(translation.translated :: options))
    }
    

    val quizz = for {
        translation <- translations
    } yield getGuessOptions(translation, enWords)

    def index = Action {
        Ok(views.html.words(Json.toJson(quizz)))
    }
    
    def getQuizz = Action {
        Ok(Json.stringify(Json.toJson(quizz)))
    }
}