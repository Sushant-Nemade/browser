@REM Maven Wrapper batch script for Windows
@REM Downloads and runs the Maven Wrapper if not already present

@echo off
setlocal

set MAVEN_VERSION=3.9.9
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/%MAVEN_VERSION%/maven-wrapper-%MAVEN_VERSION%.jar

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    if not exist ".mvn\wrapper" mkdir .mvn\wrapper
    powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)

java -jar "%WRAPPER_JAR%" %*

endlocal
