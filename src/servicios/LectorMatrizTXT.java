package servicios;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

// Carga de la matriz ampliada [A | b] desde un TXT, con criterio fail-fast:
// ante cualquier dato inválido lanza IllegalArgumentException y no devuelve una matriz parcial
public class LectorMatrizTXT {

    // Número decimal con signo opcional y exponente opcional. Excluye NaN, Infinity, hexadecimales y sufijos (1d, 1f)
    private static final Pattern NUMERO = Pattern.compile("[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?");

    // Lee el archivo en UTF-8 y lo interpreta con leerAmpliada(String, int).
    // Lanza IOException si el archivo no se puede leer
    public static double[][] leerAmpliada(Path archivo, int n) throws IOException {
        return leerAmpliada(Files.readString(archivo, StandardCharsets.UTF_8), n);
    }

    // Interpreta el contenido como los n * (n+1) valores de [A | b], de izquierda a derecha y de arriba a abajo.
    // Los valores se separan con espacios, tabs o saltos de línea; cómo se repartan en las líneas no importa.
    // Acepta coma o punto decimal y notación científica, sin depender del locale del sistema.
    // Retorna una matriz nueva de n x (n+1)
    public static double[][] leerAmpliada(String contenido, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Error: La dimensión n debe ser mayor a 0 (recibido n = " + n + ").");
        }
        if (contenido == null) {
            throw new IllegalArgumentException("Error: El contenido del archivo es nulo.");
        }
        if (contenido.startsWith("﻿")) {
            contenido = contenido.substring(1); // BOM de UTF-8 que agregan algunos editores
        }

        int columnas = n + 1;
        int esperados = n * columnas;
        double[][] ampliada = new double[n][columnas];
        int leidos = 0;

        String[] lineas = contenido.split("\\R", -1);
        for (int l = 0; l < lineas.length; l++) {
            for (String token : lineas[l].trim().split("\\s+")) {
                if (token.isEmpty()) {
                    continue; // línea vacía
                }
                double valor = convertir(token, l + 1);
                if (leidos < esperados) {
                    ampliada[leidos / columnas][leidos % columnas] = valor;
                }
                leidos++;
            }
        }

        if (leidos != esperados) {
            throw new IllegalArgumentException("Error: Para un sistema de " + n + "x" + n + " se esperaban " + esperados
                + " valores (" + n + " filas de " + columnas + ") y el archivo tiene " + leidos + ".");
        }
        return ampliada;
    }

    private static double convertir(String token, int linea) {
        String normalizado = token.replace(',', '.');
        if (!NUMERO.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("Error: Valor no numérico '" + token + "' en la línea " + linea + ".");
        }
        double valor = Double.parseDouble(normalizado);
        if (Double.isInfinite(valor)) {
            throw new IllegalArgumentException("Error: El valor '" + token + "' en la línea " + linea + " excede el rango de double.");
        }
        return valor;
    }
}
