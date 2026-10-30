@if "%DEBUG%" == "" @echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

set JAVACMD=java
if not "%JAVA_HOME%" == "" (
    set JAVACMD=%JAVA_HOME%\bin\java.exe
)

if not exist "%JAVACMD%" (
    echo.
    echo ERROR: JAVA_HOME esta configurado incorrectamente o no se detecta Java.
    echo Por favor asegúrate de tener instalado JDK 11 o superior.
    goto fail
)

:execute
"%JAVACMD%" "-Xmx64m" "-Xms64m" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:fail
exit /b 1
