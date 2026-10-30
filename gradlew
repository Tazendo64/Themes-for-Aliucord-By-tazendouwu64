#!/bin/sh

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done

SAVED="`pwd`"
CDPATH=""
BUILD_DIR=`dirname "$PRG"`
cd "$BUILD_DIR" >/dev/null
APP_HOME="`pwd`"
cd "$SAVED" >/dev/null

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    else
        eval "echo 'ERROR: JAVA_HOME está apuntando a una ruta inválida.'"
        exit 1
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || {
        eval "echo 'ERROR: No se encontró Java en tu entorno del sistema.'"
        exit 1
    }
fi

exec "$JAVACMD" "-Xmx64m" "-Xms64m" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
