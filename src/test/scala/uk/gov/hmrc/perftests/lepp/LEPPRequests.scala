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

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.{HttpConfiguration, ServicesConfiguration}
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.core.session.Expression

import scala.util.Random


object LEPPRequests extends HttpConfiguration with ServicesConfiguration {

  val baseurl: String = baseUrlFor("base-url")

  val route: String = "low-earners-pensions-payment"

  val authLoginstubRoot: String = baseUrlFor("auth-login-stub")

  val loginUrl: String = authLoginstubRoot + "/auth-login-stub/gg-sign-in?continue=/low-earners-pensions-payment"

  val standardPaymentStartPageUrl: String = baseurl + "//low-earners-pensions-payment/start"

  val underpaymentStartPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/start"

  val standardPaymentDashboardPageUrl: String = baseurl + "/low-earners-pensions-payment/dashboard"

  val underpaymentDashboardPageUrl: String = baseurl + "/low-earners-pensions-payment/underpayment/dashboard"

  val standardPaymentBreakdownPageUrl: String = baseurl + "//low-earners-pensions-payment/breakdown"

  val underpaymentBreakdownPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/breakdown"

  val standardPaymentBankDetailsPageUrl: String = baseurl + "//members-protections-and-enhancements/bank-details"

  val underpaymentBankDetailsPageUrl: String = baseurl + "//members-protections-and-enhancements/underpayment/bank-details"

  val standardPaymentCYAPageUrl: String = baseurl + "//low-earners-pensions-payment/check-your-answers"

  val underpaymentCYAPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/check-your-answers"

  val standardPaymentConfirmationPageUrl: String = baseurl + "//low-earners-pensions-payment/confirmation"

  val underpaymentConfirmationPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/confirmation"

  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  val csrfToken: Expression[String] = "#{csrfToken}"

  def getLogin: HttpRequestBuilder = {
    http("get Login Details")
      .get(loginUrl)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postLogin: HttpRequestBuilder = {
    http("Post Login Details")
      .post(loginUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("authorityId", _ => "")
      .formParam("gatewayToken",_ => "")
      .formParam("redirectionUrl",_ => "")
      .formParam("credentialStrength",_ => "strong")
      .formParam("confidenceLevel",_ => "50")
      .formParam("affinityGroup",_ => "Agent")
      .formParam("usersName",_ => "")
      .formParam("email",_ => "user@test.com")
      .formParam("credentialRole",_ => "User")
      .formParam("nino",_ => "")
      .formParam("groupIdentifier",_ => "")
      .formParam("agent.agentId",_ => "")
      .formParam("agent.agentCode",_ => "")
      .formParam("agent.agentFriendlyName",_ => "")
      .formParam("unreadMessageCount",_ => "")
      .formParam("mdtp.sessionId",_ => "")
      .formParam("mdtp.deviceId",_ => "")
      .formParam("presets-dropdown",_ => "SA")
      .formParam("enrolment[0].name",_ => "")
      .formParam("enrolment[0].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[0].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[0].state",_ => "Activated")
      .formParam("enrolment[1].name",_ => "")
      .formParam("enrolment[1].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[1].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[1].state",_ => "Activated")
      .formParam("enrolment[2].name",_ => "")
      .formParam("enrolment[2].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[2].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[2].state",_ => "Activated")
      .formParam("enrolment[3].name",_ => "")
      .formParam("enrolment[3].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[3].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[3].state",_ => "Activated")
      .formParam("enrolment[4].name",_ => "")
      .formParam("enrolment[4].taxIdentifier[0].name",_ => "")
      .formParam("enrolment[4].taxIdentifier[0].value",_ => "")
      .formParam("enrolment[4].state",_ => "Activated")
      .formParam("itmp.givenName",_ => "")
      .formParam("itmp.middleName",_ => "")
      .formParam("itmp.familyName",_ => "")
      .formParam("itmp.dateOfBirth",_ => "")
      .formParam("itmp.address.line1",_ => "")
      .formParam("itmp.address.line2",_ => "")
      .formParam("itmp.address.line3",_ => "")
      .formParam("itmp.address.line4",_ => "")
      .formParam("itmp.address.line5",_ => "")
      .formParam("itmp.address.postCode",_ => "")
      .formParam("itmp.address.countryName",_ => "")
      .formParam("itmp.address.countryCode",_ => "")
      .check(status.is(303))
  }

  def getStandardPaymentStartPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(standardPaymentStartPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentStartPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(underpaymentStartPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentDashboardPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(standardPaymentDashboardPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentDashboardPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(underpaymentDashboardPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentBreakdownPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(standardPaymentBreakdownPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentBreakdownPage: HttpRequestBuilder = {
    http("Get Start Page")
      .get(underpaymentBreakdownPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentBankDetailsPage: HttpRequestBuilder = {
    http("Get Members Details Page")
      .get(standardPaymentBankDetailsPageUrl: String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postStandardPaymentBankDetailsPage: HttpRequestBuilder = {
    http("Post to Member-Details Page")
      .post(standardPaymentBankDetailsPageUrl: String)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("name",_=> "Teddy Sherringham")
      .formParam("sortCode",_=> "55-00-33")
      .formParam("accountNumber",_=> "12345678")
      .formParam("buildingSocietyRollNumber",_=> "0123456789")
      .check(status.is(303))
  }

  def getUnderpaymentBankDetailsPage: HttpRequestBuilder = {
    http("Get Members Details Page")
      .get(underpaymentBankDetailsPageUrl: String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postUnderpaymentBankDetailsPage: HttpRequestBuilder = {
    http("Post to Member-Details Page")
      .post(standardPaymentBankDetailsPageUrl: String)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("name",_=> "John Smith")
      .formParam("sortCode",_=> "00-11-22")
      .formParam("accountNumber",_=> "12345678")
      .formParam("buildingSocietyRollNumber",_=> "0123456789")
      .check(status.is(303))
  }

  def getStandardPaymentCYAPage: HttpRequestBuilder = {
    http("Get Check Your Answers Page")
      .get(standardPaymentCYAPageUrl)
      .check(status.is(200))
  }

  def postStandardPaymentCYAPage: HttpRequestBuilder = {
    http("Post Check Your Answers Page")
      .post(standardPaymentCYAPageUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .check(status.is(303))
  }

  def getUnderpaymentCYAPage: HttpRequestBuilder = {
    http("Get Check Your Answers Page")
      .get(underpaymentCYAPageUrl)
      .check(status.is(200))
  }

  def postUnderpaymentCYAPage: HttpRequestBuilder = {
    http("Post Check Your Answers Page")
      .post(underpaymentCYAPageUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .check(status.is(303))
  }

  def getStandardPaymentConfirmationPage: HttpRequestBuilder = {
    http("Get Results Page")
      .get(standardPaymentConfirmationPageUrl)
      .check(status.is(200))
  }

  def getUnderpaymentConfirmationPage: HttpRequestBuilder = {
    http("Get Results Page")
      .get(underpaymentConfirmationPageUrl)
      .check(status.is(200))
  }

}

