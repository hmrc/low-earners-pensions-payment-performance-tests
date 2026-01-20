Low Earners Pensions Payment Performance Tests Link: https://github.com/hmrc/low-earners-pensions-payment-performance-tests

Performance test suite for the Low Earners Pensions Payment, using performance-test-runner under the hood.

1. Services to run (for local testing)
   Start the docker desktop application (and make sure the mongodb is running on the docker) Start LEPP services as follows: sm2 --start LEPP_ALL To enable test only endpoint for local testing.

2. To run smoke tests
   Run below command in terminal: sbt -Dperftest.runSmokeTest=true -DrunLocal=true "Gatling / test"

3. To run full tests
   Run below command in terminal: sbt -DrunLocal=true "Gatling / test"

3. To run full tests in staging
   Go to Jenkins Performance Tests Job on Staging Click on 'Build with parameters' Enter branch name (leave blank if the tests needs to run from the main branch) Set below parameters: Load: 100 ramp_up: 1 constant_rate: 8 Then click 'Build'

5. Logging
   The default log level for all HTTP requests is set to WARN. Configure logback.xml to update this if required.

6. WARNING
   Do NOT run a full performance test against staging from your local machine. Please implement a new performance test job and execute your job from the dashboard in Performance Jenkins.

7. License
   This code is open source software licensed under the Apache 2.0 License.