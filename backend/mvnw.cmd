@REM ----------------------------------------------------------------------------
@REM Maven Wrapper 启动脚本 — 自动下载并使用指定版本的 Maven
@REM ----------------------------------------------------------------------------
@REM 如果项目中已有 .mvn/wrapper/maven-wrapper.properties 和 maven-wrapper.jar，
@REM 此脚本会自动使用它们。否则尝试从系统 PATH 中查找 mvn。
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties

if exist "%WRAPPER_JAR%" (
  java -jar "%WRAPPER_JAR%" %*
  exit /b %ERRORLEVEL%
)

REM 如果系统中有 Maven，直接使用
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)         

echo Maven 未安装。请安装 Maven 3.6+ 或参考 https://maven.apache.org/install.html
exit /b 1

endlocal
