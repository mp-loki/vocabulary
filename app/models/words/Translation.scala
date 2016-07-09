package models.words

case class Translation(translatee: Word, translated: Word) {
    assert(translatee.language != translated.language, "Can nottranslate to the same language")
}
case class TranslationGuess(target: Word, options: List[Word])