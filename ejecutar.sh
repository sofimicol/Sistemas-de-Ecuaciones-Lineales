#!/bin/sh
# Descarga JavaFX (solo la primera vez), compila y ejecuta la interfaz. Uso: ./ejecutar.sh
set -e
cd "$(dirname "$0")"
VERSION=21.0.5
FX=lib/javafx

command -v java >/dev/null || { echo "No se encontró java en el PATH. Instalar un JDK 17 o superior."; exit 1; }

# La variante depende de la arquitectura del JDK, no de la máquina (un JDK para Intel corre emulado en Apple Silicon)
ARCH=$(java -XshowSettings:properties -version 2>&1 | awk '/os.arch/ {print $3}')
case "$(uname -s)-$ARCH" in
    Darwin-aarch64) VARIANTE=mac-aarch64 ;;
    Darwin-*)       VARIANTE=mac ;;
    Linux-aarch64)  VARIANTE=linux-aarch64 ;;
    Linux-*)        VARIANTE=linux ;;
    MINGW*|MSYS*|CYGWIN*) VARIANTE=win ;;
    *) echo "Sistema no soportado: $(uname -s) $ARCH"; exit 1 ;;
esac

if [ ! -f "$FX/javafx-controls-$VERSION-$VARIANTE.jar" ]; then
    echo "Descargando JavaFX $VERSION ($VARIANTE)..."
    rm -rf "$FX"
    mkdir -p "$FX"
    for m in base graphics controls; do
        curl -fL -o "$FX/javafx-$m-$VERSION-$VARIANTE.jar" \
            "https://repo1.maven.org/maven2/org/openjfx/javafx-$m/$VERSION/javafx-$m-$VERSION-$VARIANTE.jar"
    done
fi

javac -encoding UTF-8 --module-path "$FX" --add-modules javafx.controls -sourcepath src -d out/app src/vista/AppGUI.java
java --enable-native-access=javafx.graphics --module-path "$FX" --add-modules javafx.controls -cp out/app vista.AppGUI
