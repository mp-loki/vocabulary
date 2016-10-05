package models.words

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Word(id: Long, logos: String, partOfSpeech: String, language: String)

object Word {
    implicit val wordWrites = Json.writes[Word]
    implicit val wordFormat = Json.format[Word]
    implicit val wordReads = Json.reads[Word]
    
    
}