package servicios;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import modelo.Matriz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LectorMatrizTXTTest {

    private static final double[][] AMPLIADA_2X3 = {
        {4, 1, 5},
        {1, 3, 6}
    };

    @Nested
    @DisplayName("Formatos válidos")
    class FormatosValidos {

        @Test
        @DisplayName("Archivo correcto")
        void archivoCorrecto() {
            assertArrayEquals(AMPLIADA_2X3, LectorMatrizTXT.leerAmpliada("4 1 5\n1 3 6\n", 2));
        }

        @Test
        @DisplayName("Valores en una sola línea, tabs y CRLF")
        void separadoresVariados() {
            assertArrayEquals(AMPLIADA_2X3, LectorMatrizTXT.leerAmpliada("4\t1  5 1\r\n3 6", 2));
        }

        @Test
        @DisplayName("Coma decimal, punto, notación científica y signos")
        void formatosNumericos() {
            assertArrayEquals(new double[][]{{1.5, -3.25, 0.001}, {2, 0.5, 200}},
                LectorMatrizTXT.leerAmpliada("1,5 -3.25 1e-3\n+2 .5 2E2", 2));
        }

        @Test
        @DisplayName("Archivo con BOM de UTF-8")
        void conBOM() {
            assertArrayEquals(AMPLIADA_2X3, LectorMatrizTXT.leerAmpliada("﻿4 1 5\n1 3 6", 2));
        }

        // Con Scanner y locale es-AR, "1.5" se descartaba y "1.000" se leía como 1000
        @Test
        @DisplayName("No depende del locale del sistema (es-AR)")
        void independienteDelLocale() {
            Locale original = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("es-AR"));
                double[][] leida = LectorMatrizTXT.leerAmpliada("1.5 1.000 2\n3 4 5", 2);
                assertEquals(1.5, leida[0][0]);
                assertEquals(1.0, leida[0][1]);
            } finally {
                Locale.setDefault(original);
            }
        }

        @Test
        @DisplayName("Matriz grande de 100 x 101")
        void matrizGrande() {
            int n = 100;
            double[][] esperada = new double[n][n + 1];
            StringBuilder contenido = new StringBuilder();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n + 1; j++) {
                    esperada[i][j] = (i == j) ? 1000 + i : (i - j) / 4.0; // incluye negativos y decimales
                    contenido.append(esperada[i][j]).append(j < n ? " " : "\n");
                }
            }
            double[][] leida = LectorMatrizTXT.leerAmpliada(contenido.toString(), n);
            assertArrayEquals(esperada, leida);
            assertTrue(new Matriz(leida).esAmpliada());
        }

        @Test
        @DisplayName("Lectura desde un archivo")
        void desdeArchivo(@TempDir Path carpeta) throws IOException {
            Path archivo = carpeta.resolve("matriz.txt");
            Files.writeString(archivo, "4 1 5\n1 3 6\n");
            assertArrayEquals(AMPLIADA_2X3, LectorMatrizTXT.leerAmpliada(archivo, 2));
        }
    }

    @Nested
    @DisplayName("Errores (fail-fast)")
    class Errores {

        // El mensaje debe decir qué y dónde corregir
        @ParameterizedTest(name = "{0}")
        @CsvSource(delimiter = '|', value = {
            "Valor no numérico         | '4 1 x=5\\n1 3 6'        | 'x=5'     | línea 1",
            "Faltan valores            | '4 1 5\\n1 3'            | 6         | 5",
            "Sobran valores            | '4 1 5\\n1 3 6\\n7 8 9'   | 6         | 9",
            "Archivo vacío             | ''                      | 6         | 0",
            "NaN                       | '4 1 5\\n1 NaN 6'        | 'NaN'     | línea 2",
            "Infinity                  | '4 1 5\\n1 3 Infinity'   | 'Infinity'| línea 2",
            "Fuera de rango de double  | '4 1 5\\n1 3 1e400'      | '1e400'   | línea 2",
            "Coma como separador       | '4,1,5\\n1,3,6'          | '4,1,5'   | línea 1",
            "Sufijo de Java (6d)       | '4 1 5\\n1 3 6d'         | '6d'      | línea 2",
        })
        @DisplayName("Contenido inválido lanza IllegalArgumentException con un mensaje informativo")
        void contenidoInvalido(String caso, String contenido, String fragmento1, String fragmento2) {
            String texto = contenido.replace("\\n", "\n");
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> LectorMatrizTXT.leerAmpliada(texto, 2));
            assertTrue(e.getMessage().startsWith("Error:"), e.getMessage());
            assertTrue(e.getMessage().contains(fragmento1) && e.getMessage().contains(fragmento2), e.getMessage());
        }

        @Test
        @DisplayName("n inválido")
        void nInvalido() {
            assertThrows(IllegalArgumentException.class, () -> LectorMatrizTXT.leerAmpliada("4 1 5\n1 3 6", 0));
        }

        @Test
        @DisplayName("Contenido null")
        void contenidoNull() {
            assertThrows(IllegalArgumentException.class, () -> LectorMatrizTXT.leerAmpliada((String) null, 2));
        }

        @Test
        @DisplayName("Archivo inexistente lanza IOException")
        void archivoInexistente(@TempDir Path carpeta) {
            assertThrows(IOException.class, () -> LectorMatrizTXT.leerAmpliada(carpeta.resolve("no-existe.txt"), 2));
        }
    }
}
