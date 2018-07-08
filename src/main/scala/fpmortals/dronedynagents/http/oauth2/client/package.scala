package fpmortals.dronedynagents.http.oauth2

import scala.language.higherKinds


package object client {

  import java.time.temporal.ChronoUnit

  import client.algebra._
  import client.api._
  import fpmortals.dronedynagents.http.client.algebra.JsonHttpClient
  import fpmortals.dronedynagents.http.encoding.UrlQueryWriter.ops._
  import scalaz._
  import Scalaz._
  import spray.json.ImplicitDerivedFormats._

  class OAuth2Client[F[_] : Monad](config: ServerConfig)(implicit
    user: UserInteraction[F],
    client: JsonHttpClient[F],
    clock: LocalClock[F]
  ) {
    def authenticate: F[CodeToken] =
      for {
        callback <- user.start
        params = AuthRequest(callback, config.scope, config.clientId)
        _ <- user.open(params.toUrlQuery.forUrl(config.auth))
        code <- user.stop
      } yield code

    def access(code: CodeToken): F[(RefreshToken, BearerToken)] =
      for {
        request <- AccessRequest(code.token, code.redirect_uri, config.clientId, config.clientSecret).pure[F]
        response <- client.postUrlencoded[AccessRequest, AccessResponse](config.access, request)
        time <- clock.now
        msg = response.body
        expires = time.plus(msg.expires_in, ChronoUnit.SECONDS)
        refresh = RefreshToken(msg.refresh_token)
        bearer = BearerToken(msg.access_token, expires)
      } yield (refresh, bearer)

    def bearer(refresh: RefreshToken): F[BearerToken] =
      for {
        request <- RefreshRequest(config.clientSecret, refresh.token, config.clientId).pure[F]
        response <- client.postUrlencoded[RefreshRequest, RefreshResponse](config.refresh, request)
        time <- clock.now
        msg = response.body
        expires = time.plus(msg.expires_in, ChronoUnit.SECONDS)
        bearer = BearerToken(msg.access_token, expires)
      } yield bearer
  }

}
