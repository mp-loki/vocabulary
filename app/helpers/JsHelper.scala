package helpers

import javax.inject.Singleton
import play.api.libs.json.JsLookupResult
import play.api.libs.json.JsDefined
import play.api.libs.json.JsUndefined
import play.api.libs.json.JsValue
import models.UserDetails

@Singleton
class JsHelper {
  
  def getUserDetails(user: JsValue):UserDetails = {
    val emailJs = user \ "email" 
    val nameJs = user \ "name"
    val localeJs = user \ "locale"
    /*
    val givenNameJs = user \ "given_name"
    val familyNameJs = user \ "family_name"
    val pictureJs = user \ "picture"
    */
    val email = getJsVal(emailJs, () => throwException("email"))
    val name = getJsVal(nameJs, () => throwException("name"))
    val locale = getJsVal(localeJs, () => throwException("locale"))
    UserDetails(email, name, locale)
  }
  
  private def throwException(fieldName:String) = {
    throw new IllegalArgumentException(s"Field ${fieldName} is required")
  }
  
  private def getJsVal(jsVal: JsLookupResult, undefFunc:() => String):String = jsVal match {
    case JsDefined(v) => v.toString()
    case undef:JsUndefined => undefFunc()
  }
}