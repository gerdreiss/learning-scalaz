package fpmortals.dronedynagents.http

import fpmortals.dronedynagents.http.oauth2.client.api.AuthRequest

package object encoding {

  import eu.timepit.refined.api.Refined
  import eu.timepit.refined.string.Url
  import simulacrum.typeclass

  final case class UrlQuery(params: List[(String, String)]) {
    def forUrl(url: String Refined Url): String Refined Url = ???
  }

  @typeclass trait UrlQueryWriter[A] {
    def toUrlQuery(a: A): UrlQuery
  }

  @typeclass trait UrlEncodedWriter[A] {
    def toUrlEncoded(a: A): String
  }

  object UrlEncodedWriter {
    import ops._

    implicit val string: UrlEncodedWriter[String] = java.net.URLEncoder.encode(_, "UTF-8")
    implicit val long: UrlEncodedWriter[Long] = _.toString
    implicit val stringySeq: UrlEncodedWriter[Seq[(String, String)]] = _.map {
      case (k, v) => s"${k.toUrlEncoded}=${v.toUrlEncoded}"
    } mkString "&"
    implicit val url: UrlEncodedWriter[String Refined Url] = s => java.net.URLEncoder.encode(s.value, "UTF-8")
  }

  import UrlEncodedWriter.ops._

  object AuthRequest {
    private def stringify[T: UrlEncodedWriter](t: T) = java.net.URLEncoder.encode(t.toUrlEncoded, "UTF-8")

    implicit val query: UrlQueryWriter[AuthRequest] = { a =>
      UrlQuery(List(
        "redirect_uri"  -> stringify(a.redirect_uri),
        "scope"         -> stringify(a.scope),
        "client_id"     -> stringify(a.client_id),
        "prompt"        -> stringify(a.prompt),
        "response_type" -> stringify(a.response_type),
        "access_type"   -> stringify(a.access_type)
      ))
    }

    object AccessRequest {

    }

    object RefreshRequest {

    }
  }

}
