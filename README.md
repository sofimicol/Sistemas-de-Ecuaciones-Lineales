# Sistemas-de-Ecuaciones-Lineales

## Ejecutar la aplicación

Requiere un JDK 17 o superior (`java -version`). La interfaz usa JavaFX, que no viene en el JDK: los scripts lo descargan solos la primera vez en `lib/javafx` (ignorado por git), compilan y ejecutan.

| Sistema | Cómo ejecutar |
|---|---|
| Windows | doble clic en `ejecutar.cmd`, o `.\ejecutar.cmd` en una terminal abierta en la raíz del repo |
| macOS / Linux | `./ejecutar.sh` |

**Desde VS Code:** después de correr el script una vez (para que exista `lib/javafx`), elegir la configuración *AppGUI* en *Run and Debug* (F5). Está en `.vscode/launch.json`.

**A mano** (los mismos comandos sirven en PowerShell, cmd, bash y zsh, con `lib/javafx` ya descargado):

```bash
javac -encoding UTF-8 --module-path lib/javafx --add-modules javafx.controls -sourcepath src -d out/app src/vista/AppGUI.java
java --enable-native-access=javafx.graphics --module-path lib/javafx --add-modules javafx.controls -cp out/app vista.AppGUI
```

La variante de JavaFX depende de la arquitectura del JDK, no de la máquina: un JDK para Intel corre emulado en una Mac con Apple Silicon y necesita la variante de Intel. `ejecutar.sh` lo detecta solo. Si aparece `UnsatisfiedLinkError ... incompatible architecture`, borrar `lib/javafx` y volver a correr el script.

## Tests

Los tests están en `test/` y usan JUnit 5 (`lib/junit-platform-console-standalone-1.13.4.jar`, sin Maven ni Gradle).

**Desde VS Code:** con el Extension Pack for Java instalado, abrir el panel *Testing* y ejecutar. La configuración está en `.vscode/settings.json`.

**Desde la terminal** (en la raíz del repo; en Windows, desde Git Bash, porque usa `find` de Unix):

```bash
javac -encoding UTF-8 -d out/test -cp lib/junit-platform-console-standalone-1.13.4.jar \
  $(find src -name '*.java' -not -path 'src/vista/*') $(find test -name '*.java')
java -jar lib/junit-platform-console-standalone-1.13.4.jar execute -cp out/test --scan-classpath
```

La interfaz (`src/vista`) queda fuera de los tests porque necesita JavaFX.
