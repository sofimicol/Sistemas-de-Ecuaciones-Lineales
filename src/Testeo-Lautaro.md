📋 TICKET DE QA: Blindaje del Método pivoteoSimple
Para: Lautaro (Desarrollador de Pivoteos - Clase Matriz)
De: Equipo de QA y Arquitectura
Módulo afectado: modelo.Matriz -> pivoteoSimple(int k)
Prioridad: 🟠 Media (Fallo lógico silencioso)
Estado: ⚠️ Requiere acción.

1. Contexto de Arquitectura (Nuestras Suposiciones)
Lautaro, antes de detallar el problema, es importante que tengas en cuenta las decisiones de diseño que tomamos como equipo para este proyecto:

El usuario NO interactúa con la terminal ni con el código: Solo usa la interfaz gráfica (UI).

La UI valida las dimensiones: El usuario no puede ingresar matrices que no sean ampliadas, ni índices fuera de rango, ni valores inválidos. Las dimensiones siempre serán válidas.

Los bucles internos son seguros: Los algoritmos de Gauss-Jordan y Factorización LU controlan sus propios índices (k va de 0 a n-1). Por lo tanto, no es necesario que agregues validaciones de índices (como if (k < 0)) en pivoteoParcial ni en pivoteoTotal. El código actual de esos métodos está aprobado.

El pivoteo es automático: El usuario no aprieta un botón para pivotear. El algoritmo lo hace solo cuando detecta que un pivote es cero.

2. El Problema Detectado: pivoteoSimple y el Falso Éxito
Si bien pivoteoParcial y pivoteoTotal están bien, encontramos un fallo lógico en el método pivoteoSimple(int k).

El Escenario del Error (Dónde salta):
Imaginate que el algoritmo de Gauss-Jordan está resolviendo un sistema y llega a una columna donde el pivote (datos[k][k]) es 0. El algoritmo llama a pivoteoSimple(k).

El código actual de pivoteoSimple hace lo siguiente:

java
public void pivoteoSimple(int k) {
    if (datos[k][k] == 0.0) {
        for (int i = k + 1; i < cantFilas; i++) {
            if (datos[i][k] != 0.0) {
                interFilas(k, i);
                break; 
            }
        }
    }
}
¿Qué pasa si TODAS las filas de abajo también tienen cero?

El bucle for termina sin encontrar nada.

El método pivoteoSimple termina su ejecución sin hacer nada y sin lanzar ningún error.

La consecuencia: El algoritmo de Gauss-Jordan recibe la matriz de vuelta con el pivote en 0. Al intentar dividir por cero, el programa explota (o devuelve NaN).

¿Por qué esto es un problema?
Porque el sistema es singular (no tiene solución única), pero el pivoteoSimple se lava las manos y le deja el problema al método que lo llamó. Esto genera un error confuso y difícil de rastrear.

3. La Solución Sugerida (Restricción a Agregar)
Para que el sistema sea 100% robusto y no haya sorpresas, te pedimos que modifiques el método pivoteoSimple para que avise si no pudo pivotear.

Tenés dos formas de hacerlo (elegí la que más te guste):

Opción A: Lanzar una Excepción (Recomendada)
Si no encuentra una fila para intercambiar, lanza una ArithmeticException. Esto le avisa inmediatamente a la UI que el sistema es singular.

java
public void pivoteoSimple(int k) {
    if (datos[k][k] != 0.0) {
        return; // El pivote ya es válido, no hace falta pivotear
    }

    for (int i = k + 1; i < cantFilas; i++) {
        if (datos[i][k] != 0.0) {
            interFilas(k, i);
            return; // Pivoteo exitoso
        }
    }
    
    // Si sale del for, significa que todas las filas de abajo son cero.
    throw new ArithmeticException("Pivoteo simple fallido: La columna " + k + " es nula. El sistema es singular.");
}
Opción B: Retornar un Booleano
Si preferís no lanzar excepción, cambiale el retorno a boolean (true si pudo pivotear o no hacía falta, false si falló). Pero ojo: si hacés esto, el algoritmo de Jordan que lo llama debe revisar el resultado y lanzar la excepción por su cuenta.

java
public boolean pivoteoSimple(int k) {
    if (datos[k][k] != 0.0) return true;

    for (int i = k + 1; i < cantFilas; i++) {
        if (datos[i][k] != 0.0) {
            interFilas(k, i);
            return true; 
        }
    }
    return false; // No se pudo pivotear
}
(Nota: Recomendamos la Opción A para mantener la consistencia con el resto de los métodos de Matriz, que lanzan excepciones).

4. Caso de Prueba (Test de Regresión)
Para que des el ticket por terminado, este test debe pasar (lanzando la excepción en lugar de terminar silenciosamente).

java
// TEST: Pivoteo Simple en Matriz Ampliada Singular
System.out.println("\n--- TEST: Pivoteo Simple con Matriz Singular ---");
try {
    // Matriz ampliada donde la columna 0 es toda cero
    Matriz ampSingular = new Matriz(new double[][]{
        {0, 1, 2, 3},
        {0, 4, 5, 6},
        {0, 7, 8, 9}
    });
    
    ampSingular.pivoteoSimple(0); // Debería lanzar excepción
    System.out.println("❌ ERROR: El pivoteoSimple no detectó la singularidad.");
} catch (ArithmeticException e) {
    System.out.println("✅ ÉXITO ESPERADO: " + e.getMessage());
}
Salida esperada:

text
--- TEST: Pivoteo Simple con Matriz Singular ---
✅ ÉXITO ESPERADO: Pivoteo simple fallido: La columna 0 es nula. El sistema es singular.
5. Lo que NO tenés que hacer
❌ No agregues validaciones de índices (if (k < 0)) en ningún método de pivoteo. Los bucles internos garantizan que k siempre sea válido.

❌ No modifiques pivoteoTotal. Ya está perfecto: respeta la columna b en ampliadas y busca bien el mayor.

❌ No modifiques pivoteoParcial. Su lógica es correcta y no puede fallar en este contexto.