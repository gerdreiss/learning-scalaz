package fpmortals.dronedynagents.http.oauth2.client.api

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

// Avoid using java.net.URL at all costs: it uses DNS to resolve the hostname part when performing toString, equals or hashCode !!!

final case class AuthRequest(
  redirect_uri: String Refined Url,
  scope: String,
  client_id: String,
  prompt: String = "consent",
  response_type: String = "code",
  access_type: String = "offline"
)

final case class AccessRequest(
  code: String,
  redirect_uri: String Refined Url,
  client_id: String,
  client_secret: String,
  scope: String = "",
  grant_type: String = "authorization_code"
)

final case class AccessResponse(
  access_token: String,
  token_type: String,
  expires_in: Long,
  refresh_token: String
)

final case class RefreshRequest(
  client_secret: String,
  refresh_token: String,
  client_id: String,
  grant_type: String = "refresh_token"
)

final case class RefreshResponse(
  access_token: String,
  token_type: String,
  expires_in: Long
)
