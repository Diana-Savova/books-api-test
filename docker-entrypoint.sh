#!/bin/sh
#
# Container entrypoint.
#
# 1. Runs the Spock test suite. Raw Allure results land in
#    /app/target/allure-results regardless of whether tests pass or fail.
# 2. Renders the static HTML report into
#    /app/target/site/allure-maven-plugin.
# 3. Returns the test suite's original exit code so CI fails when tests fail.
set +e
echo "==> Environment"
echo "PWD: $(pwd)"
echo "API_BASE_URL: ${API_BASE_URL}"
mvn -v
echo
echo "==> Running test suite"
mvn -B -ntp test
TEST_EXIT=$?
echo "==> mvn test exit code: ${TEST_EXIT}"
echo
echo "==> Generating Allure HTML report"
mvn -B -ntp allure:report || \
    echo "WARN: Allure report generation failed - raw results still available in target/allure-results"
echo
echo "==> Final contents of /app/target:"
ls -la /app/target/ 2>/dev/null || echo "  (missing)"
exit $TEST_EXIT
