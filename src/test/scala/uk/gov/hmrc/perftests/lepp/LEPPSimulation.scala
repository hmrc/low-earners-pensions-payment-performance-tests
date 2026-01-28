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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner

import uk.gov.hmrc.perftests.lepp.LEPPRequests._

class LEPPSimulation extends PerformanceTestRunner {

    setup(
      id = "low-earners-pensions-payment-standard-journey",
      description = "Standard Payment Journey"
    ).withRequests(
      getLogin,
      postLoginStandardPayment,
      getStandardPaymentStartPage,
      //getStandardPaymentDashboardPage,
      //getStandardPaymentBreakdownPage,
      //getStandardPaymentBankDetailsPage,
      //postStandardPaymentBankDetailsPage,
      //getStandardPaymentCYAPage,
      //postStandardPaymentCYAPage,
      //getStandardPaymentConfirmationPage
    )

    setup(
      id = "low-earners-pensions-payment-underpayment-journey",
      description = "Underpayment Journey"
    ).withRequests(
      getLogin,
      postLoginUnderpayment,
      //getUnderpaymentStartPage,
      //getUnderpaymentDashboardPage,
      //getUnderpaymentBreakdownPage,
      //getUnderpaymentBankDetailsPage,
      //postUnderpaymentBankDetailsPage,
      //getUnderpaymentCYAPage,
      //postUnderpaymentCYAPage,
      //getUnderpaymentConfirmationPage
    )
      runSimulation()
}
