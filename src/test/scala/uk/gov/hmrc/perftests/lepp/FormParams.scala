/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.lepp

import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.Predef.*

object FormParams {

  val loginFormParams: Map[String, String] = Map(
    "authorityId"                         -> "",
    "gatewayToken"                        -> "",
    "excludeGnapToken"                    -> "false",
    "credentialStrength"                  -> "strong",
    "confidenceLevel"                     -> "250",
    "affinityGroup"                       -> "Individual",
    "usersName"                           -> "",
    "email"                               -> "user@test.com",
    "credentialRole"                      -> "User",
    "oauthTokens.accessToken"             -> "",
    "oauthTokens.refreshToken"            -> "",
    "oauthTokens.idToken"                 -> "",
    "additionalInfo.profile"              -> "",
    "additionalInfo.groupProfile"         -> "",
    "additionalInfo.emailVerified"        -> "N/A",
    "groupIdentifier"                     -> "",
    "agent.agentId"                       -> "",
    "agent.agentCode"                     -> "",
    "agent.agentFriendlyName"             -> "",
    "unreadMessageCount"                  -> "",
    "mdtp.sessionId"                      -> "",
    "mdtp.deviceId"                       -> "",
    "presets-dropdown"                    -> "IR-SA",
    "enrolment[0].name"                   -> "HMRC-PT",
    "enrolment[0].taxIdentifier[0].name"  -> "",
    "enrolment[0].taxIdentifier[0].value" -> "",
    "enrolment[0].state"                  -> "Activated",
    "itmp.givenName"                      -> "Test",
    "itmp.middleName"                     -> "",
    "itmp.familyName"                     -> "Test",
    "itmp.dateOfBirth"                    -> "",
    "itmp.address.line1"                  -> "",
    "itmp.address.line2"                  -> "",
    "itmp.address.line3"                  -> "",
    "itmp.address.line4"                  -> "",
    "itmp.address.line5"                  -> "",
    "itmp.address.postCode"               -> "",
    "itmp.address.countryName"            -> "",
    "itmp.address.countryCode"            -> ""
  )

  val bankDetailsFormParams: Map[String, String] = Map(
    "bankDetails.accountName"   -> "Melvin Loper",
    "bankDetails.sortCode"      -> "207106",
    "bankDetails.accountNumber" -> "44311677",
    "bankDetails.rollNumber"    -> ""
  )

  def addFormParams(builder: HttpRequestBuilder, params: Map[String, String]): HttpRequestBuilder =
    params.foldLeft(builder) { case (b, (key, value)) =>
      b.formParam(key, _ => value)
    }
}