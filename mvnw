#!/bin/sh
# Maven Wrapper bootstrap script
# Downloads and runs the Maven Wrapper if not already present

set -e

MAVEN_VERSION="3.9.9"
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/${MAVEN_VERSION}/maven-wrapper-${MAVEN_VERSION}.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    mkdir -p .mvn/wrapper
    if command -v curl > /dev/null 2>&1; then
        curl -sL -o "$WRAPPER_JAR" "$WRAPPER_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
    else
        echo "Error: curl or wget is required to download Maven Wrapper"
        exit 1
    fi
fi

exec java -jar "$WRAPPER_JAR" "$@"
