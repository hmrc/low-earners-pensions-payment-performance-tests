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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.lepp.LEPPRequests._
import java.net.{HttpURLConnection, URL, URLEncoder}
import java.io.DataOutputStream
import scala.jdk.CollectionConverters._
import uk.gov.hmrc.perftests.lepp.FormParams.*

class LEPPSimulation extends PerformanceTestRunner {

  before {
    val cookieStore = scala.collection.mutable.Map[String, String]()

    def request(
      method: String,
      urlStr: String,
      body: Option[String] = None,
      followRedirects: Boolean = true
    ): (Int, String) = {
      val conn   = new URL(urlStr).openConnection().asInstanceOf[HttpURLConnection]
      conn.setInstanceFollowRedirects(followRedirects)
      conn.setRequestMethod(method)
      conn.setRequestProperty("Cookie", cookieStore.map { case (k, v) => s"$k=$v" }.mkString("; "))
      body.foreach { b =>
        conn.setDoOutput(true)
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        val out = new DataOutputStream(conn.getOutputStream)
        out.writeBytes(b)
        out.flush()
      }
      // After getResponseCode, capture ALL response headers for Set-Cookie (case-insensitive)
      conn.getHeaderFields.asScala
        .filter { case (k, _) => k != null && k.equalsIgnoreCase("Set-Cookie") }
        .values
        .flatMap(_.asScala)
        .foreach { c =>
          val parts = c.split("=", 2)
          if (parts.length == 2) cookieStore(parts(0).trim) = parts(1).split(";").head.trim
        }
      val status = conn.getResponseCode
      val stream = if (status < 400) conn.getInputStream else conn.getErrorStream
      val body2  = scala.io.Source.fromInputStream(stream).mkString
      conn.disconnect()
      (status, body2)
    }

    // Step 1: GET login page for CSRF token
    val (_, loginPage) = request("GET", loginUrl)
    val csrf           = """name="csrfToken" value="([^"]+)"""".r
      .findFirstMatchIn(loginPage)
      .map(_.group(1))
      .getOrElse("")
    println(s"[SETUP] CSRF token: ${csrf.take(20)}...")

    // Step 2: POST login - include all loginFormParams
    val baseParams = Seq(
      "csrfToken"      -> csrf,
      "redirectionUrl" -> startPageUrl,
      "nino"           -> "AA123456D"
    )
    val allParams  = (baseParams ++ loginFormParams.toSeq)
      .map { case (k, v) => s"${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}" }
      .mkString("&")

    val (loginStatus, _) = request("POST", loginUrl, Some(allParams), followRedirects = false)
    println(s"[SETUP] Login POST status: $loginStatus")
    println(s"[SETUP] Cookies after login: ${cookieStore.mkString(", ")}")

    // Step 3: GET session page and extract bearer token
    val (sessionStatus, sessionPage) = request("GET", sessionUrl)

    val tokenRegex                   = """(?s)<td[^>]*data-session-id=["']authToken["'][^>]*>.*?(Bearer\s+[^,\s<]+)""".r
    SharedToken.bearerToken = tokenRegex.findFirstMatchIn(sessionPage).map(_.group(1)).getOrElse("")
    println(s"[SETUP] Token extracted: ${SharedToken.bearerToken.take(40)}...")
    println(s"[SETUP] Session page status: $sessionStatus")
    println(s"[SETUP] Session page (first 2000 chars): ${sessionPage.take(2000)}")
    println(s"[SETUP] authLoginstubRoot = $authLoginstubRoot")
    println(s"[SETUP] sessionUrl = $sessionUrl")
    println(s"[SETUP] loginUrl = $loginUrl")
    println(s"[SETUP] Cookies after login: ${cookieStore.mkString(", ")}")

  }

  setup(
    id = "low-earners-pensions-payment-journey",
    description = "Standard Payment Journey"
  ).withRequests(
    getLogin,
    postLogin("AA123456D"),
    getStartPage,
    getDashboardPage,
    getBreakdownPage,
    getBankDetailsPage,
    postBankDetailsPage,
    getCYAPage,
    postCYAPage,
    getConfirmationPage
  )

  setup(
    id = "lepp-backend-pta-journey",
    description = "PTA - LEPP Journey"
  ).withRequests(
    getLeppSummary
  )

  runSimulation()
}
