lazy val root = (project in file("."))
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "low-earners-pensions-payment-performance-tests",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "3.3.7",
      //implicitConversions & postfixOps are Gatling recommended -language settings
      scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps"),
      // Enabling sbt-auto-build plugin provides DefaultBuildSettings with default `testOptions` from `sbt-settings` plugin.
      // These testOptions are not compatible with `sbt gatling:test`. So we have to override testOptions here.
      retrieveManaged := true,
      console / initialCommands := "import uk.gov.hmrc._",
      Test / parallelExecution := false,
      Test / testOptions := Seq.empty,
      libraryDependencies ++= Seq(
          "uk.gov.hmrc" %% "performance-test-runner" % "6.3.0"
      ).map(_ % Test)
  )
  )
