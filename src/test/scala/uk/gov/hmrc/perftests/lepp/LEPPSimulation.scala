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
import uk.gov.hmrc.perftests.lepp.FormParams.loginFormParams
import uk.gov.hmrc.perftests.lepp.LEPPRequests.*

import java.net.{HttpURLConnection, URL, URLEncoder}
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.*
import scala.io.Source

class LEPPSimulation extends PerformanceTestRunner {

  before {
    val cookieStore = scala.collection.mutable.Map.empty[String, String]

    def bootstrapRequest(
                          method: String,
                          urlStr: String,
                          body: Option[String] = None,
                          followRedirects: Boolean = true
                        ): (Int, String) = {
      val conn = new URL(urlStr).openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod(method)
      conn.setInstanceFollowRedirects(followRedirects)
      conn.setConnectTimeout(15000)
      conn.setReadTimeout(15000)

      if (cookieStore.nonEmpty)
        conn.setRequestProperty("Cookie", cookieStore.map { case (k, v) => s"$k=$v" }.mkString("; "))

      body.foreach { requestBody =>
        conn.setDoOutput(true)
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        val bytes = requestBody.getBytes(StandardCharsets.UTF_8)
        val output = conn.getOutputStream
        try { output.write(bytes); output.flush() }
        finally output.close()
      }

      try {
        val status = conn.getResponseCode

        conn.getHeaderFields.asScala
          .filter { case (name, _) => name != null && name.equalsIgnoreCase("Set-Cookie") }
          .values
          .flatMap(_.asScala)
          .foreach { cookie =>
            val parts = cookie.split("=", 2)
            if (parts.length == 2)
              cookieStore(parts(0).trim) = parts(1).split(";", 2).head.trim
          }

        val stream = if (status < 400) conn.getInputStream else conn.getErrorStream
        val responseBody = if (stream == null) "" else {
          val source = Source.fromInputStream(stream, "UTF-8")
          try source.mkString finally source.close()
        }

        (status, responseBody)
      } finally conn.disconnect()
    }

    println("[SETUP] Starting one-time LEPP authentication")

    // Step 1: GET login page
    val (loginPageStatus, loginPage) = bootstrapRequest("GET", loginUrl)
    require(loginPageStatus == 200, s"Login page failed: HTTP $loginPageStatus")

    val csrf = """name="csrfToken"\s+value="([^"]+)"""".r
      .findFirstMatchIn(loginPage)
      .map(_.group(1))
      .getOrElse(throw new IllegalStateException("Could not extract CSRF token from login page"))

    // Step 2: POST login
    val baseParams = Seq(
      "csrfToken"      -> csrf,
      "redirectionUrl" -> startPageUrl,
      "nino"           -> "AA123456D"
    )

    val allParams = (baseParams ++ loginFormParams.toSeq)
      .groupBy(_._1)
      .values
      .flatMap(_.lastOption)
      .toSeq
      .map { case (k, v) => s"${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}" }
      .mkString("&")

    val (loginStatus, _) = bootstrapRequest("POST", loginUrl, body = Some(allParams), followRedirects = false)
    require(loginStatus >= 200 && loginStatus < 400, s"Login POST failed: HTTP $loginStatus")
    println(s"[SETUP] Login POST status: $loginStatus")
    println(s"[SETUP] Cookies captured: ${cookieStore.size}")

    // Step 3: GET session page and extract token
    val (sessionStatus, sessionPage) = bootstrapRequest("GET", sessionUrl)
    require(sessionStatus == 200, s"Session page failed: HTTP $sessionStatus")

    val tokenRegex = """(?s)<td[^>]*data-session-id=["']authToken["'][^>]*>.*?(Bearer\s+[^,\s<]+)""".r
    val token = tokenRegex
      .findFirstMatchIn(sessionPage)
      .map(_.group(1))
      .getOrElse(throw new IllegalStateException("Could not extract bearer token from session page"))

    require(token.startsWith("Bearer "), s"Extracted token is not in expected Bearer format")

    SharedToken.bearerToken = token

    println("[SETUP] Authentication succeeded")
    println(s"[SETUP] Bearer token extracted: ${if (token.nonEmpty) "YES" else "NO"}")
    println(s"[SETUP] Token length: ${token.length} chars")
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
