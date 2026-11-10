# Sistemas-de-Ecuaciones-Lineales

## Ejecutar la aplicación

La interfaz usa JavaFX, que no viene incluido en el JDK y se descarga aparte. Sin Maven ni Gradle: alcanzan tres jars.

**1. Elegir la variante de JavaFX según la arquitectura del JDK** (no la de la máquina: un JDK para Intel corre emulado en una Mac con Apple Silicon y necesita la variante de Intel). Para ver la del JDK:

```bash
java -XshowSettings:properties -version 2>&1 | grep os.arch
```

| Sistema | `os.arch` del JDK | Variante |
|---|---|---|
| Windows | `amd64` | `win` |
| Linux | `amd64` | `linux` |
| macOS con JDK para Intel | `x86_64` | `mac` |
| macOS con JDK para Apple Silicon | `aarch64` | `mac-aarch64` |

**2. Descargar JavaFX 21** (una sola vez; reemplazar `VARIANTE` por la de la tabla). En macOS o Linux:

```bash
mkdir -p ~/javafx && cd ~/javafx
for m in base graphics controls; do
  curl -fLO https://repo1.maven.org/maven2/org/openjfx/javafx-$m/21.0.5/javafx-$m-21.0.5-VARIANTE.jar
done
```

En Windows se pueden descargar los mismos tres archivos desde el navegador y guardarlos en una carpeta, por ejemplo `C:\javafx`.

**3. Compilar y ejecutar** (en la raíz del repo):

```bash
javac -encoding UTF-8 --module-path ~/javafx --add-modules javafx.controls -d out/app $(find src -name '*.java')
java --enable-native-access=javafx.graphics --module-path ~/javafx --add-modules javafx.controls -cp out/app vista.AppGUI
```

`--enable-native-access` evita advertencias de los JDK recientes al cargar las librerías nativas de JavaFX. Si al ejecutar aparece `UnsatisfiedLinkError ... incompatible architecture`, la variante de JavaFX no coincide con la del JDK: volver al paso 1.

## Tests

Los tests están en `test/` y usan JUnit 5 (`lib/junit-platform-console-standalone-1.13.4.jar`, sin Maven ni Gradle).

**Desde VS Code:** con el Extension Pack for Java instalado, abrir el panel *Testing* y ejecutar. La configuración está en `.vscode/settings.json`.

**Desde la terminal** (en la raíz del repo):

```bash
javac -encoding UTF-8 -d out/test -cp lib/junit-platform-console-standalone-1.13.4.jar \
  $(find src -name '*.java' -not -path 'src/vista/*') $(find test -name '*.java')
java -jar lib/junit-platform-console-standalone-1.13.4.jar execute -cp out/test --scan-classpath
```

La interfaz (`src/vista`) queda fuera de los tests porque necesita JavaFX.
