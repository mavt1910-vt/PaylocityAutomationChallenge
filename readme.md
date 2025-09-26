Paylocity Automation Challenge

End-to-end API + UI (Selenium) test suite using TestNG, Rest Assured, and Maven.
Run it from the IDE, Maven CLI, or Docker (everything-in-one). Produces HTML reports.

📁 Project Structure
PaylocityAutomationChallenge/
├─ pom.xml                         # Maven config (deps, Surefire, single suite RegressionSuite.xml)
├─ README.md                       # You are here: setup, how-to-run, platform notes
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
│  │     ├─ Common/
│  │     │  └─ LogHelper.java                  # Logging + pretty JSON
│  │     └─ UI/
│  │        ├─ components/
│  │        │  ├─ DeleteConfirmationComponent.java
│  │        │  ├─ EmployeeFormComponent.java
│  │        │  └─ EmployeesTableComponent.java
│  │        ├─ core/
│  │        │  ├─ BaseUI.java
│  │        │  ├─ BrowserType.java
│  │        │  ├─ ConfigManager.java
│  │        │  ├─ DriverManager.java
│  │        │  ├─ DriverOptions.java
│  │        │  └─ EmployeeTableColumn.java
│  │        └─ pages/
│  │           ├─ BenefitsDashboardPage.java
│  │           └─ LoginPage.java
│  └─ test/
│     ├─ java/
│     │  ├─ API/
│     │  │  └─ EmployeesAPITest.java           # API cases (create/get/put/delete & edge cases)
│     │  └─ UI/
│     │     └─ BaseTest.java                   # Base TestNG (reads params, starts/stops driver)
│     └─ resources/                            # (Optional) configs/properties
├─ reports/                        # (Created by Docker run): exported reports from the container
└─ test-output/                    # (Created by local/Maven run): TestNG HTML reports

✅ Requirements

Pick one path to run the suite:

A) IDE (IntelliJ IDEA)

JDK 17+

IntelliJ with TestNG plugin

(Optional) Google Chrome installed locally (if you run UI non-headless)

B) Maven (command line)

JDK 17

Maven 3.9+ on PATH (mvn -v)

C) Docker (all-in-one)

Docker Desktop (Windows/macOS) or Docker Engine (Linux)

On Windows: WSL2 + hardware virtualization enabled (BIOS)

▶️ How to Run
1) From IDE (IntelliJ)

Run the whole suite: right-click RegressionSuite.xml → Run.

Run a class: right-click the test class → Run.

Run a single test: right-click the @Test method → Run.

Default UI mode is non-headless; inside Docker, DriverManager auto-forces headless.

2) With Maven
   mvn clean test


Reports

test-output/index.html (TestNG)

test-output/emailable-report.html

target/surefire-reports/… (Surefire logs)

🐳 Run with Docker (by platform)
0) Build the image (run in repo root)

macOS (Apple Silicon: M1/M2/M3) – recommended for Chrome stable

docker build --platform=linux/amd64 -t paylocity-tests .


macOS (Intel), Linux, Windows

docker build -t paylocity-tests .


Alternative for Apple Silicon (native arm64): switch Dockerfile to install Chromium instead of Chrome.

1) Run and export reports

Windows (PowerShell)

mkdir .\reports -Force
docker run --rm -v "${PWD}\reports:/reports" paylocity-tests


macOS (Intel/Apple Silicon)

mkdir -p ./reports
docker run --rm -v "$PWD/reports:/reports" paylocity-tests


Linux

mkdir -p ./reports
docker run --rm -v "$PWD/reports:/reports" paylocity-tests


Open on host

reports/test-output/index.html

reports/test-output/emailable-report.html

Notes

If your ConfigManager already loads properties from the classpath, you don’t need -e env vars.

To override config at runtime, add -e NAME=value to docker run.

Windows: ensure drive C: is shared in Docker Desktop (Settings → Resources → File Sharing).

💻 Platform-specific Docker tips

macOS (Apple Silicon)

Best compatibility: build with --platform=linux/amd64 (Chrome stable).

Native arm64 alternative: use Chromium in the Dockerfile.

macOS (Intel)

Standard build/run commands; no special flags needed.

Linux

If rootless Docker, ensure your user can mount volumes to ./reports.

Only add GPU/video deps if you change the image to run browsers non-headless.

Windows

Docker Desktop requires WSL2 and virtualization enabled in BIOS.

Use PowerShell path style in -v (as shown above).

⚙️ TestNG Parameters (optional)

BaseTest reads optional parameters from the suite XML:

<!-- Example overrides (uncomment to apply) -->
<!--
<parameter name="browser" value="FIREFOX"/>
<parameter name="headless" value="true"/>
<parameter name="remote" value="true"/>
<parameter name="gridUrl" value="http://selenium:4444/wd/hub"/>
-->


Defaults: CHROME, headless=false, remote=false.

Docker: headless is forced automatically by DriverManager.

🧪 Reports

IDE / Maven: test-output/ in project root.

Docker: exported to host under ./reports/test-output/.

🔧 Generate/refresh the tree (exact, optional)

Print an up-to-date ASCII map (ignores build/IDE folders):

macOS / Linux

python3 - <<'PY'
import os, re
ignore = re.compile(r'(^\.git$|^target$|^\.idea$|^reports$|^test-output$)')
def tree(root, prefix=""):
entries = [e for e in sorted(os.listdir(root)) if not ignore.match(e)]
for i, name in enumerate(entries):
path = os.path.join(root, name)
tee = "└─ " if i == len(entries)-1 else "├─ "
print(prefix + tee + name + ("/" if os.path.isdir(path) else ""))
if os.path.isdir(path):
tree(path, prefix + ("   " if i == len(entries)-1 else "│  "))
print(os.path.basename(os.getcwd()) + "/")
tree(".")
PY


Windows (PowerShell)

python - <<'PY'
import os, re
ignore = re.compile(r'(^\.git$|^target$|^\.idea$|^reports$|^test-output$)')
def tree(root, prefix=""):
entries = [e for e in sorted(os.listdir(root)) if not ignore.match(e)]
for i, name in enumerate(entries):
path = os.path.join(root, name)
tee = "└─ " if i == len(entries)-1 else "├─ "
print(prefix + tee + name + ("/" if os.path.isdir(path) else ""))
if os.path.isdir(path):
tree(path, prefix + ("   " if i == len(entries)-1 else "│  "))
print(os.path.basename(os.getcwd()) + "/")
tree(".")
PY

❗Troubleshooting

Docker on Windows: “Virtualization support not detected” → enable VT-x/AMD-V in BIOS, enable WSL2, then restart Docker Desktop.

macOS Apple Silicon: if Chrome install fails during build, rebuild with --platform=linux/amd64 (recommended), or switch the Dockerfile to Chromium for native arm64.

Heavy UI tests: increase Docker Desktop resources (Settings → Resources → CPU/RAM).