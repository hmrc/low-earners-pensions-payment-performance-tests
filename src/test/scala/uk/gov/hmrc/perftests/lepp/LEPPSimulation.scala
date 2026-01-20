/*
 * Copyright 2023 HM Revenue & Customs
 *
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
      postLogin,
      getStandardPaymentStartPage,
      getStandardPaymentDashboardPage,
      getStandardPaymentBreakdownPage,
      getStandardPaymentBankDetailsPage,
      postStandardPaymentBankDetailsPage,
      getStandardPaymentCYAPage,
      postStandardPaymentCYAPage,
      getStandardPaymentConfirmationPage
    )

    setup(
      id = "low-earners-pensions-payment-underpayment-journey",
      description = "Underpayment Journey"
    ).withRequests(
      getLogin,
      postLogin,
      getUnderpaymentStartPage,
      getUnderpaymentDashboardPage,
      getUnderpaymentBreakdownPage,
      getUnderpaymentBankDetailsPage,
      postUnderpaymentBankDetailsPage,
      getUnderpaymentCYAPage,
      postUnderpaymentCYAPage,
      getUnderpaymentConfirmationPage
    )

  runSimulation()
}
