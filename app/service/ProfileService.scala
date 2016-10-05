package service

import javax.inject.Singleton
import javax.inject.Inject
import helpers.JsHelper
import models.UserDetails
import dal.ProfileRepository
import models.Profile
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext
import play.api.Logger

@Singleton
class ProfileService @Inject() (jsHelper: JsHelper, profileRepo: ProfileRepository) {


}