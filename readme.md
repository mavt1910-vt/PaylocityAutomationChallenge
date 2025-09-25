# Paylocity Automation Challenge

End-to-end **API + UI (Selenium)** test suite using **TestNG**, **Rest Assured**, and **Maven**.
Supports running from IDE, Maven CLI, or **Docker** (everything-in-one) and produces **HTML reports**.

---

## 📁 Project Structure (ASCII map)

```
PaylocityAutomationChallenge/
├─ pom.xml                         # Maven config (deps, Surefire, single suite RegressionSuite.xml)
├─ README.md                       # You are here: setup, how-to-run, and platform notes
├─ RegressionSuite.xml             # TestNG suite (orchestrates all tests)
├─ Dockerfile                      # Image to run the whole suite (API + UI) and export reports
├─ src/
│  ├─ main/
│  │  └─ java/
│  │     ├─ API/
│  │     │  ├─ core/
│  │     │  │  └─ ApiHelper.java               # REST client (base URI, auth, headers, logging)
│  │     │  ├─ factories/
│  │     │  │  └─ EmployeeDataFactory.java     # Payload builders (faker / variants)
│  │     │  ├─ models/
│  │     │  │  └─ employees/                   # Request POJOs (create/update)
│  │     │  ├─ responses/
│  │     │  │  └─ employees/                   # Response POJOs (EmployeeResponse)
│  │     │  └─ services/
│  │     │     └─ EmployeeService.java         # Service layer (maps Response → objects)
│  │     ├─ UI/
│  │     │  └─ core/
│  │     │     ├─ BrowserType.java             # Browser enum
│  │     │     ├─ DriverOptions.java           # Driver parameters (browser/headless/remote/gridUrl)
│  │     │     └─ DriverManager.java           # WebDriver (local/remote, Docker-aware headless flags)
│  │     └─ Common/
│  │        └─ LogHelper.java                  # Logging + pretty JSON
│  └─ test/
│     ├─ java/
│     │  ├─ API/                               # API test cases (TestNG)
│     │  └─ UI/                                # UI test cases (TestNG)
│     └─ resources/                            # (Optional) configs/properties
├─ reports/                        # (Created by Docker run): exported reports from the container
└─ test-output/                    # (Created by local/Maven run): TestNG HTML reports
```

---

## ✅ Requirements

Pick **one** path to run the suite:

### A) IDE (IntelliJ IDEA)

* **JDK 17+**
* IntelliJ with **TestNG** plugin
* (Optional) **Google Chrome** installed locally (if you run UI non-headless)

### B) Maven (command line)

* **JDK 17**
* **Maven 3.9+** on PATH (`mvn -v`)

### C) Docker (all-in-one)

* **Docker Desktop** (Windows/macOS) or **Docker Engine** (Linux)
* On Windows: **WSL2** + hardware virtualization enabled (BIOS)

---

## ▶️ How to Run

### 1) From IDE (IntelliJ)

* **Run the whole suite**: right-click `RegressionSuite.xml` → **Run**.
* **Run a class**: right-click the test class → **Run**.
* **Run a single test**: right-click the `@Test` method → **Run**.

> Default UI mode is **non-headless**; inside Docker, `DriverManager` auto-forces **headless**.

### 2) With Maven

```bash
mvn clean test
```

Reports:

* `test-output/index.html` (TestNG)
* `test-output/emailable-report.html`
* `target/surefire-reports/…` (Surefire logs)

### 3) With Docker

#### Build the image (run in repo root)

* **macOS (Apple Silicon: M1/M2/M3)** → recommended to force amd64 for Chrome:

  ```bash
  docker build --platform=linux/amd64 -t paylocity-tests .
  ```
* **macOS (Intel), Linux, Windows**:

  ```bash
  docker build -t paylocity-tests .
  ```

#### Run and export reports

* **Windows (PowerShell)**:

  ```powershell
  mkdir .\reports -Force
  docker run --rm -v "${PWD}\reports:/reports" paylocity-tests
  ```

* **macOS (Intel/Apple Silicon)**:

  ```bash
  mkdir -p ./reports
  docker run --rm -v "$PWD/reports:/reports" paylocity-tests
  ```

* **Linux**:

  ```bash
  mkdir -p ./reports
  docker run --rm -v "$PWD/reports:/reports" paylocity-tests
  ```

After it finishes, open on your host:

* `reports/test-output/index.html`
* `reports/test-output/emailable-report.html`

> Notes
> • If your `ConfigManager` already loads properties from the classpath, you don’t need to pass `-e` env vars.
> • To override config at runtime, add `-e NAME=value` to `docker run`.
> • On Windows, ensure drive **C:** is shared in Docker Desktop (Settings → Resources → File Sharing).

---

## 💻 Platform-specific Docker tips

* **macOS (Apple Silicon)**

    * Best compatibility: build with `--platform=linux/amd64` (Chrome stable).
    * Alternative (native arm64): adjust Dockerfile to use **Chromium** instead of Chrome.

* **macOS (Intel)**

    * Use the standard build/run commands; no special flags needed.

* **Linux**

    * If running rootless Docker, ensure your user can mount volumes to `./reports`.
    * Install GPU/Video deps only if you modify the image to run browsers non-headless.

* **Windows**

    * Docker Desktop requires **WSL2** and virtualization enabled in BIOS.
    * Use PowerShell path style in `-v` (as shown above).

---

## ⚙️ TestNG Parameters (optional)

`BaseTest` reads optional parameters from the suite XML:

```xml
<!-- Example overrides (uncomment to apply) -->
<!--
<parameter name="browser" value="FIREFOX"/>
<parameter name="headless" value="true"/>
<parameter name="remote" value="true"/>
<parameter name="gridUrl" value="http://selenium:4444/wd/hub"/>
-->
```

* **Defaults**: `CHROME`, `headless=false`, `remote=false`.
* **Docker**: headless is forced automatically by `DriverManager`.

---

## 🧪 Reports

* **IDE / Maven**: generated under `test-output/` in the project root.
* **Docker**: exported to your host under `./reports/test-output/`.

---

## ❗Troubleshooting

* **Docker on Windows**: “Virtualization support not detected” → enable VT-x/AMD-V in BIOS, enable **WSL2**, then restart Docker Desktop.
* **macOS Apple Silicon**: if Chrome install fails during build, rebuild with `--platform=linux/amd64` (recommended), or switch the Dockerfile to Chromium for native arm64.
* If UI tests are heavy: increase Docker Desktop resources (Settings → Resources → CPU/RAM).
