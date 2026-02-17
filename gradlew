#!/usr/bin/env sh

# Simulación básica del wrapper de Gradle para que el entorno de CI sepa qué hacer
# En un proyecto real, este archivo se genera con 'gradle wrapper'

if [ -z "$JAVA_HOME" ]; then
  JAVA_EXE="java"
else
  JAVA_EXE="$JAVA_HOME/bin/java"
fi

echo "Iniciando compilación en la nube..."
# Este script es solo un marcador. En GitHub Actions usaremos la instalación de Gradle del sistema.
gradle assembleDebug
