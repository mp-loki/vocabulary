package models.words

case class Translation(translatee: Word, translated: Word) {
    assert(translatee.language != translated.language, "Can not translate to the same language")
}
case class TranslationGuess(question: Word, options: List[Word], answer: Int)