# API Test Framework

An API test automation framework for the **Books** endpoints of [FakeRestAPI](https://fakerestapi.azurewebsites.net/index.html), built with **Maven**, **Groovy**, and **Spock**, using **Rest-Assured** for HTTP calls and **Allure** for reporting.

Happy-path and negative tests are split into separate packages (`books/happyPath/` and `books/negative/`) and tagged for selective execution. The project uses the GitHub Actions pipeline that builds the image, runs the suite, and publishes an Allure report to GitHub Pages.

---

## Prerequisites

| Tool      | Minimum version | Check installed                  | How to install                                         |
|-----------|-----------------|----------------------------------|--------------------------------------------------------|
| **Git**   | any             | `git --version`                  | [git-scm.com/downloads](https://git-scm.com/downloads) |
| **JDK**   | 17              | `javac -version` (must show 17+) | [adoptium.net](https://adoptium.net/) — Temurin 17 LTS |
| **Maven** | 3.8             | `mvn -v`                         | macOS: `brew install maven` · Linux: `sudo apt install maven` · Windows: [maven.apache.org](https://maven.apache.org/download.cgi) |

Prefer not to install a JDK locally? Skip ahead to [Running in Docker](#running-in-docker) — Docker is the only requirement.

---

## Quick start

### 1. Clone

```bash

git clone https://github.com/Diana-Savova/books-api-test.git

```

### 2. Run the tests

```bash

mvn test

```

### 3. View the report

```bash

mvn allure:serve

```

Renders the HTML report and opens it in your browser. Ctrl-C to stop the server.

---

## Endpoints covered

`/api/v1/Books`:

- `GET    /api/v1/Books`

- `GET    /api/v1/Books/{id}`

- `POST   /api/v1/Books`

- `PUT    /api/v1/Books/{id}`

- `DELETE /api/v1/Books/{id}`

---

## Running the tests

### Run everything

```bash

mvn test

```

### Run a single spec

```bash

mvn test -Dtest=GetBookByIdHappyPathSpec

```

### Point at a different environment

```bash

mvn test -Dapi.base.url=https://my-other-api.example.com

```

### Run a subset by tag

The suite is tagged so you can run subsets without changing code — useful in CI when you want only happy-path tests on every PR but the full suite on `main`. Every spec is tagged with `happy-path` or `negative`, plus `critical` and `books`:

```bash

mvn test -Dgroups=happy-path           # only happy-path specs

mvn test -Dgroups=negative             # only negative specs

mvn test -DexcludedGroups=negative     # skip negative specs

```

### Run a subset by package

```bash

mvn test -Dtest='com.example.apitests.books.happyPath.*Spec'

```

---

## Running in Docker

The container runs the suite *and* renders the Allure HTML report — no JDK or Maven needed on the host, only Docker.

```bash

docker build -t api-test .

mkdir reports

docker run --rm -v "$(pwd)/reports:/app/target" api-test

```

Then open `reports/site/allure-maven-plugin/index.html` to view the report.

To target a different API:

```bash

docker run --rm -e API_BASE_URL=https://staging.example.com \

  -v "$(pwd)/reports:/app/target" api-test

```

---

## CI/CD — GitHub Actions

`.github/workflows/ci.yml` runs on every push to `main` and every PR targeting `main`. Manual triggers via the **Run workflow** button accept an optional base-URL override.

### Viewing CI results

Three ways to inspect results after a workflow run:

**1. The "Spock test results" check run** (inline, no download)

On any commit, PR, or workflow run page you'll see a check called **"Spock test results"** with a pass/fail summary:
> ✅ Spock test results · 43 passed, 1 failed, 0 skipped

Click to expand failure details inline.

**2. Downloadable Allure HTML artifact**

At the bottom of any workflow run page, the `allure-report-html` artifact contains the full report. Download, unzip, open `index.html`. This works even when GitHub Pages isn't enabled.

**3. The live GitHub Pages site** (on `main` pushes)

Once Pages is enabled, every push to `main` updates `https://diana-savova.github.io/books-api-test/`. This is the friendliest way to share results with non-developers.

---

## Dependency and security notes

### CVE-pinned transitive dependencies

The POM's `<dependencyManagement>` section pins three artifact families that would otherwise come in at vulnerable versions through transitive resolution: **`jackson-bom` 2.18.6**, **`logback-core` / `logback-classic` 1.5.32**, and **`commons-lang3` 3.18.0**. Each addresses a published GHSA advisory ranging from CVSS 1.8 to 8.7.

To verify the patched versions are actually on the classpath after a change:

```bash

mvn dependency:tree | findstr -i "jackson logback commons-lang3"   # Windows

mvn dependency:tree | grep -iE "jackson|logback|commons-lang3"     # macOS / Linux

```

### JUnit Platform alignment

A `junit-bom` import in `<dependencyManagement>` keeps every `junit-platform-*` artifact at a matching version. This prevents the `NoSuchMethodError: TestDescriptor.getAncestors()` failure that arises when Surefire and Spock disagree on the JUnit Platform version.

Surefire's plugin-level `<dependencies>` block also declares `junit-platform-launcher` with an explicit version — plugin dependencies don't inherit from the project's `<dependencyManagement>`, so the version has to be hard-coded there to match the BOM. 