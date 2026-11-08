package vista;

import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import modelo.Matriz;
import servicios.MetodosDirectosMatriz;
import servicios.MetodosIterativosMatriz;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppGUI extends Application {

    // ===== Tipografía moderna (UI + numérica) =====
    private static final String FUENTE_UI   = "Segoe UI";
    private static final String FUENTE_MONO = "Consolas";

    // ===== Colores basados en Paleta =====
    private final String ESTILO_L      = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;", Paleta.CELDA_L_BG, Paleta.CELDA_L_BORDE, Paleta.CELDA_L_TEXTO);
    private final String ESTILO_U      = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;", Paleta.CELDA_U_BG, Paleta.CELDA_U_BORDE, Paleta.CELDA_U_TEXTO);
    
    // Ajuste: Texto amarillo brillante para que resalte en el pivote
    private final String ESTILO_PIVOTE = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: #FDE047; -fx-font-weight: bold;", Paleta.PIVOTE_BG, Paleta.PIVOTE_BORDE);
    
    private final String ESTILO_NORMAL = "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private final String ESTILO_VECTOR_B = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #06B6D4; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private final String ESTILO_CELDA_VACIA_RES = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s;", Paleta.CELDA_VACIA_BG, Paleta.BORDE_SUTIL, Paleta.CELDA_VACIA_TEXTO);

    // Inputs dinámicos
    private static final String IN_VACIO     = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s;", Paleta.CELDA_NORMAL_BG, Paleta.CELDA_NORMAL_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_VACIO_B   = String.format("-fx-background-color: %s; -fx-border-color: transparent transparent transparent %s; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: %s;", Paleta.VECTOR_B_BG, Paleta.VECTOR_B_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_LLENO     = "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private static final String IN_LLENO_B   = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #06B6D4; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private static final String IN_FOCUS_V   = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: %s; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);", Paleta.CELDA_NORMAL_BG, Paleta.CELDA_NORMAL_FOCUS, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_FOCUS_VB  = String.format("-fx-background-color: %s; -fx-border-color: transparent transparent transparent %s; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: %s; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);", Paleta.VECTOR_B_BG, Paleta.VECTOR_B_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_FOCUS_L   = "-fx-background-color: #FFFFFF; -fx-border-color: #2DD4BF; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: #1E293B; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);";
    private static final String IN_FOCUS_LB  = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #2DD4BF; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);";

    // ===== Estilos Botones Sidebar =====
    private final String BTN_SIDEBAR_BASE = "-fx-background-color: " + Paleta.ACENTO + "; -fx-text-fill: " + Paleta.FONDO_GLOBAL + "; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
    private final String BTN_SIDEBAR_HOVER = "-fx-background-color: #5EEAD4; -fx-text-fill: " + Paleta.FONDO_GLOBAL + "; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.4), 10, 0, 0, 0);";
    private final String BTN_SIDEBAR_ACTIVO = "-fx-background-color: #D8B4FE; -fx-text-fill: #4C1D95; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(168,85,247,0.8), 18, 0.6, 0, 0); -fx-border-color: #A855F7; -fx-border-width: 2; -fx-border-radius: 8;";
    
    private Button botonActivo = null;

    private GridPane gridMatriz;
    private TextField[][] casillas;
    private int n = 3;

    private VBox panelResultado;
    private Label lblEstado;
    private Label lblDetalle;

    private Label lblAlertaIngreso;
    private VBox contenedorResultadosVisuales;

    private VBox contenidoGlobal;
    private HBox envolturaMatriz;

    private Path parentesisIzq;
    private Path parentesisDer;

    private Label lblMetodoActivo;

    private enum Iteracion { JACOBI, SEIDEL, SOR, RELAJACION, MEJORAMIENTO }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // ==========================================
        // 1. PANEL LATERAL FIJO (Menú)
        // ==========================================
        VBox panelLateral = new VBox(12);
        panelLateral.setPadding(new Insets(20));
        panelLateral.setStyle("-fx-background-color: " + Paleta.FONDO_LATERAL + "; -fx-border-color: transparent " + Paleta.BORDE_SUTIL + " transparent transparent; -fx-border-width: 0 1 0 0;");
        panelLateral.setPrefWidth(290);

        Label tituloMenu = new Label("SISTEMAS DE\nECUACIONES LINEALES");
        tituloMenu.setTextFill(Paleta.color(Paleta.TEXTO_PRINCIPAL));
        tituloMenu.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 22));
        tituloMenu.setWrapText(true);
        tituloMenu.setPadding(new Insets(0, 0, 15, 0));

        Button btnGaussSimple  = crearBotonSidebar("Gauss (Pivoteo Simple)");
        Button btnGaussParcial = crearBotonSidebar("Gauss (Pivoteo Parcial)");
        Button btnGaussTotal   = crearBotonSidebar("Gauss (Pivoteo Total)");
        Button btnJordan       = crearBotonSidebar("Gauss-Jordan");
        Button btnFactorizarLU = crearBotonSidebar("Factorización LU");
        Button btnCroutL1U     = crearBotonSidebar("Crout (L1 U)");
        Button btnCroutLU1     = crearBotonSidebar("Crout (L U1)");
        Button btnCholeskyL    = crearBotonSidebar("Cholesky (L L^T)");
        Button btnCholeskyU    = crearBotonSidebar("Cholesky (U^T U)");
        Button btnJacobi       = crearBotonSidebar("Jacobi");
        Button btnSeidel       = crearBotonSidebar("Gauss-Seidel");
        Button btnSOR          = crearBotonSidebar("SOR");
        Button btnRelajamiento = crearBotonSidebar("Relajamiento");
        Button btnMejoramiento = crearBotonSidebar("Mejoramiento Iterativo");

        // EVENTOS MATEMÁTICOS
        btnGaussSimple.setOnAction(e -> { marcarBotonActivo(btnGaussSimple); ejecutarGauss(3); });
        btnGaussParcial.setOnAction(e -> { marcarBotonActivo(btnGaussParcial); ejecutarGauss(1); });
        btnGaussTotal.setOnAction(e -> { marcarBotonActivo(btnGaussTotal); ejecutarGauss(2); });
        btnJordan.setOnAction(e -> { marcarBotonActivo(btnJordan); ejecutarJordan(); });

        btnFactorizarLU.setOnAction(e -> {
            marcarBotonActivo(btnFactorizarLU);
            setMetodoActivo("Factorización LU");
            limpiarPantalla();
            try {
                Matriz matrizA = leerMatrizDesdeInterfaz().extraerA();
                MetodosDirectosMatriz.ResultadoLU res = MetodosDirectosMatriz.factorizar_x_gauss(matrizA, 1);
                // Agregamos 'null' como último parámetro porque aquí no hay vector solución
                animarDobleMatriz(res.L, res.U, "L", "U", "Factorización LU", res.huboPermutacion, null);
            } catch (Exception ex) {
                mostrarError("Error Matemático", ex.getMessage());
            }
        });

        btnCroutL1U.setOnAction(e -> { marcarBotonActivo(btnCroutL1U); ejecutarCrout(true); });
        btnCroutLU1.setOnAction(e -> { marcarBotonActivo(btnCroutLU1); ejecutarCrout(false); });
        btnCholeskyL.setOnAction(e -> { marcarBotonActivo(btnCholeskyL); ejecutarCholesky(true); });
        btnCholeskyU.setOnAction(e -> { marcarBotonActivo(btnCholeskyU); ejecutarCholesky(false); });

        btnJacobi.setOnAction(e -> { marcarBotonActivo(btnJacobi); ejecutarIterativo(Iteracion.JACOBI); });
        btnSeidel.setOnAction(e -> { marcarBotonActivo(btnSeidel); ejecutarIterativo(Iteracion.SEIDEL); });
        btnSOR.setOnAction(e -> { marcarBotonActivo(btnSOR); ejecutarIterativo(Iteracion.SOR); });
        btnRelajamiento.setOnAction(e -> { marcarBotonActivo(btnRelajamiento); ejecutarIterativo(Iteracion.RELAJACION); });
        btnMejoramiento.setOnAction(e -> { marcarBotonActivo(btnMejoramiento); ejecutarIterativo(Iteracion.MEJORAMIENTO); });

        panelLateral.getChildren().addAll(
            tituloMenu, new Separator(),
            crearSubtitulo("RESOLUCIÓN DIRECTA"), btnGaussSimple, btnGaussParcial, btnGaussTotal, btnJordan,
            new Separator(), crearSubtitulo("FACTORIZACIONES"), btnFactorizarLU, btnCroutL1U, btnCroutLU1, btnCholeskyL, btnCholeskyU,
            new Separator(), crearSubtitulo("ITERATIVOS"), btnJacobi, btnSeidel, btnSOR, btnRelajamiento, btnMejoramiento
        );

        // ==========================================
        // 2. PANEL DE CONTROLES FIJOS
        // ==========================================
        HBox barraControles = new HBox(15);
        barraControles.setAlignment(Pos.CENTER_LEFT);
        barraControles.setPadding(new Insets(20, 20, 10, 30));
        barraControles.setStyle("-fx-background-color: " + Paleta.FONDO_GLOBAL + ";");

        Label lblTamanio = new Label("Tamaño:");
        lblTamanio.setTextFill(Paleta.color(Paleta.TEXTO_PRINCIPAL));
        lblTamanio.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 14));

        Spinner<Integer> spinnerN = new Spinner<>(2, Matriz.MAX_DIMENSION, n);
        spinnerN.setPrefWidth(80);
        spinnerN.setEditable(true);
        spinnerN.setStyle("-fx-background-color: " + Paleta.FONDO_TARJETA2 + "; -fx-border-color: " + Paleta.BORDE_SUTIL + "; -fx-background-radius: 6;");
        spinnerN.getEditor().setStyle("-fx-background-color: " + Paleta.FONDO_TARJETA2 + "; -fx-text-fill: " + Paleta.TEXTO_PRINCIPAL + "; -fx-font-weight: bold; -fx-font-family: '" + FUENTE_UI + "';");

        spinnerN.valueProperty().addListener((obs, viejo, nuevo) -> {
            n = nuevo;
            marcarBotonActivo(null);
            setMetodoActivo(null);
            limpiarPantalla();
            generarCuadricula();
        });

        Button btnRandom = crearBotonAleatorio("Llenar Aleatorio");
        btnRandom.setOnAction(e -> {
            marcarBotonActivo(null);
            setMetodoActivo("Llenado Aleatorio");
            generarMatrizAleatoria();
        });

        Button btnLimpiar = crearBotonLimpiar("Limpiar Matriz");
        btnLimpiar.setOnAction(e -> {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j <= n; j++) {
                    casillas[i][j].setText("");
                }
            }
            marcarBotonActivo(null);
            setMetodoActivo(null);
            limpiarPantalla();
        });
        
        // --- NUEVO: Botón Importar TXT ---
        Button btnImportar = crearBotonLimpiar("Importar TXT");
        btnImportar.setOnAction(e -> cargarMatrizDesdeTXT(stage));

        // Región flexible para empujar el botón Importar a la derecha
        Region espaciadorBarra = new Region();
        HBox.setHgrow(espaciadorBarra, Priority.ALWAYS);

        // Ensamblaje de la barra superior
        barraControles.getChildren().addAll(lblTamanio, spinnerN, btnRandom, btnLimpiar, espaciadorBarra, btnImportar);

        // ==========================================
        // 3. CONTENIDO CENTRAL
        // ==========================================
        contenidoGlobal = new VBox(15);
        contenidoGlobal.setAlignment(Pos.TOP_CENTER);
        contenidoGlobal.setPadding(new Insets(20, 40, 40, 40));

        lblMetodoActivo = new Label("");
        lblMetodoActivo.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 34));
        lblMetodoActivo.setTextFill(Paleta.color(Paleta.ACENTO));
        lblMetodoActivo.setAlignment(Pos.CENTER);
        lblMetodoActivo.setVisible(false);
        lblMetodoActivo.setManaged(false);

        Label tituloMatriz = new Label("Ingrese la Matriz Ampliada");
        tituloMatriz.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 26));
        tituloMatriz.setTextFill(Paleta.color(Paleta.TEXTO_PRINCIPAL));
        VBox.setMargin(tituloMatriz, new Insets(10, 0, 0, 0));

        lblAlertaIngreso = new Label("⚠️ Carácter inválido: Solo números, signos o puntos.");
        lblAlertaIngreso.setTextFill(Paleta.color(Paleta.ERROR_TEXTO));
        lblAlertaIngreso.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 13));
        lblAlertaIngreso.setVisible(false);
        lblAlertaIngreso.setManaged(false);

        envolturaMatriz = new HBox(5);
        envolturaMatriz.setAlignment(Pos.CENTER);

        parentesisIzq = new Path();
        parentesisDer = new Path();

        gridMatriz = new GridPane();
        gridMatriz.setAlignment(Pos.CENTER);

        envolturaMatriz.getChildren().addAll(parentesisIzq, gridMatriz, parentesisDer);

        contenedorResultadosVisuales = new VBox(20);
        contenedorResultadosVisuales.setAlignment(Pos.CENTER);

        panelResultado = new VBox(5);
        panelResultado.setAlignment(Pos.CENTER);
        panelResultado.setPadding(new Insets(15, 20, 15, 20));
        panelResultado.setMaxWidth(600);
        panelResultado.setVisible(false);

        lblEstado = new Label();
        lblEstado.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 15));
        lblDetalle = new Label();
        lblDetalle.setFont(Font.font(FUENTE_MONO, 13));
        lblDetalle.setWrapText(true);
        lblDetalle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        panelResultado.getChildren().addAll(lblEstado, lblDetalle);

        generarCuadricula();

        // --- ENSAMBLAJE APILADO HACIA ABAJO (El clásico centrado) ---
        contenidoGlobal.getChildren().addAll(
            lblMetodoActivo,
            tituloMatriz,
            lblAlertaIngreso,
            envolturaMatriz,       // 1. La matriz siempre centrada arriba
            contenedorResultadosVisuales, // 2. Las animaciones L y U o los Pivoteos al medio
            panelResultado         // 3. El cartel de resultados siempre abajo
        );


        // ==========================================
        // 4. ENSAMBLAJE Y SCROLL
        // ==========================================
        StackPane centerWrapper = new StackPane(contenidoGlobal);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setStyle("-fx-background-color: transparent;");

        ScrollPane centroScroll = new ScrollPane(centerWrapper);
        centroScroll.setFitToWidth(false);
        centroScroll.setFitToHeight(false);

        centroScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        centroScroll.setBorder(Border.EMPTY);
        centroScroll.setPannable(true);

        centroScroll.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Platform.runLater(() -> {
                    Region viewport = (Region) centroScroll.lookup(".viewport");
                    if (viewport != null) viewport.setStyle("-fx-background-color: transparent;");
                });
            }
        });

        centerWrapper.minWidthProperty().bind(centroScroll.widthProperty().subtract(2));

        StackPane layoutPrincipal = new StackPane();
        Region bgGrid = new Region();
        bgGrid.setStyle(Paleta.FONDO_GRILLA);

        BorderPane contentPane = new BorderPane();
        contentPane.setTop(barraControles);
        contentPane.setCenter(centroScroll);
        contentPane.setStyle("-fx-background-color: transparent;");

        layoutPrincipal.getChildren().addAll(bgGrid, contentPane);

        BorderPane root = new BorderPane();
        root.setLeft(panelLateral);
        root.setCenter(layoutPrincipal);

        Scene scene = new Scene(root, 1180, 780);
        scene.setFill(Paleta.color(Paleta.FONDO_GLOBAL));
        stage.setTitle("Sistemas de Ecuaciones Lineales");
        stage.setScene(scene);
        stage.show();
    }

    // ==========================================
    // MARCAR BOTÓN ACTIVO (Lila Claro)
    // ==========================================
    private void marcarBotonActivo(Button btn) {
        if (botonActivo != null) {
            botonActivo.setStyle(BTN_SIDEBAR_BASE);
        }
        botonActivo = btn;
        if (botonActivo != null) {
            botonActivo.setStyle(BTN_SIDEBAR_ACTIVO);
        }
    }

    // ==========================================
    // TÍTULO DINÁMICO DEL MÉTODO ACTIVO
    // ==========================================
    private void setMetodoActivo(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            lblMetodoActivo.setText("");
            lblMetodoActivo.setVisible(false);
            lblMetodoActivo.setManaged(false);
        } else {
            lblMetodoActivo.setText(nombre);
            lblMetodoActivo.setVisible(true);
            lblMetodoActivo.setManaged(true);
            animarAparicion(lblMetodoActivo);
        }
    }

    // ==========================================
    // GENERACIÓN DE MATRIZ ALEATORIA (DIAGONAL DOMINANTE)
    // ==========================================
    private void generarMatrizAleatoria() {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int val = rand.nextInt(5) + 1;
                    casillas[i][j].setText(String.valueOf(val));
                    sum += val;
                }
            }
            int diag = sum + rand.nextInt(10) + 1;
            casillas[i][i].setText(String.valueOf(diag));
            casillas[i][n].setText(String.valueOf(rand.nextInt(21) - 10));
        }
        limpiarPantalla();
    }

    private static String estiloInput(boolean hasText, boolean focused, boolean esB) {
        if (focused) {
            if (hasText) return esB ? IN_FOCUS_LB : IN_FOCUS_L;
            else         return esB ? IN_FOCUS_VB : IN_FOCUS_V;
        } else {
            if (hasText) return esB ? IN_LLENO_B   : IN_LLENO;
            else         return esB ? IN_VACIO_B   : IN_VACIO;
        }
    }

    // ==========================================
    // MÉTODOS VISUALES Y REDIMENSIONAMIENTO
    // ==========================================
    private void generarCuadricula() {
        gridMatriz.getChildren().clear();
        int columnasTotales = n + 1;
        casillas = new TextField[n][columnasTotales];

        int cellSize = 60;
        int fontSize = 16;
        int gap = 10;

        if (n > 5) { cellSize = 45; fontSize = 14; gap = 6; }
        if (n > 8) { cellSize = 35; fontSize = 12; gap = 4; }
        if (n > 10) { cellSize = 28; fontSize = 10; gap = 2; }

        gridMatriz.setHgap(gap);
        gridMatriz.setVgap(gap);

        Label lblA = new Label("A");
        lblA.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 18));
        lblA.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        lblA.setAlignment(Pos.CENTER);
        lblA.setMaxWidth(Double.MAX_VALUE);

        Label lblB = new Label("b");
        lblB.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 18));
        lblB.setTextFill(Paleta.color(Paleta.ACENTO));
        lblB.setAlignment(Pos.CENTER);
        lblB.setMaxWidth(Double.MAX_VALUE);

        gridMatriz.add(lblA, 0, 0, n, 1);
        gridMatriz.add(lblB, n, 0);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnasTotales; j++) {
                TextField tf = new TextField("");
                tf.setPromptText("0");
                tf.setPrefWidth(cellSize);
                tf.setPrefHeight(cellSize);
                tf.setAlignment(Pos.CENTER);
                tf.setFont(Font.font(FUENTE_UI, fontSize));

                final boolean esB = (j == columnasTotales - 1);
                tf.setStyle(estiloInput(false, false, esB));

                tf.focusedProperty().addListener((obs, was, isF) -> tf.setStyle(estiloInput(!tf.getText().isEmpty(), isF, esB)));

                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("-?\\d*([\\.,]\\d*)?")) {
                        if (lblAlertaIngreso != null) {
                            lblAlertaIngreso.setVisible(true);
                            lblAlertaIngreso.setManaged(true);
                        }
                        tf.setText(oldVal);
                    } else {
                        if (lblAlertaIngreso != null) {
                            lblAlertaIngreso.setVisible(false);
                            lblAlertaIngreso.setManaged(false);
                        }
                        tf.setStyle(estiloInput(!newVal.isEmpty(), tf.isFocused(), esB));
                    }
                });

                final int fila = i;
                final int col = j;
                tf.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.UP && fila > 0) casillas[fila - 1][col].requestFocus();
                    else if (e.getCode() == KeyCode.DOWN && fila < n - 1) casillas[fila + 1][col].requestFocus();
                    else if (e.getCode() == KeyCode.LEFT && tf.getCaretPosition() == 0 && col > 0) casillas[fila][col - 1].requestFocus();
                    else if (e.getCode() == KeyCode.RIGHT && tf.getCaretPosition() == tf.getText().length() && col < columnasTotales - 1) casillas[fila][col + 1].requestFocus();
                });

                casillas[i][j] = tf;
                gridMatriz.add(tf, j, i + 1);
            }
        }

        double alturaMatriz = n * (cellSize + gap) + 40;

        parentesisIzq.getElements().clear();
        parentesisIzq.getElements().add(new MoveTo(10, 0));
        parentesisIzq.getElements().add(new QuadCurveTo(-5, alturaMatriz / 2, 10, alturaMatriz));
        parentesisIzq.setStroke(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        parentesisIzq.setStrokeWidth(2.5);
        parentesisIzq.setFill(Color.TRANSPARENT);

        parentesisDer.getElements().clear();
        parentesisDer.getElements().add(new MoveTo(0, 0));
        parentesisDer.getElements().add(new QuadCurveTo(15, alturaMatriz / 2, 0, alturaMatriz));
        parentesisDer.setStroke(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        parentesisDer.setStrokeWidth(2.5);
        parentesisDer.setFill(Color.TRANSPARENT);
    }

    private void limpiarPantalla() {
        contenedorResultadosVisuales.getChildren().clear();
        panelResultado.setVisible(false);
        lblAlertaIngreso.setVisible(false);
        lblAlertaIngreso.setManaged(false);
    }

    private TextField crearCeldaResultados() {
        TextField tf = new TextField("");
        tf.setPrefWidth(60);
        tf.setPrefHeight(40);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false);
        tf.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 14));
        tf.setStyle(ESTILO_NORMAL);
        return tf;
    }

    private HBox envolverEnParentesis(GridPane grid) {
        double altura = n * 50;
        Path pIzq = new Path();
        pIzq.getElements().add(new MoveTo(10, 0));
        pIzq.getElements().add(new QuadCurveTo(-5, altura / 2, 10, altura));
        pIzq.setStroke(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        pIzq.setStrokeWidth(2);
        pIzq.setFill(Color.TRANSPARENT);

        Path pDer = new Path();
        pDer.getElements().add(new MoveTo(0, 0));
        pDer.getElements().add(new QuadCurveTo(15, altura / 2, 0, altura));
        pDer.setStroke(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        pDer.setStrokeWidth(2);
        pDer.setFill(Color.TRANSPARENT);

        HBox box = new HBox(5, pIzq, grid, pDer);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Label crearSubtitulo(String texto) {
        Label lbl = new Label(texto);
        lbl.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        lbl.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 12));
        return lbl;
    }

    private Button crearBotonSidebar(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(BTN_SIDEBAR_BASE);

        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), btn);
        stIn.setToX(1.02); stIn.setToY(1.02);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), btn);
        stOut.setToX(1.0); stOut.setToY(1.0);

        btn.setOnMouseEntered(e -> { 
            if (btn != botonActivo) btn.setStyle(BTN_SIDEBAR_HOVER); 
            stIn.playFromStart(); 
        });
        btn.setOnMouseExited(e -> { 
            if (btn != botonActivo) btn.setStyle(BTN_SIDEBAR_BASE); 
            stOut.playFromStart(); 
        });
        return btn;
    }

    private Button crearBotonAleatorio(String texto) {
        Button btn = new Button(texto);
        String base = String.format(
            "-fx-background-color: %s; -fx-text-fill: %s; " +
            "-fx-font-family: '%s'; -fx-font-weight: bold; -fx-font-size: 13px; " +
            "-fx-background-radius: 8; -fx-cursor: hand;",
            Paleta.ACENTO_AMBER, Paleta.FONDO_GLOBAL, FUENTE_UI);
        String hover = String.format(
            "-fx-background-color: #FCD34D; -fx-text-fill: %s; " +
            "-fx-font-family: '%s'; -fx-font-weight: bold; -fx-font-size: 13px; " +
            "-fx-background-radius: 8; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(245,158,11,0.5), 10, 0, 0, 0);",
            Paleta.FONDO_GLOBAL, FUENTE_UI);
        btn.setStyle(base);
        btn.setPrefHeight(32);
        aplicarAnimacionHover(btn, base, hover);
        return btn;
    }

    private Button crearBotonLimpiar(String texto) {
        Button btn = new Button(texto);
        String base = String.format(
            "-fx-background-color: transparent; -fx-text-fill: %s; " +
            "-fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-font-family: '%s'; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-cursor: hand;",
            Paleta.ACENTO_AMBER, Paleta.ACENTO_AMBER, FUENTE_UI);
        String hover = String.format(
            "-fx-background-color: %s; -fx-text-fill: %s; " +
            "-fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-font-family: '%s'; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(245,158,11,0.5), 10, 0, 0, 0);",
            Paleta.ACENTO_AMBER, Paleta.FONDO_GLOBAL, Paleta.ACENTO_AMBER, FUENTE_UI);
        btn.setStyle(base);
        btn.setPrefHeight(32);
        aplicarAnimacionHover(btn, base, hover);
        return btn;
    }

    private void aplicarAnimacionHover(Button btn, String base, String hover) {
        ScaleTransition stIn  = new ScaleTransition(Duration.millis(150), btn);
        stIn.setToX(1.05); stIn.setToY(1.05);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), btn);
        stOut.setToX(1.0); stOut.setToY(1.0);
        btn.setOnMouseEntered(e -> { btn.setStyle(hover); stIn.playFromStart(); });
        btn.setOnMouseExited (e -> { btn.setStyle(base);  stOut.playFromStart(); });
    }

    // ==========================================
    // MENSAJES
    // ==========================================
    private void animarAparicion(javafx.scene.Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(180), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

   private void mostrarExito(String titulo, String detalle, boolean esVectorX) {
        panelResultado.setStyle("-fx-background-color: " + Paleta.EXITO_FONDO + "; -fx-border-color: " + Paleta.EXITO_BORDE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        lblEstado.setTextFill(Paleta.color(Paleta.EXITO_TEXTO));
        lblEstado.setText("✔ " + titulo);
        lblDetalle.setTextFill(Paleta.color(Paleta.TEXTO_PRINCIPAL));
        
        if (esVectorX) {
            // Reemplaza el separador por saltos de línea para listar x1, x2, x3 en columna
            detalle = detalle.replace("   |   ", "\n");
            // Centra el texto de la columna para que se vea elegante en la parte inferior
            lblDetalle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        } else {
            lblDetalle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        }
        
        // Hacemos que el panel ocupe un ancho generoso siempre para mantener la simetría central
        panelResultado.setMaxWidth(600); 
        
        lblDetalle.setText(detalle);
        panelResultado.setVisible(true);
        animarAparicion(panelResultado);
    }

    private void mostrarError(String titulo, String detalle) {
        panelResultado.setStyle("-fx-background-color: " + Paleta.ERROR_FONDO + "; -fx-border-color: " + Paleta.ERROR_BORDE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        lblEstado.setTextFill(Paleta.color(Paleta.ERROR_TEXTO));
        lblEstado.setText("✖ " + titulo);
        lblDetalle.setTextFill(Paleta.color(Paleta.ERROR_TEXTO));
        lblDetalle.setText(detalle);
        panelResultado.setVisible(true);
        animarAparicion(panelResultado);
    }

    private void mostrarAdvertencia(String mensaje) {
        panelResultado.setStyle("-fx-background-color: " + Paleta.ADVERTENCIA_FONDO + "; -fx-border-color: " + Paleta.ADVERTENCIA_BORDE + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        lblEstado.setTextFill(Paleta.color(Paleta.ADVERTENCIA_TEXTO));
        lblEstado.setText("⚠ Aviso");
        lblDetalle.setTextFill(Paleta.color(Paleta.ADVERTENCIA_TEXTO));
        lblDetalle.setText(mensaje);
        panelResultado.setVisible(true);
        animarAparicion(panelResultado);
    }

    private Matriz leerMatrizDesdeInterfaz() {
        int columnas = n + 1;
        double[][] datos = new double[n][columnas];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnas; j++) {
                String texto = casillas[i][j].getText().trim().replace(",", ".");
                if (texto.isEmpty()) texto = "0";
                datos[i][j] = Double.parseDouble(texto);
            }
        }
        return new Matriz(datos);
    }

    // ==========================================
    // MÉTODOS MATEMÁTICOS ACTIVADOS
    // ==========================================

    private void ejecutarGauss(int tipoPivoteo) {
        String nombre = (tipoPivoteo == 1) ? "Gauss · Pivoteo Parcial"
                      : (tipoPivoteo == 2) ? "Gauss · Pivoteo Total"
                                           : "Gauss · Pivoteo Simple";
        setMetodoActivo(nombre);
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            int nLocal = matrizAmp.getCantFilas();

            double[] solucion = null;
            Exception mathError = null;
            try {
                if (tipoPivoteo == 1)      solucion = MetodosDirectosMatriz.GaussPivoteoParcial(matrizAmp);
                else if (tipoPivoteo == 2) solucion = MetodosDirectosMatriz.GaussPivoteoTotal(matrizAmp);
                else                       solucion = MetodosDirectosMatriz.GaussPivoteoSimple(matrizAmp);
            } catch (Exception ex) {
                mathError = ex;
            }

            boolean animar = (nLocal <= 5);
            if (animar) {
                // AQUÍ DERIVAMOS A LA ANIMACIÓN CORRECTA
                if (tipoPivoteo == 2) {
                    animarGaussTotal(matrizAmp, solucion, mathError);
                } else {
                    animarGaussParcialSimple(matrizAmp, tipoPivoteo, solucion, mathError);
                }
            } else if (mathError != null) {
                mostrarError("Error Matemático", mathError.getMessage());
            } else {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            }
        } catch (NumberFormatException ex) {
            mostrarError("Error de Ingreso", "Asegúrese de ingresar solo números.");
        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }
    // ==============================================================
    // ANIMACIÓN EXCLUSIVA PARA PIVOTEO TOTAL
    // ==============================================================
    private void animarGaussTotal(Matriz original, double[] solucion, Exception mathError) {
        int nLocal = original.getCantFilas();
        int cols = nLocal + 1;

        final String ESTILO_COL_VERDE_AGUA = String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;",
            Paleta.CELDA_L_BG, Paleta.CELDA_L_BORDE, Paleta.CELDA_L_TEXTO
        );

        List<double[][]> snapshots  = new ArrayList<>();
        List<String>     labels     = new ArrayList<>();
        List<Integer>    pivotRows1 = new ArrayList<>();
        List<Integer>    pivotRows2 = new ArrayList<>();
        List<Integer>    pivotCols1 = new ArrayList<>();
        List<Integer>    pivotCols2 = new ArrayList<>();

        double[][] sim = new double[nLocal][cols];
        for (int i = 0; i < nLocal; i++) {
            for (int j = 0; j < cols; j++) {
                sim[i][j] = original.getElemento(i, j);
            }
        }

        snapshots.add(copiar(sim));
        labels.add("Matriz ampliada inicial");
        pivotRows1.add(-1); pivotRows2.add(-1);
        pivotCols1.add(-1); pivotCols2.add(-1);

        for (int k = 0; k < nLocal - 1; k++) {
            int filaMayor = k;
            int colMayor = k;
            double maxValor = Math.abs(sim[k][k]);

            for (int i = k; i < nLocal; i++) {
                for (int j = k; j < nLocal; j++) {
                    if (Math.abs(sim[i][j]) > maxValor) {
                        maxValor = Math.abs(sim[i][j]);
                        filaMayor = i;
                        colMayor = j;
                    }
                }
            }

            if (filaMayor != k) {
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": filas seleccionadas F" + (k + 1) + " ↔ F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor);
                pivotCols1.add(-1); pivotCols2.add(-1);

                for (int j = 0; j < cols; j++) {
                    double tmp = sim[k][j];
                    sim[k][j] = sim[filaMayor][j];
                    sim[filaMayor][j] = tmp;
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": intercambio F" + (k + 1) + " ↔ F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor);
                pivotCols1.add(-1); pivotCols2.add(-1);
            }

            if (colMayor != k) {
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": columnas seleccionadas C" + (k + 1) + " ↔ C" + (colMayor + 1));
                pivotRows1.add(-1); pivotRows2.add(-1);
                pivotCols1.add(k); pivotCols2.add(colMayor);

                for (int i = 0; i < nLocal; i++) {
                    double tmp = sim[i][k];
                    sim[i][k] = sim[i][colMayor];
                    sim[i][colMayor] = tmp;
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": intercambio C" + (k + 1) + " ↔ C" + (colMayor + 1));
                pivotRows1.add(-1); pivotRows2.add(-1);
                pivotCols1.add(k); pivotCols2.add(colMayor);
            }

            if (Math.abs(sim[k][k]) > 1e-12) {
                for (int i = k + 1; i < nLocal; i++) {
                    double m = sim[i][k] / sim[k][k];
                    for (int j = k; j < cols; j++) {
                        sim[i][j] -= m * sim[k][j];
                    }
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": eliminación bajo pivote F" + (k + 1));
                pivotRows1.add(-1); pivotRows2.add(-1);
                pivotCols1.add(-1); pivotCols2.add(-1);
            }
        }

        GridPane gridSim = new GridPane();
        gridSim.setAlignment(Pos.CENTER);
        gridSim.setHgap(4);
        gridSim.setVgap(4);

        TextField[][] celdas = new TextField[nLocal][cols];
        for (int i = 0; i < nLocal; i++) {
            for (int j = 0; j < cols; j++) {
                celdas[i][j] = crearCeldaResultados();
                celdas[i][j].setText(String.format("%.2f", snapshots.get(0)[i][j]));
                if (j == cols - 1) celdas[i][j].setStyle(ESTILO_VECTOR_B);
                gridSim.add(celdas[i][j], j, i);
            }
        }

        Label lblPaso = new Label(labels.get(0));
        lblPaso.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        lblPaso.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 14));

        HBox envoltura = envolverEnParentesis(gridSim);
        VBox bloqueVisual = new VBox(10, lblPaso, envoltura);
        bloqueVisual.setAlignment(Pos.CENTER);

        contenedorResultadosVisuales.getChildren().add(bloqueVisual);
        animarAparicion(bloqueVisual);

        Timeline timeline = new Timeline();
        double stepDelay = 0.7;

        for (int s = 1; s < snapshots.size(); s++) {
            final double[][] snap = snapshots.get(s);
            final String lbl = labels.get(s);
            final int pRow1 = pivotRows1.get(s);
            final int pRow2 = pivotRows2.get(s);
            final int pCol1 = pivotCols1.get(s);
            final int pCol2 = pivotCols2.get(s);

            KeyFrame frame = new KeyFrame(Duration.seconds(stepDelay * s), e -> {
                lblPaso.setText(lbl);
                for (int i = 0; i < nLocal; i++) {
                    for (int j = 0; j < cols; j++) {
                        celdas[i][j].setText(String.format("%.2f", snap[i][j]));

                        if (pRow1 != -1 && pRow2 != -1) {
                            if (i == pRow1 || i == pRow2) {
                                celdas[i][j].setStyle(ESTILO_PIVOTE);
                            } else if (j == cols - 1) {
                                celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            } else {
                                celdas[i][j].setStyle(ESTILO_NORMAL);
                            }
                        } else if (pCol1 != -1 && pCol2 != -1) {
                            if (j == pCol1 || j == pCol2) {
                                celdas[i][j].setStyle(ESTILO_COL_VERDE_AGUA);
                            } else if (j == cols - 1) {
                                celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            } else {
                                celdas[i][j].setStyle(ESTILO_NORMAL);
                            }
                        } else {
                            if (j == cols - 1) {
                                celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            } else {
                                celdas[i][j].setStyle(ESTILO_NORMAL);
                            }
                        }
                    }
                }
            });
            timeline.getKeyFrames().add(frame);
        }

        double finalDelay = stepDelay * snapshots.size();
        KeyFrame fin = new KeyFrame(Duration.seconds(finalDelay), e -> {
            lblPaso.setText("Triangulación finalizada");
            for (int i = 0; i < nLocal; i++) {
                for (int j = 0; j < cols; j++) {
                    celdas[i][j].setStyle(j == cols - 1 ? ESTILO_VECTOR_B : ESTILO_NORMAL);
                }
            }
            if (mathError != null) {
                mostrarError("Error Matemático", mathError.getMessage());
            } else {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            }
        });
        timeline.getKeyFrames().add(fin);
        timeline.play();
    }
    
 // ==============================================================
    // ANIMACIÓN EXCLUSIVA PARA PIVOTEO PARCIAL Y SIMPLE
    // ==============================================================
    private void animarGaussParcialSimple(Matriz original, int tipoPivoteo, double[] solucion, Exception mathError) {
        int nLocal = original.getCantFilas();
        int cols = nLocal + 1;

        List<double[][]> snapshots  = new ArrayList<>();
        List<String>     labels     = new ArrayList<>();
        
        List<Integer>    pivotRows1 = new ArrayList<>();
        List<Integer>    pivotRows2 = new ArrayList<>();
        List<Integer>    columnaVerde = new ArrayList<>(); // Para pintar la columna del pivote

        double[][] sim = new double[nLocal][cols];
        for (int i = 0; i < nLocal; i++)
            for (int j = 0; j < cols; j++)
                sim[i][j] = original.getElemento(i, j);

        snapshots.add(copiar(sim));
        labels.add("Matriz ampliada inicial");
        pivotRows1.add(-1); pivotRows2.add(-1); columnaVerde.add(-1);

        for (int k = 0; k < nLocal - 1; k++) {
            int filaMayor = k;
            double maxValor = Math.abs(sim[k][k]);
            
            // Detección de Pivote (Solo en la columna k)
            for (int i = k + 1; i < nLocal; i++) {
                double va = Math.abs(sim[i][k]);
                if ((tipoPivoteo == 1 && va > maxValor) ||
                    (tipoPivoteo == 3 && maxValor < 1e-12 && va > 1e-12)) {
                    maxValor = va;
                    filaMayor = i;
                }
            }

            // Intercambio de Filas (Dos fotogramas con color)
            if (filaMayor != k) {
                // Fotograma A: Resaltar ANTES de mover
                snapshots.add(copiar(sim));
                labels.add("Pivoteo: Filas a intercambiar F" + (k + 1) + " y F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor); columnaVerde.add(k);

                // Mover los números
                for (int j = 0; j < cols; j++) {
                    double tmp = sim[k][j];
                    sim[k][j] = sim[filaMayor][j];
                    sim[filaMayor][j] = tmp;
                }
                
                // Fotograma B: Resaltar DESPUÉS de mover
                snapshots.add(copiar(sim));
                labels.add("Pivoteo: Intercambio F" + (k + 1) + " ↔ F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor); columnaVerde.add(k);
            }

            // Eliminación Gaussiana
            if (Math.abs(sim[k][k]) > 1e-12) {
                for (int i = k + 1; i < nLocal; i++) {
                    double m = sim[i][k] / sim[k][k];
                    for (int j = k; j < cols; j++) {
                        sim[i][j] -= m * sim[k][j];
                    }
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": eliminación bajo pivote F" + (k + 1));
                pivotRows1.add(-1); pivotRows2.add(-1); columnaVerde.add(-1);
            }
        }

        // --- RENDERIZADO VISUAL ---
        GridPane gridSim = new GridPane();
        gridSim.setAlignment(Pos.CENTER);
        gridSim.setHgap(4);
        gridSim.setVgap(4);

        TextField[][] celdas = new TextField[nLocal][cols];
        for (int i = 0; i < nLocal; i++) {
            for (int j = 0; j < cols; j++) {
                celdas[i][j] = crearCeldaResultados();
                celdas[i][j].setText(String.format("%.2f", snapshots.get(0)[i][j]));
                if (j == cols - 1) celdas[i][j].setStyle(ESTILO_VECTOR_B);
                gridSim.add(celdas[i][j], j, i);
            }
        }

        Label lblPaso = new Label(labels.get(0));
        lblPaso.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        lblPaso.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 14));

        HBox envoltura = envolverEnParentesis(gridSim);
        VBox bloqueVisual = new VBox(10, lblPaso, envoltura);
        bloqueVisual.setAlignment(Pos.CENTER);
        contenedorResultadosVisuales.getChildren().add(bloqueVisual);
        animarAparicion(bloqueVisual);

        Timeline timeline = new Timeline();
        double stepDelay = 0.7;

        for (int s = 1; s < snapshots.size(); s++) {
            final double[][] snap = snapshots.get(s);
            final String lbl = labels.get(s);
            final int pRow1 = pivotRows1.get(s);
            final int pRow2 = pivotRows2.get(s);
            final int pCol = columnaVerde.get(s);

            KeyFrame frame = new KeyFrame(Duration.seconds(stepDelay * s), e -> {
                lblPaso.setText(lbl);
                for (int i = 0; i < nLocal; i++) {
                    for (int j = 0; j < cols; j++) {
                        celdas[i][j].setText(String.format("%.2f", snap[i][j]));

                        // Regla de color estricta y simplificada
                        if (pRow1 != -1 && (i == pRow1 || i == pRow2)) {
                            celdas[i][j].setStyle(ESTILO_PIVOTE); // Fila en amarillo
                        } else if (pCol != -1 && j == pCol) {
                            celdas[i][j].setStyle("-fx-background-color: #e8f6f3; -fx-border-color: #1abc9c; -fx-background-radius: 6; -fx-text-fill: #16a085; -fx-font-weight: bold;"); // Columna en verde
                        } else if (j == cols - 1) {
                            celdas[i][j].setStyle(ESTILO_VECTOR_B);
                        } else {
                            celdas[i][j].setStyle(ESTILO_NORMAL);
                        }
                    }
                }
            });
            timeline.getKeyFrames().add(frame);
        }

        double finalDelay = stepDelay * snapshots.size();
        KeyFrame fin = new KeyFrame(Duration.seconds(finalDelay), e -> {
            lblPaso.setText("Triangulación finalizada");
            for (int i = 0; i < nLocal; i++) {
                for (int j = 0; j < cols; j++) {
                    celdas[i][j].setStyle(j == cols - 1 ? ESTILO_VECTOR_B : ESTILO_NORMAL);
                }
            }
            if (mathError != null) {
                mostrarError("Error Matemático", mathError.getMessage());
            } else {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            }
        });
        timeline.getKeyFrames().add(fin);
        timeline.play();
    }

    private void ejecutarJordan() {
        setMetodoActivo("Gauss-Jordan");
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            double[] solucion = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(matrizAmp, n);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < solucion.length; i++) {
                sb.append("x").append(i+1).append(" = ").append(String.format("%.4f", solucion[i]));
                if (i < solucion.length - 1) sb.append("   |   ");
            }
            mostrarExito("Gauss-Jordan", sb.toString(), true);
        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private void ejecutarCrout(boolean esL1U) {
        setMetodoActivo(esL1U ? "Crout · L1 · U" : "Crout · L · U1");
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            Matriz matrizA = matrizAmp.extraerA();
            
            // 1. Calculamos la solución matemática (vector x)
            double[] solucion = esL1U ? MetodosDirectosMatriz.CroutSolucionL1U(matrizAmp) : MetodosDirectosMatriz.CroutSolucionLU1(matrizAmp);
            
            // 2. Extraemos las matrices y enviamos la solución a la animación
            if (esL1U) {
                Matriz[] res = MetodosDirectosMatriz.factorizarCroutL1U(matrizA);
                animarDobleMatriz(res[0], res[1], "L1", "U", "Crout (L1 · U)", false, solucion);
            } else {
                Matriz[] res = MetodosDirectosMatriz.factorizarCroutLU1(matrizA);
                animarDobleMatriz(res[0], res[1], "L", "U1", "Crout (L · U1)", false, solucion);
            }
        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private void ejecutarCholesky(boolean esL) {
        setMetodoActivo(esL ? "Cholesky · L · Lᵀ" : "Cholesky · Uᵀ · U");
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            double[] solucion = esL ? MetodosDirectosMatriz.CholeskySolucionL(matrizAmp) : MetodosDirectosMatriz.CholeskySolucionU(matrizAmp);
            
            // Usamos 'true' para forzar que el resultado salga compacto en el panel lateral
            mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
        } catch (Exception ex) {
            mostrarError("Error de Cholesky", ex.getMessage());
        }
    }

    private void ejecutarIterativo(Iteracion tipo) {
        String nombre;
        switch (tipo) {
            case JACOBI:       nombre = "Jacobi";                break;
            case SEIDEL:       nombre = "Gauss-Seidel";          break;
            case SOR:          nombre = "SOR";                   break;
            case RELAJACION:   nombre = "Relajación";            break;
            case MEJORAMIENTO: nombre = "Mejoramiento Iterativo"; break;
            default:           nombre = "Iterativo";             break;
        }
        setMetodoActivo(nombre);
        limpiarPantalla();
        try {
            double tolerancia = 1e-6;
            int maxIter = 1000;
            double omega = 1.0;

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Parámetros Iterativos");
            dialog.initModality(Modality.APPLICATION_MODAL);

            ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);

            GridPane gridDialog = new GridPane();
            gridDialog.setHgap(10); gridDialog.setVgap(10); gridDialog.setPadding(new Insets(20, 150, 10, 10));

            TextField tfTol = new TextField("1e-6");
            TextField tfMax = new TextField("1000");
            TextField tfOmega = new TextField("1.0");

            gridDialog.add(new Label("Tolerancia:"), 0, 0); gridDialog.add(tfTol, 1, 0);
            gridDialog.add(new Label("Iteraciones máx:"), 0, 1); gridDialog.add(tfMax, 1, 1);
            if (tipo == Iteracion.SOR) {
                gridDialog.add(new Label("Omega (ω):"), 0, 2); gridDialog.add(tfOmega, 1, 2);
            }

            dialog.getDialogPane().setContent(gridDialog);
            if (dialog.showAndWait().orElse(ButtonType.CANCEL) != btnAceptar) return;

            try {
                tolerancia = Double.parseDouble(tfTol.getText().trim().replace(",", "."));
                maxIter = Integer.parseInt(tfMax.getText().trim());
                if (tipo == Iteracion.SOR) omega = Double.parseDouble(tfOmega.getText().trim().replace(",", "."));
            } catch (Exception e) {
                mostrarError("Error de Ingreso", "Parámetros numéricos inválidos.");
                return;
            }

            Matriz matrizAmp = leerMatrizDesdeInterfaz();

            for (int i = 0; i < n; i++) {
                if (Math.abs(matrizAmp.getElemento(i, i)) < 1e-12) {
                    mostrarError("Error de Convergencia", "El elemento a[" + (i+1) + "][" + (i+1) + "] es 0.\nLos métodos iterativos requieren que la diagonal no tenga ceros.");
                    return;
                }
            }

            double[] x0 = new double[n];
            double[] solucion = new double[n];
            String titulo = "";

            switch (tipo) {
                case JACOBI:
                    solucion = MetodosIterativosMatriz.JacobiSolucion(matrizAmp, x0, tolerancia, maxIter);
                    titulo = "Jacobi";
                    break;
                case SEIDEL:
                    solucion = MetodosIterativosMatriz.GaussSeidelSolucion(matrizAmp, x0, tolerancia, maxIter);
                    titulo = "Gauss-Seidel";
                    break;
                case SOR:
                    solucion = MetodosIterativosMatriz.SORSolucion(matrizAmp, x0, omega, tolerancia, maxIter);
                    titulo = "SOR";
                    break;
                case RELAJACION:
                    solucion = MetodosIterativosMatriz.Relajacion(matrizAmp, x0, tolerancia, maxIter);
                    titulo = "Relajación";
                    break;
                case MEJORAMIENTO:
                    solucion = MetodosIterativosMatriz.MejoramientoIterativo(matrizAmp, x0, tolerancia, maxIter);
                    titulo = "Mejoramiento Iterativo";
                    break;
            }

            for (double val : solucion) {
                if (Double.isNaN(val) || Double.isInfinite(val)) {
                    mostrarError("Error de Convergencia", "El método divergió (NaN).\nVerifique que la matriz sea diagonalmente dominante.");
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < solucion.length; i++) {
                sb.append("x").append(i+1).append(" = ").append(String.format("%.6f", solucion[i]));
                if (i < solucion.length - 1) sb.append("   |   ");
            }
            mostrarExito(titulo, sb.toString(), true);

        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private void animarDobleMatriz(Matriz M1, Matriz M2, String nom1, String nom2, String nombreMetodo, boolean huboPermutacion, double[] solucion) {
        GridPane gridCopia = new GridPane(); gridCopia.setAlignment(Pos.CENTER); gridCopia.setHgap(4); gridCopia.setVgap(4);
        GridPane gridM1 = new GridPane(); gridM1.setAlignment(Pos.CENTER); gridM1.setHgap(4); gridM1.setVgap(4);
        GridPane gridM2 = new GridPane(); gridM2.setAlignment(Pos.CENTER); gridM2.setHgap(4); gridM2.setVgap(4);

        TextField[][] celdasCopia = new TextField[n][n];
        TextField[][] celdasM1 = new TextField[n][n];
        TextField[][] celdasM2 = new TextField[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                celdasCopia[i][j] = crearCeldaResultados(); gridCopia.add(celdasCopia[i][j], j, i);
                celdasM1[i][j] = crearCeldaResultados(); gridM1.add(celdasM1[i][j], j, i);
                celdasM2[i][j] = crearCeldaResultados(); gridM2.add(celdasM2[i][j], j, i);
            }
        }

        Label lblFlecha = new Label(" ➡ "); lblFlecha.setFont(Font.font(FUENTE_UI, 30)); lblFlecha.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
        Label lblPor = new Label(" × "); lblPor.setFont(Font.font(FUENTE_UI, 30)); lblPor.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));

        HBox filaMatrices = new HBox(15,
            new VBox(5, crearSubtitulo("A"), envolverEnParentesis(gridCopia)),
            lblFlecha,
            new VBox(5, crearSubtitulo(nom1), envolverEnParentesis(gridM1)),
            lblPor,
            new VBox(5, crearSubtitulo(nom2), envolverEnParentesis(gridM2))
        );
        filaMatrices.setAlignment(Pos.CENTER);
        contenedorResultadosVisuales.getChildren().add(filaMatrices);
        animarAparicion(filaMatrices);

        String msgExito = "Descomposición finalizada.\nSe extrajeron las matrices " + nom1 + " y " + nom2 + ".";
        if (huboPermutacion) msgExito += "\n(Permutación: PA = " + nom1 + " · " + nom2 + ")";

        if (n > 5) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean esM1 = (i >= j);
                    double valor = esM1 ? M1.getElemento(i, j) : M2.getElemento(i, j);
                    celdasCopia[i][j].setText(String.format("%.3f", valor));
                    celdasCopia[i][j].setStyle(esM1 ? ESTILO_L : ESTILO_U);

                    celdasM1[i][j].setText(esM1 ? String.format("%.3f", M1.getElemento(i, j)) : "0.00");
                    celdasM1[i][j].setStyle(esM1 ? ESTILO_L : ESTILO_CELDA_VACIA_RES);

                    celdasM2[i][j].setText(i <= j ? String.format("%.3f", M2.getElemento(i, j)) : "0.00");
                    celdasM2[i][j].setStyle(i <= j ? ESTILO_U : ESTILO_CELDA_VACIA_RES);
                }
            }
            
            if (solucion != null) {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            } else {
                mostrarExito(nombreMetodo, "Matrices generadas al instante.\n" + (huboPermutacion ? "(Hubo permutación de filas)" : ""), false);
            }
            
        } else {
            Timeline timeline = new Timeline();
            double delaySegundos = 0.0;

            mostrarAdvertencia("Descomponiendo " + nombreMetodo + " paso a paso...");

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    final int fila = i;
                    final int col = j;

                    KeyFrame frame = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                        boolean esM1 = (fila >= col);
                        double valor = esM1 ? M1.getElemento(fila, col) : M2.getElemento(fila, col);

                        celdasCopia[fila][col].setText(String.format("%.3f", valor));
                        celdasCopia[fila][col].setStyle(esM1 ? ESTILO_L : ESTILO_U);

                        celdasM1[fila][col].setText(esM1 ? String.format("%.3f", M1.getElemento(fila, col)) : "0.00");
                        celdasM1[fila][col].setStyle(esM1 ? ESTILO_L : ESTILO_CELDA_VACIA_RES);

                        celdasM2[fila][col].setText(fila <= col ? String.format("%.3f", M2.getElemento(fila, col)) : "0.00");
                        celdasM2[fila][col].setStyle(fila <= col ? ESTILO_U : ESTILO_CELDA_VACIA_RES);
                    });
                    timeline.getKeyFrames().add(frame);
                    delaySegundos += 0.15;
                }
            }
            
            final String finalMsg = msgExito;
            KeyFrame fin = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                if (solucion != null) {
                    mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
                } else {
                    mostrarExito(nombreMetodo, finalMsg, false);
                }
            });
            timeline.getKeyFrames().add(fin);
            timeline.play();
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================
    private static double[][] copiar(double[][] m) {
        double[][] c = new double[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }

    private String formatSolucion(double[] solucion) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < solucion.length; i++) {
            sb.append("x").append(i + 1).append(" = ").append(String.format("%.4f", solucion[i]));
            if (i < solucion.length - 1) sb.append("   |   ");
        }
        return sb.toString();
    }
    
 // ==========================================
    // IMPORTACIÓN DESDE ARCHIVO TXT
    // ==========================================
    private void cargarMatrizDesdeTXT(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Matriz TXT");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Texto", "*.txt"));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                // Leemos todo el contenido y reemplazamos comas por puntos por seguridad
                String contenido = new String(Files.readAllBytes(file.toPath())).replace(",", ".");
                
                // Usamos un Scanner para extraer los números ignorando espacios y saltos de línea
                Scanner scanner = new Scanner(contenido);
                
                int i = 0;
                int j = 0;
                int columnasTotales = n + 1;
                
                // Llenamos la cuadrícula de izquierda a derecha, de arriba a abajo
                while (scanner.hasNext() && i < n) {
                    // Validamos que sea un número para evitar que el programa falle por un texto erróneo en el TXT
                    if (scanner.hasNextDouble()) {
                        double valor = scanner.nextDouble();
                        
                        // Si el valor es entero (ej. 5.0), lo escribimos sin decimales (5) para que quede más limpio
                        if (valor == (long) valor) {
                            casillas[i][j].setText(String.format("%d", (long) valor));
                        } else {
                            casillas[i][j].setText(String.valueOf(valor));
                        }
                        
                        j++;
                        if (j == columnasTotales) {
                            j = 0;
                            i++;
                        }
                    } else {
                        scanner.next(); // Salta cualquier basura que no sea número
                    }
                }
                scanner.close();
                
                marcarBotonActivo(null);
                setMetodoActivo("Archivo Cargado");
                limpiarPantalla();
                
            } catch (Exception ex) {
                mostrarError("Error de Archivo", "No se pudo leer el archivo TXT.");
            }
        }
    }
}
