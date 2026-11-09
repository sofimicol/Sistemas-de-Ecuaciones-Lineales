# Sistemas-de-Ecuaciones-Lineales

## Tests

Los tests están en `test/` y usan JUnit 5 (`lib/junit-platform-console-standalone-1.13.4.jar`, sin Maven ni Gradle).

**Desde VS Code:** con el Extension Pack for Java instalado, abrir el panel *Testing* y ejecutar. La configuración está en `.vscode/settings.json`.

**Desde la terminal** (en la raíz del repo):

```bash
javac -encoding UTF-8 -d out/test -cp lib/junit-platform-console-standalone-1.13.4.jar \
  $(find src -name '*.java' -not -path 'src/vista/*' -not -name nuevo.java) $(find test -name '*.java')
java -jar lib/junit-platform-console-standalone-1.13.4.jar execute -cp out/test --scan-classpath
```

La interfaz (`src/vista`) queda fuera porque necesita JavaFX y no tiene tests.
