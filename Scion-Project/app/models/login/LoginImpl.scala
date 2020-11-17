package login

import play.mvc.Http.RequestHeader

object LoginImpl {
    def extractUser(req: RequestHeader): Option[User] = {
      val sessionTokenOpt = req.session().get("sessionToken")
      None
    }
}
