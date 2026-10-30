@rem Configuración del Gradle Wrapper para sistemas de comandos Windows
@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Script de inicio de Gradle para Windows
@rem
@rem ##########################################################################

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Agregar rutas por defecto para encontrar Java si no está en el entorno
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
@rem Configurar argumentos y llamar al ejecutable interno de Gradle
"%JAVACMD%" "-Xmx64m" "-Xms64m" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:fail
exit /b 1
