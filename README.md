# MissionQA — SDET Practical Assignment

**API under test:** ReqRes — `https://reqres.in`
**UI under test:** SauceDemo — `https://www.saucedemo.com`
**Stack:** Java 11 · Maven · Cucumber 7 (TestNG runner) · Selenium 4 · REST Assured 5 · WireMock 3 · Apache JMeter

**Video walkthrough:** [add link here before submission]

---

## 1. Project Structure

```
├── .github/workflows/api-tests.yml   CI: live API tests, mock API tests, UI tests
├── src/main/java/                    api clients, driver setup, config, browser factory, CSV reader
├── src/test/java/                    page objects, step defs, hooks, WireMock hook, runner, CSV data-driven test
├── src/test/resources/features/      UI-Test.feature, API-Test.feature, API-Mock-Test.feature
├── src/test/resources/TestData/      config properties + create-users.csv
├── postman/                          Postman collection (git-sync YAML + exported v2.1 JSON) + environment
├── wiremock/mappings/                4 WireMock stub JSON files
├── jmeter/                           Thread_Group_1_Rt.jmx + committed results.jtl + html-report/
├── monitoring/                       UptimeRobot config notes + screenshots
└── Screenshots/                      UI failure screenshots (auto-captured)
```

## 2. Setup

```bash
git clone <repo-url>
cd MissionQa--Rutull-master
mvn clean compile
```

Requires Java 11+, Maven, and (for UI tests) Chrome installed — driver binaries are fetched automatically via WebDriverManager, nothing to install manually.

## 3. Run Commands

| What | Command |
|---|---|
| Everything (UI + live API) | `mvn clean test` |
| UI only | `mvn -Dcucumber.filter.tags="@ui" clean test` |
| Live API only | `mvn -Dcucumber.filter.tags="@api and not @mock" clean test` |
| Mock API only (WireMock, localhost:8089) | `mvn -Denv=mock -Dcucumber.filter.tags="@mock" test` |
| CSV data-driven API test | `mvn test -Dtest=CreateUserDataDrivenTest` |
| Generate Cucumber HTML report | `mvn verify -DskipTests` → open `test-output/cucumber-reports/cucumber-pretty.html` |

The default runner tag filter is `@ui or (@api and not @mock)` — mock scenarios only run when `-Denv=mock` is explicitly passed, so they never accidentally hit the live API.

## 4. Test Coverage

- **UI (5 Cucumber scenarios):** checkout total/tax, invalid login, sort by price, remove-all-cart, checkout without ZIP — all using Page Object Model with `ThreadLocal<WebDriver>`.
- **API (9 Cucumber scenarios + 1 outline):** list users across pages, single user found/404, create (2 examples), login success/failure, delayed response, update, delete — with header and response-time assertions.
- **API data-driven (5 TestNG cases):** `CreateUserDataDrivenTest` creates users from `create-users.csv` via a `@DataProvider`, independent of the Cucumber flow.
- **API mock (3 scenarios):** same list/404/login flows re-run against 4 WireMock stubs on `localhost:8089` instead of the live API.

## 5. Postman (Manual API Testing)

Collection: `postman/postman/collections/ReqRes API/` (git-sync YAML, 9 requests — GET/POST/PUT/DELETE) plus the exported `postman/postman/ReqRes-API.postman_collection.json` for direct import.
Environment: `postman/postman/collections/ReqRes-Env.postman_environment.json` (`baseUrl`, `apiKey`, `userId`, `job`).
Every request has pre-request scripts (dynamic data, token/id chaining) and post-response assertions (status code, response time < 2000ms, `Content-Type` header, schema checks on key requests).

**To run:** Postman → Import → select the exported JSON and the environment file → Collection Runner → Run.

## 6. WireMock (API Mocking)

4 stubs in `wiremock/mappings/`: `get-users-page1.json`, `get-users-page2.json`, `get-user-404.json`, `post-login-success.json`. `WireMockHook` starts the server on port 8089 before any `@mock`-tagged scenario and stops it after. `GenericApiClient` reads the `-Denv=mock` system property to point REST Assured at `localhost:8089` instead of `https://reqres.in`.

## 7. JMeter (Load Testing)

Plan: `jmeter/Thread_Group_1_Rt.jmx` — Baseline (10 users, 10s ramp-up, 5 iterations) and Stress (50 users, 20s ramp-up, 5 iterations) thread groups, both hitting `GET /api/users?page=1` with a 200-status assertion and a <3000ms duration assertion.

**Committed run evidence** (`jmeter/results.jtl`, `jmeter/html-report/`):

| Metric | Baseline (10 users) | Stress (50 users) |
|---|---|---|
| Requests | 50 | 250 |
| Errors | 0 | 0 |
| Mean response time | 103.44 ms | 77.90 ms |
| 95th percentile | 514.2 ms | 146.7 ms |
| Max response time | 1454 ms | 1308 ms |
| Throughput | 5.45 req/s | 12.66 req/s |

Both groups passed 100% of assertions. Stress throughput was over 2x baseline with no increase in error rate; the rare 1.3–1.5s latency spikes did not breach the 3000ms threshold.

**To re-run:** `jmeter -n -t jmeter/Thread_Group_1_Rt.jmx -l jmeter/results.jtl -e -o jmeter/html-report` (delete `jmeter/html-report/` first if it already exists).

## 8. Health Monitoring

UptimeRobot (free tier) monitors `https://reqres.in/api/users?page=1` every 5 minutes, alerting on non-2xx responses or >2000ms response time. Configuration and live dashboard screenshots are in `monitoring/screenshots/`.

## 9. CI/CD

`.github/workflows/api-tests.yml` runs on every push/PR to `main`:
- **api-tests job:** live API suite, then the mock API suite as a separate step, then uploads the Cucumber report.
- **ui-tests job:** headless Chrome UI suite, uploads the report and any failure screenshots.

## 10. Known Limitations

- JMeter load tests are not part of the CI pipeline (resource-intensive) — run manually and results are committed as evidence.
- API health monitoring is configured in an external free-tier tool (UptimeRobot), not code in this repo.
- `TestData.properties` contains a non-production placeholder API key for the free-tier demo API; real credentials would go through GitHub Secrets / environment variables instead.
