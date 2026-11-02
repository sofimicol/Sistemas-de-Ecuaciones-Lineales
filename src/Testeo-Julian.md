// Reunion 4, para Julian 
📋 TICKET DE DESARROLLO Y QA: MITIGACIÓN DE ERRORES SILENCIOSOS (NaN)
Proyecto: Calculadora de Sistemas de Ecuaciones (Módulo Core)
Módulo afectado: servicios.MetodosDirectosMatriz (Métodos de Gauss-Jordan y Factorización LU)
Prioridad: 🔴 Bloqueante (Crítico)
Rol del receptor: Desarrollador Backend / Lógica Matemática
Rol del emisor: QA Lead / Product Owner

1. Contexto y Descripción del Defecto (Bug Report)
Durante la fase de pruebas de integración (Test Case #1 - Sistema 2x2 con solución única), el pipeline de pruebas arrojó un falso positivo. El sistema indicó una ejecución exitosa, pero los valores del vector solución fueron incorrectos:
Output: Éxito. Solución: x= NaN, y= NaN

Análisis de Causa Raíz (Root Cause Analysis)
El método JordanSolucion... opera sobre una matriz estructuralmente válida (dimensiones 2x3), pero carece de validaciones de integridad matemática.
En Java, la división de un tipo primitivo double por 0.0 no arroja una excepción en tiempo de ejecución; en su lugar, retorna Infinity o NaN (Not a Number). Este valor atípico se propaga a través de las operaciones matriciales subsecuentes, corrompiendo el resultado final de forma silenciosa. Al no lanzarse una excepción, la capa de presentación (UI) recibe datos corruptos y los muestra como válidos, afectando la integridad del sistema y la experiencia del usuario final.

2. Objetivo de la Refactorización
Implementar el patrón de diseño Fail-Fast (Fallo Rápido) dentro de los métodos de cálculo. El objetivo es que el algoritmo detecte y aborte la ejecución ante datos inconsistentes o estados matemáticos indeterminados, lanzando excepciones explícitas que puedan ser capturadas y notificadas correctamente por la capa superior (Main/UI).

3. Criterios de Aceptación y Restricciones Técnicas
El desarrollador asignado deberá implementar los siguientes guards (guardas de seguridad) en los métodos correspondientes:

3.1. Validación de Contrato (Parámetro n)
Restricción: El parámetro n es un dato redundante que puede desincronizarse del tamaño real del objeto Matriz.

Criterio: Se debe validar que n coincida con las dimensiones físicas de la matriz antes de iniciar cualquier bucle iterativo, previniendo excepciones de tipo ArrayIndexOutOfBoundsException.

Implementación requerida:

java
if (n <= 0 || n != Ab.getCantFilas()) {
    throw new IllegalArgumentException("Inconsistencia de dimensiones: El parámetro n no coincide con el tamaño de la matriz.");
}
3.2. Prevención de Indeterminación Matemática (Pivoteo)
Restricción: Los métodos de eliminación requieren divisiones sucesivas por los elementos de la diagonal principal (pivotes).

Criterio: Se debe validar que el pivote no sea cero (o no se encuentre en un rango de épsilon de máquina para evitar errores de precisión). Si el pivote es nulo, el sistema es singular o requiere un pivoteo previo que no fue ejecutado. Se debe lanzar una excepción de tipo aritmético.

Implementación requerida:

java
double pivot = Ab.getElemento(k, k);
if (Math.abs(pivot) < 1e-12) {
    throw new ArithmeticException("Excepción Aritmética: Pivote nulo en fila " + k + ". El sistema es singular o requiere pivoteo manual.");
}
3.3. Validación de Dominio de Datos (NaN / Infinity)
Restricción: El algoritmo no debe procesar valores que escapen al conjunto de los números reales finitos.

Criterio: Antes de comenzar la eliminación, se debe barrer la matriz en busca de valores corruptos (NaN o Infinity) inyectados por fallos en el parseo de la capa de UI. Si se detectan, se debe rechazar la operación inmediatamente.

Implementación requerida:

java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n + 1; j++) {
        double valor = Ab.getElemento(i, j);
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("Violación de Dominio: La matriz contiene valores no finitos (NaN o Infinity).");
        }
    }
}
3.4. Retorno Seguro (Manejo de Excepciones de Estado)
Restricción: El método actual delega la resolución final a Ab.sistDiagonal(), el cual asume que la matriz es ampliada y diagonal.

Criterio: Validar las precondiciones del objeto antes de retornar, para evitar excepciones no controladas de tipo IllegalStateException si el llamador cometió un error de formato.

Implementación requerida:

java
if (!Ab.esAmpliada()) {
    throw new IllegalStateException("Estado Inválido: El método requiere una matriz en formato ampliado n x (n+1).");
}
return Ab.sistDiagonal();
4. Matriz de Pruebas (QA Test Suite) y Definición de Terminado (DoD)
La tarea se considerará Finalizada (Done) cuando los métodos refactorizados pasen la totalidad de los siguientes casos de prueba sin arrojar valores NaN en la consola:

ID	Escenario de Prueba	Input (Matriz Ampliada)	Comportamiento Esperado (Post-Fix)
T1	Flujo Principal (Happy Path)	[2,1,5; 1,-1,1]	Retorna [2.0, 1.0]. Cero excepciones.
T2	Pivote Nulo (Sin pivoteo previo)	[0,2,4; 1,1,3]	Aborta arrojando ArithmeticException.
T3	Sistema Singular (Dependencia lineal)	[1,2,3; 2,4,6]	Aborta arrojando ArithmeticException.
T4	Formato Inválido (No ampliada)	Matriz de 3x3	Aborta arrojando IllegalStateException.
T5	Corrupción de Datos	[1, NaN, 3; 4,5,6]	Aborta arrojando IllegalArgumentException.
T6	Inconsistencia de Contrato	Pasar n=3 para una matriz de 2x3	Aborta arrojando IllegalArgumentException.
T7	Pivoteo Exitoso	[0,2,4; 1,1,3] + Ab.pivoteoParcial(0)	Retorna solución válida. Cero excepciones.
5. Conclusión y Directivas para el Equipo de Desarrollo

Decision de equipo: El modelo matemático debe asumir que el main o la interfaz gráfica han validado absolutamente todo? Es necesaria la validacion del ingreso de datos en la matriz? La clase Matriz ya provee validaciones estructurales de memoria (índices y dimensiones). Su responsabilidad en MetodosDirectosMatriz es blindar la lógica algorítmica.

Si un pivote es cero, si falta un dato, o si se intenta resolver algo imposible, el método debe detenerse y lanzar la excepción correspondiente. La arquitectura de la UI ya está preparada para capturar estos errores mediante bloques try-catch y mostrar alertas amigables al usuario. Bajo ninguna circunstancia se debe permitir que un cálculo retorne NaN hacia las capas superiores.

1. contieneValoresValidos() (En la clase Matriz)
Qué hace: Recorre toda la matriz y verifica que no haya ningún NaN ni Infinity (que pudieron colarse por un error de la UI).

Dónde va: En modelo.Matriz.

Por qué: Evita que un dato corrupto se propague como virus por todo el cálculo.

2. esDiagonalmenteDominante() (En la clase Matriz)
Qué hace: Compara la magnitud del número en la diagonal con la suma de los demás de su fila. Devuelve true o false.

Dónde va: En modelo.Matriz.

Por qué: Para los métodos iterativos (Jacobi/Seidel) es ley. Y para los directos, sirve para advertirle al usuario de que el sistema es propenso a fallos de precisión.

3. validarAntesDeResolver(Matriz A) (En el Main o en Matriz)
Qué hace: Es el "guardia de seguridad". Llama a contieneValoresValidos(). Luego recorre la diagonal (A.getElemento(i,i)) y si encuentra un 0.0 (o un número menor a 1e-12), lanza una excepción ArithmeticException.

Dónde va: En la clase Main (o como método estático de validación).

Por qué: Frena la ejecución antes de que el algoritmo de Jordan intente dividir por cero.

4. pivoteoAutomático() (En la clase Matriz o en Main)
Qué hace: Reemplaza la necesidad de que el usuario apriete un botón de pivoteo. Recorre la diagonal; si encuentra un cero, llama a pivoteoParcial(k) para intercambiar filas. Si después de buscar en todas las filas de abajo sigue habiendo un cero, lanza ArithmeticException (el sistema es realmente singular).

Dónde va: En modelo.Matriz (es un método de conveniencia).

Por qué: Hace que el main sea mucho más limpio. El main solo llama a pivoteoAutomático() y luego a Jordan.

¿Cómo queda blindado el método con estas 4 piezas?
El main recibe los datos, llama a Matriz A = new Matriz(datos).

El main llama a A.pivoteoAutomático(). Si el pivote era cero, lo soluciona. Si es insoluble, explota acá.

El main llama a validarAntesDeResolver(A). Si hay un NaN colado, lo frena acá.

El main llama a MetodosDirectosMatriz.JordanSolucion(A, n).

Jordan, por su parte, tiene su propio if (pivote == 0) throw new ArithmeticException(...) como última red de seguridad (que pedimos en el ticket de QA).

🔴 TAREA CRÍTICA PARA JULIÁN: Escenario 4 (Mutación de la matriz)
El Problema:
Los métodos JordanSolucion y JordanSolucionDiagonalUnitaria modifican la matriz original (Ab) in-place (en el mismo lugar de memoria).
Si el main o un test llama al método dos veces con la misma matriz, la segunda llamada recibirá una matriz que ya fue transformada y está en forma diagonal, lo que romperá los cálculos y devolverá basura (como NaN o Infinity).

Ejemplo de prueba (Test 13):

java
Matriz original = new Matriz(new double[][]{{2, 1, 5}, {1, -1, 1}});

// Primera llamada: resuelve bien y muta la matriz
double[] x1 = MetodosDirectosMatriz.JordanSolucion(original, 2);

// Segunda llamada: la matriz 'original' ya no es la misma. ¡Explota o da mal!
double[] x2 = MetodosDirectosMatriz.JordanSolucion(original, 2); 
Tu Solución Requerida:
Como es un método de cálculo, no debería "ensuciar" el objeto que recibe. Debés clonar la matriz al inicio del método para operar sobre una copia segura.

Implementación sugerida: Agregar una función copiar() en la clase Matriz (o crear una nueva Matriz con los mismos datos) y hacer que el algoritmo trabaje sobre esa copia. Al final, devolvés el resultado sin haber tocado la matriz original del usuario.

🟠 CONSULTA PARA DEBATIR EN GRUPO: Escenario 3 (Pivoteo Total)
El Problema:
Implementaste correctamente el pivoteoParcial (intercambia filas). Sin embargo, si el pivote de toda la columna es 0 (todas las filas debajo tienen 0), el sistema es singular. Pero matemáticamente, a veces el sistema se puede resolver si intercambiamos columnas (Pivoteo Total).

Ejemplo: Si el pivote (0,0) es 0, y toda la columna 0 es 0, pero la columna 1 tiene números, se puede pivotear la columna.

¿Qué necesitamos de vos / del equipo?

¿Es necesario implementar Pivoteo Total? Dado que la UI solo acepta matrices ampliadas (n x n+1), el sistema siempre es cuadrado. En sistemas cuadrados, si el pivote parcial falla, el sistema suele ser realmente singular.

Recomendación de QA: Mantener solo el Pivoteo Parcial (que ya tenés) y lanzar ArithmeticException si falla. No agregar Pivoteo Total para no complicar el código. (A confirmar por Julián y el equipo).

🟠 CONSULTA PARA DEBATIR EN GRUPO: Escenario 2 (Precisión y Mal Condicionamiento)
El Problema:
Actualmente usás Math.abs(Ab.getElemento(k, k)) < 1e-12 para decidir si un pivote es cero. El problema es que hay matrices matemáticamente válidas (como la Matriz de Hilbert) que tienen pivotes extremadamente pequeños (0.000000000000001). Tu código las rechazaría como "singulares" (falso positivo) cuando en realidad sí tienen solución, solo que son mal condicionadas.

La Solución Propuesta (Para Julián):
En lugar de tomar decisiones a ciegas con el pivote, implementar un método calcularCondicion() o esMalCondicionada() en la clase Matriz.

¿Qué haría? Calcularía el Número de Condición de la matriz (usando normas).

¿Cómo se usa?

Si el Número de Condición es muy alto (ej. > 1000), el main le avisa al usuario: "Advertencia: La matriz está mal condicionada. Los resultados pueden tener errores de precisión."

Tu método de Jordan puede relajar el épsilon (o mantenerlo) sabiendo que el sistema le avisará al usuario de antemano.

Ejemplo de prueba para testear tu código:

java
// Matriz de Hilbert 2x2 (mal condicionada)
Matriz hilbert = new Matriz(new double[][]{
    {1.0, 1.0/2.0, 1.0},
    {1.0/3.0, 1.0/4.0, 1.0}
});
🔵 DUDA PARA EL EQUIPO: Escenario 1 (El parámetro n)
El Problema:
Los métodos reciben int n, pero la matriz ya sabe su tamaño (Ab.getCantFilas()). Si el programador del main comete un error de tipeo y pasa n=3 para una matriz 2x3, el programa explotará con IndexOutOfBoundsException.

¿Qué decidimos como equipo?

Opción A (QA recomienda): Eliminar el parámetro int n de la firma del método. Que el método use int n = Ab.getCantFilas(); internamente. Es infalible.

Opción B: Mantener n, pero agregar un if (n != Ab.getCantFilas()) throw new IllegalArgumentException(...) al inicio del método.

Como equipo, confiamos en que el programador del main no cometerá ese error (por eso lo dejamos como duda), pero si quieren blindarlo al 100%, elijan la Opción A.

Resumen de Acciones
[Julián] Clonar la matriz al inicio de JordanSolucion y JordanSolucionDiagonalUnitaria para no mutar la original.

[Julián] Evaluar si implementar calcularCondicion() en Matriz o dejar que el main lo haga.

[Equipo] Decidir si quitamos el parámetro n de los métodos (Opción A).

[Equipo] Confirmar que NO implementaremos Pivoteo Total por ser un caso extremo.

