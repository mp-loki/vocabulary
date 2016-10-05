package models

import play.api.libs.json._

case class Profile(id: Long, email: String, name: String, nativeLang: String)

object Profile {
  
  implicit val profileFormat = Json.format[Profile]
  
}

case class UserDetails(email: String, name: String, locale: String)
