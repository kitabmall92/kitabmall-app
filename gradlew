#!/bin/sh
# Gradle wrapper script

GRADLE_VERSION="8.4"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

# Download gradle if not present
if [ ! -f "$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin/*/gradle-${GRADLE_VERSION}/bin/gradle" ]; then
  mkdir -p "$HOME/.gradle/wrapper/dists"
  curl -L "$GRADLE_URL" -o /tmp/gradle.zip
  unzip -q /tmp/gradle.zip -d "$HOME/.gradle/wrapper/dists/"
fi

GRADLE_HOME=$(find "$HOME/.gradle/wrapper/dists" -name "gradle-${GRADLE_VERSION}" -type d | head -1)
exec "$GRADLE_HOME/bin/gradle" "$@"
