package fpmortals.dronedynagents.http.oauth2.client

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

import scala.language.higherKinds

package object algebra {

  final case class CodeToken(token: String, redirect_uri: String Refined Url)

  trait UserInteraction[F[_]] {
    /** returns the URL of the local server */
    def start: F[String Refined Url]

    /** prompts the user to open this URL */
    def open(uri: String Refined Url): F[Unit]

    /** recover the code from the callback */
    def stop: F[CodeToken]
  }

  trait LocalClock[F[_]] {
    def now: F[java.time.LocalDateTime]
  }

}
