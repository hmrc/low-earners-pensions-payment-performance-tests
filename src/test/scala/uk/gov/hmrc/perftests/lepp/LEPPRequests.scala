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

  val standardPaymentStartPageUrl: String = baseurl + "//low-earners-pensions-payment/start"

  val underpaymentStartPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/start"

  val standardPaymentDashboardPageUrl: String = baseurl + "/low-earners-pensions-payment/dashboard"

  val underpaymentDashboardPageUrl: String = baseurl + "/low-earners-pensions-payment/underpayment/dashboard"

  val standardPaymentBreakdownPageUrl: String = baseurl + "//low-earners-pensions-payment/breakdown"

  val underpaymentBreakdownPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/breakdown"

  val standardPaymentBankDetailsPageUrl: String = baseurl + "//low-earners-pensions-payment/bank-details"

  val underpaymentBankDetailsPageUrl: String = baseurl + "//low-earners-pensions-payment/underpayment/bank-details"

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

  def postLoginStandardPayment: HttpRequestBuilder = {
    http("Post Login Details Standard Payment")
      .post(loginUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("redirectionUrl",_ => standardPaymentStartPageUrl)
      .formParam("credentialStrength",_ => "strong")
      .formParam("confidenceLevel",_ => "250")
      .formParam("nino",_ => "AA123456D")
      .formParam("affinityGroup",_ => "Individual")
      .formParam("authorityId", _ => "someId")
      .check(status.is(303))
  }

  def postLoginUnderpayment: HttpRequestBuilder = {
    http("Post Login Details Underpayment")
      .post(loginUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("redirectionUrl", _ => underpaymentStartPageUrl)
      .formParam("credentialStrength", _ => "strong")
      .formParam("confidenceLevel", _ => "250")
      .formParam("nino", _ => "AA123456D")
      .formParam("affinityGroup",_ => "Individual")
      .formParam("authorityId", _ => "someId")
      .check(status.is(303))
  }

  def getStandardPaymentStartPage: HttpRequestBuilder = {
    http("Get Start Page Standard Payment")
      .get(standardPaymentStartPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentStartPage: HttpRequestBuilder = {
    http("Get Start Page Underpayment")
      .get(underpaymentStartPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentDashboardPage: HttpRequestBuilder = {
    http("Get Dashboard Page Standard Payment")
      .get(standardPaymentDashboardPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentDashboardPage: HttpRequestBuilder = {
    http("Get Dashboard Page Underpayment")
      .get(underpaymentDashboardPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentBreakdownPage: HttpRequestBuilder = {
    http("Get Breakdown Page Standard Payment")
      .get(standardPaymentBreakdownPageUrl: String)
      .check(status.is(200))
  }

  def getUnderpaymentBreakdownPage: HttpRequestBuilder = {
    http("Get Breakdown Page Underpayment")
      .get(underpaymentBreakdownPageUrl: String)
      .check(status.is(200))
  }

  def getStandardPaymentBankDetailsPage: HttpRequestBuilder = {
    http("Get Bank Details Page Standard Payment")
      .get(standardPaymentBankDetailsPageUrl: String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postStandardPaymentBankDetailsPage: HttpRequestBuilder = {
    http("Post to Bank Details Page Standard Payment")
      .post(standardPaymentBankDetailsPageUrl: String)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("name",_=> "Teddy Sherringham")
      .formParam("sortCode",_=> "55-00-33")
      .formParam("accountNumber",_=> "12345678")
      .formParam("buildingSocietyRollNumber",_=> "0123456789")
      .check(status.is(303))
  }

  def getUnderpaymentBankDetailsPage: HttpRequestBuilder = {
    http("Get Bank Details Page Underpayment")
      .get(underpaymentBankDetailsPageUrl: String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))
  }

  def postUnderpaymentBankDetailsPage: HttpRequestBuilder = {
    http("Post to Bank Details Page Underpayment")
      .post(standardPaymentBankDetailsPageUrl: String)
      .formParam("csrfToken", _("csrfToken").as[String])
      .formParam("name",_=> "John Smith")
      .formParam("sortCode",_=> "00-11-22")
      .formParam("accountNumber",_=> "12345678")
      .formParam("buildingSocietyRollNumber",_=> "0123456789")
      .check(status.is(303))
  }

  def getStandardPaymentCYAPage: HttpRequestBuilder = {
    http("Get Check Your Answers Page Standard Payment")
      .get(standardPaymentCYAPageUrl)
      .check(status.is(200))
  }

  def postStandardPaymentCYAPage: HttpRequestBuilder = {
    http("Post Check Your Answers Page Standard Payment")
      .post(standardPaymentCYAPageUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .check(status.is(303))
  }

  def getUnderpaymentCYAPage: HttpRequestBuilder = {
    http("Get Check Your Answers Page Underpayment")
      .get(underpaymentCYAPageUrl)
      .check(status.is(200))
  }

  def postUnderpaymentCYAPage: HttpRequestBuilder = {
    http("Post Check Your Answers Page Underpayment")
      .post(underpaymentCYAPageUrl)
      .formParam("csrfToken", _("csrfToken").as[String])
      .check(status.is(303))
  }

  def getStandardPaymentConfirmationPage: HttpRequestBuilder = {
    http("Get Confirmation Page Standard Payment")
      .get(standardPaymentConfirmationPageUrl)
      .check(status.is(200))
  }

  def getUnderpaymentConfirmationPage: HttpRequestBuilder = {
    http("Get Confirmation Page Underpayment")
      .get(underpaymentConfirmationPageUrl)
      .check(status.is(200))
  }

}

