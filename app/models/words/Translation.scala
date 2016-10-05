package models.words

case class Translation(translatee: Word, translated: Word) {
  assert(translatee.language != translated.language, "Can not translate to the same language")
}
case class TranslationGuess(question: Word, options: List[Word], answer: Int)

sealed trait Language {
  val code: String
}

object Language {
  val english = Lang("English", "English", "en")
  val german = Lang("German", "Deutsch","de")
  val french = Lang("French", "Français","fr")
  val ukrainian = Lang("Ukrainian", "Українська", "ua")
  
  val values = english :: french :: german :: ukrainian :: Nil
  
}

case class Lang(name: String, nativeName: String, code: String)