/*
 * Copyright 2023 HM Revenue & Customs
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

import io.gatling.core.Predef.*
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.{HttpConfiguration, ServicesConfiguration}
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.core.session.Expression


object LEPPRequests extends HttpConfiguration with ServicesConfiguration {

  val baseurl: String = baseUrlFor("base-url")

  val route: String = "low-earners-pensions-payment"

  val authLoginstubRoot: String = baseUrlFor("auth-login-stub")

  val loginUrl: String = authLoginstubRoot + "/auth-login-stub/gg-sign-in?continue=/low-earners-pensions-payment"

  val startPageUrl: String = baseurl + "//low-earners-pensions-payment/start"

  val dashboardPageUrl: String = baseurl + "/low-earners-pensions-payment/dashboard"

  val breakdownPageUrl: String = baseurl + "//low-earners-pensions-payment/breakdown"

  val bankDetailsPageUrl: String = baseurl + "//low-earners-pensions-payment/bank-details"

  val cyaPageUrl: String = baseurl + "//low-earners-pensions-payment/check-your-answers"

  val confirmationPageUrl: String = baseurl + "//low-earners-pensions-payment/confirmation"

  val csrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String] = regex(_ => csrfPattern).saveAs("csrfToken")

  val csrfToken: Expression[String] = "#{csrfToken}"

  def getLogin: HttpRequestBuilder = {
    http("get Login Details")
      .get(loginUrl)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postLogin: HttpRequestBuilder = {
    http("Post Login Details Standard Payment")
      .post(loginUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("redirectionUrl",_ => startPageUrl)
      .formParam("credentialStrength",_ => "strong")
      .formParam("confidenceLevel",_ => "250")
      .formParam("nino",_ => "AA000003D")
      .formParam("affinityGroup",_ => "Individual")
      .formParam("authorityId", _ => "someId")
      .formParam("enrolment[0].name",_ => "HMRC-PI")
      .formParam("enrolment[0].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[0].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[0].state",_ => "Activated")
      .check(status.is(303))
  }

  def getStartPage: HttpRequestBuilder = {
    http("Get Start Page Standard Payment")
      .get(startPageUrl: String)
      .check(status.is(200))
  }

  def getDashboardPage: HttpRequestBuilder = {
    http("Get Dashboard Page Standard Payment")
      .get(dashboardPageUrl: String)
      .check(status.is(200))
  }

  def getBreakdownPage: HttpRequestBuilder = {
    http("Get Breakdown Page Standard Payment")
      .get(breakdownPageUrl: String)
      .check(status.is(200))
  }

  def getBankDetailsPage: HttpRequestBuilder = {
    http("Get Bank Details Page Standard Payment")
      .get(bankDetailsPageUrl: String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postBankDetailsPage: HttpRequestBuilder = {
    http("Post to Bank Details Page Standard Payment")
      .post(bankDetailsPageUrl: String)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("bankDetails_accountName",_=> "Melvin Loper")
      .formParam("bankDetails_sortCode",_=> "207106")
      .formParam("bankDetail_accountNumber",_=> "44311677")
      .formParam("bankDetail_rollNumber",_=> "0123456789")
      .check(status.is(303))
  }

  def getCYAPage: HttpRequestBuilder = {
    http("Get Check Your Answers Page Standard Payment")
      .get(cyaPageUrl)
      .check(status.is(200))
  }

  def postCYAPage: HttpRequestBuilder = {
    http("Post Check Your Answers Page Standard Payment")
      .post(cyaPageUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .check(status.is(303))
  }

  def getConfirmationPage: HttpRequestBuilder = {
    http("Get Confirmation Page Standard Payment")
      .get(confirmationPageUrl)
      .check(status.is(200))
  }

}

