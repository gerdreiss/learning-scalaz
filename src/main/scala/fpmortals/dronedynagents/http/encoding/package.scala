package fpmortals.dronedynagents.http


package object encoding {

  import eu.timepit.refined.api.Refined
  import eu.timepit.refined.string.Url
  import fpmortals.dronedynagents.http.oauth2.client.api._
  import simulacrum.typeclass

  final case class UrlQuery(params: List[(String, String)]) {
    def forUrl(url: String Refined Url): String Refined Url = url
  }

  @typeclass trait UrlEncodedWriter[A] {
    def toUrlEncoded(a: A): String
  }

  @typeclass trait UrlQueryWriter[A] {
    def toUrlQuery(a: A): UrlQuery
  }

  object UrlEncodedWriter {
    import ops._

    implicit val string: UrlEncodedWriter[String] = java.net.URLEncoder.encode(_, "UTF-8")
    implicit val long: UrlEncodedWriter[Long] = _.toString
    implicit val stringySeq: UrlEncodedWriter[Seq[(String, String)]] = _.map {
      case (k, v) => s"${k.toUrlEncoded}=${v.toUrlEncoded}"
    } mkString "&"

    implicit val url: UrlEncodedWriter[String Refined Url] = { s =>
      java.net.URLEncoder.encode(s.value, "UTF-8")
    }
  }

  import UrlEncodedWriter.ops._

  private def stringify[T: UrlEncodedWriter](t: T) =
    java.net.URLEncoder.encode(t.toUrlEncoded, "UTF-8")

  object UrlQueryWriter {
    implicit val authRequest: UrlQueryWriter[AuthRequest] = { a =>
      UrlQuery(List(
        "redirect_uri"  -> stringify(a.redirect_uri),
        "scope"         -> stringify(a.scope),
        "client_id"     -> stringify(a.client_id),
        "prompt"        -> stringify(a.prompt),
        "response_type" -> stringify(a.response_type),
        "access_type"   -> stringify(a.access_type)
      ))
    }
  }
  object AccessRequest {
    implicit val encoded: UrlEncodedWriter[AccessRequest] = { a =>
      Seq(
        "code"          -> a.code.toUrlEncoded,
        "redirect_uri"  -> a.redirect_uri.toUrlEncoded,
        "client_id"     -> a.client_id.toUrlEncoded,
        "client_secret" -> a.client_secret.toUrlEncoded,
        "scope"         -> a.scope.toUrlEncoded,
        "grant_type"    -> a.grant_type.toUrlEncoded
      ).toUrlEncoded
    }
  }
  object RefreshRequest {
    implicit val encoded: UrlEncodedWriter[RefreshRequest] = { r =>
      Seq(
        "client_secret" -> r.client_secret.toUrlEncoded,
        "refresh_token" -> r.refresh_token.toUrlEncoded,
        "client_id"     -> r.client_id.toUrlEncoded,
        "grant_type"    -> r.grant_type.toUrlEncoded
      ).toUrlEncoded
    }
  }
}
