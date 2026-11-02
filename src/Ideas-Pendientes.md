//Ideas, pendientes en la interfaz 
# 📋 Documento de Diseño: Ingreso de Matrices y Validación de Datos
**Proyecto:** Calculadora de Sistemas de Ecuaciones
**Módulo:** Interfaz de Usuario (UI) y Controlador Principal (`main`)
**Rol del emisor:** QA Lead / Arquitecto de Software

---

## 1. El Problema a Resolver

En el primer ciclo de testeo (Test 1), el sistema arrojó un falso positivo: resolvió el cálculo y mostró `x = NaN, y = NaN`. 

La causa raíz no estaba en el algoritmo matemático, sino en el **ingreso de datos**. La interfaz permitía que datos inválidos (vacíos, texto, símbolos o valores infinitos) llegaran al modelo, y el `main` los procesaba sin chistar.

Para mitigar esto de raíz y evitar que se repita con futuros métodos (Jacobi, Gauss-Seidel, etc.), necesitamos diseñar un **pipeline de ingreso y validación** estricto.

---

## 2. La Propuesta: Pipeline de 3 Etapas

Para que el ingreso sea sencillo y portable, el flujo del dato debe pasar por 3 filtros secuenciales antes de tocar los algoritmos de cálculo:

`[UI: JTextField]` ➡️ *(Filtro 1)* ➡️ `[Main: String[][] -> double[][]]` ➡️ *(Filtro 2)* ➡️ `[Matriz: Objeto Seguro]` ➡️ *(Filtro 3)* ➡️ `[Algoritmo: Cálculo Seguro]`

### Etapa 1: La Interfaz Gráfica (Captura)
*   **¿Qué hace?** Provee campos de texto (`JTextField`) en una grilla. No convierte a números, solo captura texto.
*   **Librerías necesarias:** `javax.swing.*` (JFrame, JPanel, JTextField, JButton, JOptionPane).
*   **Validaciones en UI:** La UI **no deja** apretar el botón "Calcular" si hay celdas vacías. Pinta de rojo la celda con error (como en la imagen de referencia), pero **conserva los datos ya ingresados**.

### Etapa 2: El `main` (Parseo y Conversión)
*   **¿Qué hace?** Recibe el `String[][]` de la UI. Recorre cada celda, intenta convertir el texto a `double` (`Double.parseDouble`). Si falla, atrapa la excepción y le dice a la UI qué celda está mal. Si tiene éxito, crea el `double[][]`.
*   **Funciones pendientes:**
    *   `parsearMatrizDesdeUI(String[][] textosUI)`: Convierte y ataja `NumberFormatException`.
    *   `validarAntesDeResolver(Matriz A)`: Llama a los métodos de validación profunda de `Matriz`.

### Etapa 3: La clase `Matriz` (Validación Matemática)
*   **¿Qué hace?** Garantiza que el objeto sea seguro para calcular.
*   **Funciones pendientes a implementar en `Matriz`:**
    *   `contieneValoresValidos()`: Revisa que no haya `NaN` ni `Infinity`.
    *   `esDiagonalmenteDominante()`: Advierte sobre convergencia en métodos iterativos.

---

## 3. ¿Por qué esta es la forma más sencilla y portable?

1. **Separación de responsabilidades (SRP):** La UI no se ensucia con lógica matemática, y el modelo no se ensucia con lógica de interfaz.
2. **Portabilidad:** Este pipeline funciona igual en Swing (escritorio), en JavaFX, en Consola (`Scanner`) o en un entorno Web (Spring Boot). Lo único que cambia es la Etapa 1. Las Etapas 2 y 3 son idénticas.
3. **Cero librerías externas:** Solo usa el Java Development Kit (JDK) estándar. Nada de Maven, Gradle, Jackson ni dependencias raras.
4. **Mitigación del `NaN`:** Al prohibir explícitamente `NaN` en el parseo (Etapa 2) y en el modelo (Etapa 3), es **matemáticamente imposible** que un `NaN` inyectado por el usuario llegue a `MetodosDirectosMatriz`.

---

## 4. Errores Mitigados con esta Arquitectura

Con esta propuesta, los siguientes errores detectados en QA quedarán bloqueados:

| Error Detectado | Capa que lo ataja | Cómo lo ataja |
| :--- | :--- | :--- |
| El usuario escribe "hola" | Etapa 2 (`Main`) | `NumberFormatException` atrapada y mostrada en pantalla. |
| El usuario deja un campo vacío | Etapa 1 (UI) / Etapa 2 | Bloqueo del botón Calcular o `IllegalArgumentException`. |
| El usuario ingresa `Infinity` | Etapa 3 (`Matriz`) | `contieneValoresValidos()` lo detecta y aborta. |
| El sistema da `NaN` por pivote 0 | Etapa 3 (`Main`/`Matriz`) | `validarAntesDeResolver()` lanza `ArithmeticException`. |
| El usuario pide una matriz 10x10 en un sistema 3x3 | Etapa 2 (`Main`) | El constructor de `Matriz` exige que las dimensiones coincidan con el sistema. |

---

## 5. Funciones Pendientes para el `main` (Checklist del Programador)

El programador encargado del controlador (`main` / Conexión UI-Modelo) debe implementar:

- [ ] **`String[][] obtenerDatosDeUI()`**: Lee el contenido de los `JTextField`.
- [ ] **`double[][] parsearMatrizDesdeUI(String[][] datos)`**: Convierte texto a números, atajando letras y vacíos.
- [ ] **`void validarAntesDeResolver(Matriz A)`**: Implementa las 3 validaciones (valores finitos, pivotes no nulos, advertencia de dominancia).
- [ ] **Manejo de Excepciones Global (`try-catch`)**: Atrapar cualquier `IllegalArgumentException` o `ArithmeticException` lanzada por el modelo y mostrar un `JOptionPane.showMessageDialog` (cartel rojo).
- [ ] **Preservación de Estado**: Asegurarse de que la grilla de la UI **no se limpie** cuando se lanza un error.

---

## 6. Conclusión

> *"Al centralizar la validación de datos en tres capas bien definidas, logramos que los algoritmos matemáticos (LU, Gauss-Jordan, futuros métodos iterativos) reciban **únicamente matrices matemáticamente válidas**. De esta manera, si el usuario comete un error, el sistema se lo informa de inmediato y de forma amigable, sin perder su trabajo y sin generar resultados basura (`NaN`). Es la solución más limpia, escalable y libre de dependencias para nuestro proyecto."*