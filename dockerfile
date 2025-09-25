FROM maven:3.9.9-eclipse-temurin-17

USER root
RUN apt-get update && apt-get install -y \
    wget gnupg apt-transport-https ca-certificates curl unzip jq fonts-dejavu \
 && wget -qO - https://dl.google.com/linux/linux_signing_key.pub \
    | gpg --dearmor -o /usr/share/keyrings/google-linux.gpg \
 && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update && apt-get install -y google-chrome-stable \
 && apt-get clean && rm -rf /var/lib/apt/lists/*

ENV RUNNING_IN_DOCKER=true \
    WDM_CACHE=/wdm \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY . .

CMD bash -lc '\
  mvn -q clean test && \
  mkdir -p /reports && \
  cp -r test-output /reports/test-output || true && \
  cp -r target/surefire-reports /reports/surefire-reports || true && \
  echo "Reports on /reports" \
'
