package fpmortals.dronedynagents.http.client

import fpmortals.dronedynagents.http.encoding.UrlEncodedWriter

import scala.language.higherKinds

package object algebra {

  import eu.timepit.refined.api.Refined
  import eu.timepit.refined.string.Url
  import spray.json.JsonReader

  type HttpHeader = (String, String)
  type HttpResponseHeader = List[(String, String)]

  final case class Response[T](header: HttpResponseHeader, body: T)

  trait JsonHttpClient[F[_]] {

    def get[B: JsonReader](
      uri: String Refined Url,
      headers: List[HttpHeader] = Nil
    ): F[Response[B]]

    def postUrlEncoded[A: UrlEncodedWriter, B: JsonReader](
      uri: String Refined Url,
      payload: A,
      headers: List[HttpHeader] = Nil
    ): F[Response[B]]

  }

}
