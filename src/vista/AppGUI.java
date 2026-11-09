package vista;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import modelo.Matriz;
import servicios.MetodosDirectosMatriz;
import servicios.MetodosIterativosMatriz;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AppGUI extends Application {

    private static final String FUENTE_UI   = "Segoe UI";
    private static final String FUENTE_MONO = "Consolas";

    private static final String COLOR_LILA_OSCURO_BORDE = "#A855F7";
    private static final String COLOR_LILA_OSCURO_TEXTO = "#D8B4FE";

    private final String ESTILO_L = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;",
        Paleta.CELDA_L_BG, Paleta.CELDA_L_BORDE, Paleta.CELDA_L_TEXTO);
    private final String ESTILO_U = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;",
        Paleta.CELDA_U_BG, Paleta.CELDA_U_BORDE, Paleta.CELDA_U_TEXTO);
    private final String ESTILO_PIVOTE = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: #FDE047; -fx-font-weight: bold;",
        Paleta.PIVOTE_BG, Paleta.PIVOTE_BORDE);
    private final String ESTILO_COL_VERDE_AGUA = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: %s; -fx-font-weight: bold;",
        "#e8f6f3", "#1abc9c", "#16a085");
    private final String ESTILO_NORMAL = "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private final String ESTILO_VECTOR_B = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #06B6D4; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private final String ESTILO_CELDA_VACIA_RES = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s;",
        Paleta.CELDA_VACIA_BG, Paleta.BORDE_SUTIL, Paleta.CELDA_VACIA_TEXTO);

    private static final String IN_VACIO = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-background-radius: 6; -fx-text-fill: %s;",
        Paleta.CELDA_NORMAL_BG, Paleta.CELDA_NORMAL_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_VACIO_B = String.format(
        "-fx-background-color: %s; -fx-border-color: transparent transparent transparent %s; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: %s;",
        Paleta.VECTOR_B_BG, Paleta.VECTOR_B_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_LLENO = "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private static final String IN_LLENO_B = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #06B6D4; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B;";
    private static final String IN_FOCUS_V = String.format(
        "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: %s; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);",
        Paleta.CELDA_NORMAL_BG, Paleta.CELDA_NORMAL_FOCUS, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_FOCUS_VB = String.format(
        "-fx-background-color: %s; -fx-border-color: transparent transparent transparent %s; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: %s; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);",
        Paleta.VECTOR_B_BG, Paleta.VECTOR_B_BORDE, Paleta.TEXTO_PRINCIPAL);
    private static final String IN_FOCUS_L = "-fx-background-color: #FFFFFF; -fx-border-color: #2DD4BF; -fx-border-width: 2; -fx-background-radius: 6; -fx-text-fill: #1E293B; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);";
    private static final String IN_FOCUS_LB = "-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #2DD4BF; -fx-border-width: 0 0 0 3; -fx-background-radius: 6; -fx-text-fill: #1E293B; -fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);";

    private final String BTN_SIDEBAR_BASE = "-fx-background-color: " + Paleta.ACENTO + "; -fx-text-fill: " + Paleta.FONDO_GLOBAL + "; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
    private final String BTN_SIDEBAR_ACTIVO = "-fx-background-color: #D8B4FE; -fx-text-fill: #4C1D95; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(168,85,247,0.8), 18, 0.6, 0, 0); -fx-border-color: #A855F7; -fx-border-width: 2; -fx-border-radius: 8;";

    private final String ESTILO_ACORDEON_TITULO =
        "-fx-text-fill: " + COLOR_LILA_OSCURO_TEXTO + "; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 8 0 8 10;";

    private final String ESTILO_TARJETA_PASO =
        "-fx-background-color: #23232B; -fx-background-radius: 12; " +
        "-fx-border-color: #34343F; -fx-border-radius: 12; -fx-border-width: 1; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.2, 0, 2);";

    private Button botonActivo = null;
    private GridPane gridMatriz;
    private TextField[][] casillas;
    private int n = 3;

    private VBox panelResultado;
    private Label lblEstado;
    private Label lblDetalle;
    private Label lblAlertaIngreso;
    private VBox contenedorResultadosVisuales;

    private TitledPane acordeonPasos;
    private VBox vboxPasos;
    private ScrollPane scrollPasos;

    private VBox panelParametrosIterativos;
    private TextField tfTol, tfMax, tfOmega;
    private Iteracion iteracionPendiente = null;

    private VBox contenidoGlobal;
    private HBox envolturaMatriz;
    private Path parentesisIzq;
    private Path parentesisDer;
    private Label lblMetodoActivo;

    private Timeline timelineActivo = null;

    private enum Iteracion { JACOBI, SEIDEL, SOR, RELAJACION, MEJORAMIENTO }

    private static class PasoResolucion {
        int numero;
        String titulo;
        List<String> formulas;
        double[][] matriz;
        int filaResaltada = -1;
        int colResaltada = -1;
        String notaAdicional = null;

        PasoResolucion(int n, String t) {
            numero = n; titulo = t; formulas = new ArrayList<>();
        }
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
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

        btnGaussSimple.setOnAction(e -> { marcarBotonActivo(btnGaussSimple); ejecutarGauss(3); });
        btnGaussParcial.setOnAction(e -> { marcarBotonActivo(btnGaussParcial); ejecutarGauss(1); });
        btnGaussTotal.setOnAction(e -> { marcarBotonActivo(btnGaussTotal); ejecutarGauss(2); });
        btnJordan.setOnAction(e -> { marcarBotonActivo(btnJordan); ejecutarJordan(); });

        btnFactorizarLU.setOnAction(e -> { marcarBotonActivo(btnFactorizarLU); ejecutarLU(); });
        btnCroutL1U.setOnAction(e -> { marcarBotonActivo(btnCroutL1U); ejecutarCrout(true); });
        btnCroutLU1.setOnAction(e -> { marcarBotonActivo(btnCroutLU1); ejecutarCrout(false); });
        btnCholeskyL.setOnAction(e -> { marcarBotonActivo(btnCholeskyL); ejecutarCholesky(true); });
        btnCholeskyU.setOnAction(e -> { marcarBotonActivo(btnCholeskyU); ejecutarCholesky(false); });

        btnJacobi.setOnAction(e -> { marcarBotonActivo(btnJacobi); mostrarParametrosIterativos(Iteracion.JACOBI); });
        btnSeidel.setOnAction(e -> { marcarBotonActivo(btnSeidel); mostrarParametrosIterativos(Iteracion.SEIDEL); });
        btnSOR.setOnAction(e -> { marcarBotonActivo(btnSOR); mostrarParametrosIterativos(Iteracion.SOR); });
        btnRelajamiento.setOnAction(e -> { marcarBotonActivo(btnRelajamiento); mostrarParametrosIterativos(Iteracion.RELAJACION); });
        btnMejoramiento.setOnAction(e -> { marcarBotonActivo(btnMejoramiento); mostrarParametrosIterativos(Iteracion.MEJORAMIENTO); });

        panelLateral.getChildren().addAll(
            tituloMenu, new Separator(),
            crearSubtitulo("RESOLUCIÓN DIRECTA"), btnGaussSimple, btnGaussParcial, btnGaussTotal, btnJordan,
            new Separator(), crearSubtitulo("FACTORIZACIONES"), btnFactorizarLU, btnCroutL1U, btnCroutLU1, btnCholeskyL, btnCholeskyU,
            new Separator(), crearSubtitulo("ITERATIVOS"), btnJacobi, btnSeidel, btnSOR, btnRelajamiento, btnMejoramiento
        );

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

        // SOLUCIÓN SPINNER: Volvemos a limpiar pero actualizando primero 'n' y forzando que redibuje
        spinnerN.valueProperty().addListener((obs, viejo, nuevo) -> {
            n = nuevo;
            limpiarPantalla(); // Limpiamos rastros anteriores
            generarCuadricula(); // El método usará el nuevo 'n' para crear los textfields
        });

        Button btnRandom = crearBotonAleatorio("Llenar Aleatorio");
        btnRandom.setOnAction(e -> {
            marcarBotonActivo(null);
            setMetodoActivo("Llenado Aleatorio");
            generarMatrizAleatoria();
        });

        Button btnLimpiar = crearBotonLimpiar("Limpiar Matriz");
        btnLimpiar.setOnAction(e -> {
            for (int i = 0; i < n; i++)
                for (int j = 0; j <= n; j++) casillas[i][j].setText("");
            marcarBotonActivo(null);
            setMetodoActivo(null);
            limpiarPantalla();
        });
        
        Button btnImportar = crearBotonLimpiar("Importar TXT");
        btnImportar.setOnAction(e -> cargarMatrizDesdeTXT(stage));

        Region espaciadorBarra = new Region();
        HBox.setHgrow(espaciadorBarra, Priority.ALWAYS);

        barraControles.getChildren().addAll(lblTamanio, spinnerN, btnRandom, btnLimpiar, espaciadorBarra, btnImportar);

        contenidoGlobal = new VBox(15);
        contenidoGlobal.setAlignment(Pos.TOP_CENTER);
        contenidoGlobal.setPadding(new Insets(20, 40, 80, 40));

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

        // ===== Caja de configuración iterativa =====
        panelParametrosIterativos = new VBox(14);
        panelParametrosIterativos.setAlignment(Pos.CENTER);
        panelParametrosIterativos.setPadding(new Insets(22, 34, 22, 34));
        panelParametrosIterativos.setMaxWidth(460);
        panelParametrosIterativos.setMaxHeight(Region.USE_PREF_SIZE);
        panelParametrosIterativos.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1E1B4B, #2E1065); " +
            "-fx-border-color: " + COLOR_LILA_OSCURO_BORDE + "; " +
            "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14; " +
            "-fx-effect: dropshadow(gaussian, rgba(168,85,247,0.45), 22, 0.35, 0, 8);");

        Label lblConfig = new Label("Configuración Iterativa");
        lblConfig.setTextFill(Color.web(COLOR_LILA_OSCURO_TEXTO));
        lblConfig.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 16));

        Label lblSub = new Label("Ajusta los parámetros y pulsa Ejecutar");
        lblSub.setTextFill(Color.web("#94A3B8"));
        lblSub.setFont(Font.font(FUENTE_UI, FontWeight.NORMAL, 12));

        String estiloLabelCampo = "-fx-text-fill: #E2E8F0; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-font-size: 13px;";
        String estiloInputCampo =
            "-fx-background-color: #0F172A; -fx-text-fill: #F1F5F9; " +
            "-fx-border-color: #4C1D95; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-font-family: '" + FUENTE_MONO + "'; -fx-font-weight: bold; " +
            "-fx-padding: 8 12 8 12; -fx-prompt-text-fill: #64748B;";
        String estiloInputFocus =
            "-fx-background-color: #0F172A; -fx-text-fill: #F1F5F9; " +
            "-fx-border-color: #2DD4BF; -fx-border-width: 2; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-font-family: '" + FUENTE_MONO + "'; -fx-font-weight: bold; " +
            "-fx-padding: 8 12 8 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(45,212,191,0.45), 8, 0.4, 0, 0);";

        HBox hTol = new HBox(12); hTol.setAlignment(Pos.CENTER_LEFT);
        Label lblT = new Label("Tolerancia:"); lblT.setStyle(estiloLabelCampo); lblT.setMinWidth(130);
        tfTol = new TextField("1e-6"); tfTol.setPrefWidth(180); tfTol.setStyle(estiloInputCampo);
        tfTol.focusedProperty().addListener((o, a, f) -> tfTol.setStyle(f ? estiloInputFocus : estiloInputCampo));
        hTol.getChildren().addAll(lblT, tfTol);

        HBox hMax = new HBox(12); hMax.setAlignment(Pos.CENTER_LEFT);
        Label lblM = new Label("Iteraciones máx:"); lblM.setStyle(estiloLabelCampo); lblM.setMinWidth(130);
        tfMax = new TextField("1000"); tfMax.setPrefWidth(180); tfMax.setStyle(estiloInputCampo);
        tfMax.focusedProperty().addListener((o, a, f) -> tfMax.setStyle(f ? estiloInputFocus : estiloInputCampo));
        hMax.getChildren().addAll(lblM, tfMax);

        HBox hOmega = new HBox(12); hOmega.setAlignment(Pos.CENTER_LEFT);
        Label lblO = new Label("Omega (ω):"); lblO.setStyle(estiloLabelCampo); lblO.setMinWidth(130);
        tfOmega = new TextField("1.0"); tfOmega.setPrefWidth(180); tfOmega.setStyle(estiloInputCampo);
        tfOmega.focusedProperty().addListener((o, a, f) -> tfOmega.setStyle(f ? estiloInputFocus : estiloInputCampo));
        hOmega.getChildren().addAll(lblO, tfOmega);

        String btnBase  = "-fx-background-color: " + COLOR_LILA_OSCURO_BORDE + "; -fx-text-fill: white; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28 10 28;";
        String btnHover = "-fx-background-color: #C084FC; -fx-text-fill: white; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28 10 28; -fx-effect: dropshadow(gaussian, rgba(192,132,252,0.6), 12, 0.4, 0, 0);";

        Button btnEjecutarIter = new Button("Ejecutar");
        btnEjecutarIter.setStyle(btnBase);
        btnEjecutarIter.setOnMouseEntered(e -> btnEjecutarIter.setStyle(btnHover));
        btnEjecutarIter.setOnMouseExited (e -> btnEjecutarIter.setStyle(btnBase));
        btnEjecutarIter.setOnAction(e -> {
            if (iteracionPendiente != null) {
                panelParametrosIterativos.setVisible(false);
                panelParametrosIterativos.setManaged(false);
                ejecutarIterativo(iteracionPendiente);
            }
        });

        Button btnCancelarIter = new Button("Cancelar");
        btnCancelarIter.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
            "-fx-border-color: #475569; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-padding: 9 22 9 22; -fx-cursor: hand;");
        btnCancelarIter.setOnAction(e -> {
            panelParametrosIterativos.setVisible(false);
            panelParametrosIterativos.setManaged(false);
            iteracionPendiente = null;
            setMetodoActivo(null);
        });

        HBox hBotones = new HBox(12, btnCancelarIter, btnEjecutarIter);
        hBotones.setAlignment(Pos.CENTER);

        panelParametrosIterativos.getChildren().addAll(lblConfig, lblSub, hTol, hMax, hOmega, hBotones);
        panelParametrosIterativos.setVisible(false);
        panelParametrosIterativos.setManaged(false);

        contenedorResultadosVisuales = new VBox(20);
        contenedorResultadosVisuales.setAlignment(Pos.CENTER);

        vboxPasos = new VBox(15);
        vboxPasos.setPadding(new Insets(15));

        scrollPasos = new ScrollPane(vboxPasos);
        scrollPasos.setFitToWidth(true);
        scrollPasos.setStyle("-fx-background: #1E1E24; -fx-background-color: #1E1E24; -fx-control-inner-background: #1E1E24;");
        scrollPasos.setPrefHeight(500);

        acordeonPasos = new TitledPane("Resolución paso a paso", scrollPasos);
        acordeonPasos.setExpanded(false);
        acordeonPasos.setVisible(false);
        acordeonPasos.setManaged(false);
        acordeonPasos.setStyle(
            "-fx-background-color: #2A2A33; -fx-border-color: " + COLOR_LILA_OSCURO_BORDE + "; " +
            "-fx-border-radius: 10; -fx-background-radius: 10;");

        acordeonPasos.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Label lblTitle = (Label) acordeonPasos.lookup(".title");
                if (lblTitle != null) lblTitle.setStyle(ESTILO_ACORDEON_TITULO);
            }
        });

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

        HBox panelMatrizYResultados = new HBox(30); 
        panelMatrizYResultados.setAlignment(Pos.CENTER);
        panelMatrizYResultados.getChildren().addAll(envolturaMatriz, panelResultado);

        contenidoGlobal.getChildren().addAll(
            lblMetodoActivo,
            tituloMatriz,
            lblAlertaIngreso,
            panelMatrizYResultados,
            contenedorResultadosVisuales,
            acordeonPasos
        );

        // SOLUCIÓN POP-UP (OVERLAY) Y SCROLL: Alineamos explícitamente el popup abajo
        StackPane overlayContainer = new StackPane(contenidoGlobal, panelParametrosIterativos);
        StackPane.setAlignment(panelParametrosIterativos, Pos.BOTTOM_CENTER);
        StackPane.setMargin(panelParametrosIterativos, new Insets(0, 0, 50, 0)); // Un margen desde abajo para que no pegue al borde
        overlayContainer.setStyle("-fx-background-color: transparent;");

        ScrollPane centroScroll = new ScrollPane(overlayContainer);
        centroScroll.setFitToWidth(true);
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

        Scene scene = new Scene(root, 1180, 850);
        scene.setFill(Paleta.color(Paleta.FONDO_GLOBAL));
        stage.setTitle("Sistemas de Ecuaciones Lineales");
        stage.setScene(scene);
        stage.show();
    }

    private void marcarBotonActivo(Button btn) {
        if (botonActivo != null) botonActivo.setStyle(BTN_SIDEBAR_BASE);
        botonActivo = btn;
        if (botonActivo != null) botonActivo.setStyle(BTN_SIDEBAR_ACTIVO);
    }

    private void setMetodoActivo(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            lblMetodoActivo.setText("");
            lblMetodoActivo.setVisible(false);
            lblMetodoActivo.setManaged(false);
        } else {
            lblMetodoActivo.setText(nombre);
            lblMetodoActivo.setVisible(true);
            lblMetodoActivo.setManaged(true);
        }
    }

    private void mostrarParametrosIterativos(Iteracion tipo) {
        iteracionPendiente = tipo;
        limpiarPantalla();

        String nombre = "";
        switch (tipo) {
            case JACOBI: nombre = "Jacobi"; break;
            case SEIDEL: nombre = "Gauss-Seidel"; break;
            case SOR: nombre = "SOR"; break;
            case RELAJACION: nombre = "Relajación"; break;
            case MEJORAMIENTO: nombre = "Mejoramiento Iterativo"; break;
        }
        setMetodoActivo(nombre);

        boolean esSOR = (tipo == Iteracion.SOR);
        tfOmega.getParent().setVisible(esSOR);
        tfOmega.getParent().setManaged(esSOR);

        tfTol.setText("1e-6");
        tfMax.setText("1000");
        tfOmega.setText("1.0");

        panelParametrosIterativos.setVisible(true);
        panelParametrosIterativos.setManaged(true);
        panelParametrosIterativos.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), panelParametrosIterativos);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        Platform.runLater(() -> tfTol.requestFocus());
    }

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

    private void cargarMatrizDesdeTXT(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Matriz TXT");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Texto", "*.txt"));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                String contenido = new String(Files.readAllBytes(file.toPath())).replace(",", ".");
                Scanner scanner = new Scanner(contenido);
                
                int i = 0;
                int j = 0;
                int columnasTotales = n + 1;
                
                while (scanner.hasNext() && i < n) {
                    if (scanner.hasNextDouble()) {
                        double valor = scanner.nextDouble();
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
                        scanner.next(); 
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

    private static String estiloInput(boolean hasText, boolean focused, boolean esB) {
        if (focused) {
            if (hasText) return esB ? IN_FOCUS_LB : IN_FOCUS_L;
            else         return esB ? IN_FOCUS_VB : IN_FOCUS_V;
        } else {
            if (hasText) return esB ? IN_LLENO_B : IN_LLENO;
            else         return esB ? IN_VACIO_B : IN_VACIO;
        }
    }

    private void generarCuadricula() {
        gridMatriz.getChildren().clear();
        int columnasTotales = n + 1;
        casillas = new TextField[n][columnasTotales];

        int cellSize = 60, fontSize = 16, gap = 10;
        if (n > 5)  { cellSize = 45; fontSize = 14; gap = 6; }
        if (n > 8)  { cellSize = 35; fontSize = 12; gap = 4; }
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
        if (timelineActivo != null) {
            timelineActivo.stop();
            timelineActivo = null;
        }
        contenedorResultadosVisuales.getChildren().clear();
        panelResultado.setVisible(false);
        lblAlertaIngreso.setVisible(false);
        lblAlertaIngreso.setManaged(false);
        
        if (casillas != null) {
            int filasActuales = casillas.length;
            int colsActuales = casillas[0].length;
            
            for (int i = 0; i < filasActuales; i++) {
                for (int j = 0; j < colsActuales; j++) {
                    if (casillas[i][j] != null) {
                        casillas[i][j].setStyle(j == colsActuales - 1 ? ESTILO_VECTOR_B : ESTILO_NORMAL);
                    }
                }
            }
        }

        vboxPasos.getChildren().clear();
        acordeonPasos.setVisible(false);
        acordeonPasos.setManaged(false);
        acordeonPasos.setExpanded(false);

        panelParametrosIterativos.setVisible(false);
        panelParametrosIterativos.setManaged(false);
    }

    private TextField crearCeldaResultados() {
        TextField tf = new TextField("");
        tf.setPrefWidth(60);
        tf.setPrefHeight(40);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false);
        tf.setFont(Font.font(FUENTE_MONO, FontWeight.BOLD, 14));
        tf.setStyle(ESTILO_NORMAL);
        return tf;
    }

    private HBox envolverEnParentesis(GridPane grid) {
        double altura = Math.max(60, n * 34);
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
        return btn;
    }

    private Button crearBotonAleatorio(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + Paleta.ACENTO_AMBER + "; -fx-text-fill: " + Paleta.FONDO_GLOBAL + "; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        return btn;
    }

    private Button crearBotonLimpiar(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Paleta.ACENTO_AMBER + "; -fx-border-color: " + Paleta.ACENTO_AMBER + "; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-family: '" + FUENTE_UI + "'; -fx-font-weight: bold; -fx-cursor: hand;");
        return btn;
    }

    private void animarAparicion(Node node) {
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
        
        HBox panelHBox = (HBox) envolturaMatriz.getParent();
        if (panelHBox != null) panelHBox.getChildren().remove(panelResultado);
        contenidoGlobal.getChildren().remove(panelResultado);
        
        if (esVectorX) {
            detalle = detalle.replace("   |   ", "\n");
            lblDetalle.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
            panelResultado.setMaxWidth(200); 
            if (panelHBox != null) panelHBox.getChildren().add(panelResultado);
        } else {
            lblDetalle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            panelResultado.setMaxWidth(600); 
            contenidoGlobal.getChildren().add(panelResultado);
        }
        
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
        
        HBox panelHBox = (HBox) envolturaMatriz.getParent();
        if (panelHBox != null) panelHBox.getChildren().remove(panelResultado);
        contenidoGlobal.getChildren().remove(panelResultado);
        contenidoGlobal.getChildren().add(panelResultado);
        
        panelResultado.setVisible(true);
        animarAparicion(panelResultado);
    }

    private Matriz leerMatrizDesdeInterfaz() {
        int columnas = n + 1;
        double[][] datosIngresados = new double[n][columnas];
        boolean esMatrizNula = true;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnas; j++) {
                String texto = casillas[i][j].getText().trim().replace(",", ".");
                
                // 1. VALIDACIÓN: Casilla en blanco no permitida
                if (texto.isEmpty()) {
                    throw new IllegalArgumentException("La matriz está incompleta. Por favor, rellene todas las celdas.");
                }
                
                // 2. Extracción de datos
                double valor = Double.parseDouble(texto);
                datosIngresados[i][j] = valor;
                
                // Verificamos si al menos UN elemento de los coeficientes es distinto de cero
                if (j < n && Math.abs(valor) > 1e-12) {
                    esMatrizNula = false;
                }
            }
        }
        
        // 3. VALIDACIÓN: Matriz completamente nula (puros ceros)
        if (esMatrizNula) {
            throw new IllegalArgumentException("Sistema Inexistente: La matriz de coeficientes está compuesta únicamente por ceros.");
        }
        
        return new Matriz(datosIngresados);
    }
    private String fmt(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-9) return String.valueOf((long) Math.round(v));
        return String.format("%.3f", v);
    }

    private VBox crearTarjetaPaso(PasoResolucion paso) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPadding(new Insets(18, 22, 18, 22));
        tarjeta.setStyle(ESTILO_TARJETA_PASO);
        tarjeta.setMaxWidth(880);

        StackPane circulo = new StackPane();
        circulo.setPrefSize(28, 28);
        circulo.setStyle("-fx-background-color: #2F2F3A; -fx-background-radius: 14;");
        Label lblNum = new Label(String.valueOf(paso.numero));
        lblNum.setTextFill(Color.web("#C7C7D1"));
        lblNum.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 13));
        circulo.getChildren().add(lblNum);

        Label lblTitulo = new Label(paso.titulo);
        lblTitulo.setTextFill(Color.web("#E4E4EC"));
        lblTitulo.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 15));

        HBox header = new HBox(12, circulo, lblTitulo);
        header.setAlignment(Pos.CENTER_LEFT);
        tarjeta.getChildren().add(header);

        VBox bloqueFormulas = new VBox(6);
        bloqueFormulas.setAlignment(Pos.CENTER_LEFT);
        for (String f : paso.formulas) {
            Label lf = new Label(f);
            lf.setTextFill(Color.web("#B8B8C4"));
            lf.setFont(Font.font(FUENTE_MONO, 14));
            lf.setWrapText(true);
            bloqueFormulas.getChildren().add(lf);
        }

        Node bloqueMatriz = null;
        if (paso.matriz != null) {
            GridPane g = new GridPane();
            g.setHgap(6); g.setVgap(6);
            g.setAlignment(Pos.CENTER);
            for (int i = 0; i < paso.matriz.length; i++) {
                for (int j = 0; j < paso.matriz[i].length; j++) {
                    Label celda = new Label(fmt(paso.matriz[i][j]));
                    celda.setFont(Font.font(FUENTE_MONO, FontWeight.BOLD, 15));
                    celda.setMinWidth(52);
                    celda.setAlignment(Pos.CENTER);
                    celda.setPadding(new Insets(4, 8, 4, 8));
                    if (i == paso.filaResaltada && j == paso.colResaltada) {
                        celda.setStyle("-fx-background-color: " + Paleta.PIVOTE_BG + "; " +
                                       "-fx-border-color: " + Paleta.PIVOTE_BORDE + "; " +
                                       "-fx-border-width: 2; -fx-background-radius: 6; -fx-border-radius: 6;");
                        celda.setTextFill(Color.web("#FDE047"));
                    } else if (i == paso.filaResaltada) {
                        celda.setTextFill(Color.web("#FDE047"));
                        celda.setStyle("-fx-background-color: rgba(253,224,71,0.10); -fx-background-radius: 6;");
                    } else if (j == paso.colResaltada) {
                        celda.setTextFill(Color.web("#5EEAD4"));
                        celda.setStyle("-fx-background-color: rgba(45,212,191,0.10); -fx-background-radius: 6;");
                    } else {
                        celda.setTextFill(Color.web("#E4E4EC"));
                    }
                    g.add(celda, j, i);
                }
            }
            bloqueMatriz = envolverEnParentesis(g);
        }

        HBox cuerpo = new HBox(20);
        cuerpo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bloqueFormulas, Priority.ALWAYS);
        cuerpo.getChildren().add(bloqueFormulas);
        if (bloqueMatriz != null) cuerpo.getChildren().add(bloqueMatriz);
        tarjeta.getChildren().add(cuerpo);

        if (paso.notaAdicional != null) {
            Label nota = new Label(paso.notaAdicional);
            nota.setTextFill(Color.web("#A78BFA"));
            nota.setFont(Font.font(FUENTE_UI, FontWeight.BOLD, 12));
            tarjeta.getChildren().add(nota);
        }
        return tarjeta;
    }

    private void mostrarPasosEnAcordeon(List<PasoResolucion> pasos) {
        vboxPasos.getChildren().clear();
        for (PasoResolucion p : pasos) {
            VBox t = crearTarjetaPaso(p);
            vboxPasos.getChildren().add(t);
            animarAparicion(t);
        }
        acordeonPasos.setVisible(true);
        acordeonPasos.setManaged(true);
        acordeonPasos.setExpanded(true);
    }

    private void ejecutarGauss(int tipoPivoteo) {
        String nombre = (tipoPivoteo == 1) ? "Gauss · Pivoteo Parcial"
                      : (tipoPivoteo == 2) ? "Gauss · Pivoteo Total"
                                           : "Gauss · Pivoteo Simple";
        setMetodoActivo(nombre);
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            double[] solucion = null;
            Exception mathError = null;
            try {
                if (tipoPivoteo == 1)      solucion = MetodosDirectosMatriz.GaussPivoteoParcial(matrizAmp);
                else if (tipoPivoteo == 2) solucion = MetodosDirectosMatriz.GaussPivoteoTotal(matrizAmp);
                else                       solucion = MetodosDirectosMatriz.GaussPivoteoSimple(matrizAmp);
            } catch (Exception ex) {
                mathError = ex;
            }

            int nLocal = matrizAmp.getCantFilas();
            boolean animar = (nLocal <= 5);
            
            if (animar) {
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

    private void animarGaussParcialSimple(Matriz original, int tipoPivoteo, double[] solucion, Exception mathError) {
        int nLocal = original.getCantFilas();
        int cols = nLocal + 1;

        List<double[][]> snapshots  = new ArrayList<>();
        List<String>     labels     = new ArrayList<>();
        List<Integer>    pivotRows1 = new ArrayList<>();
        List<Integer>    pivotRows2 = new ArrayList<>();
        List<Integer>    columnaVerde = new ArrayList<>(); 
        List<PasoResolucion> pasosDoc = new ArrayList<>();
        int contador = 1;

        double[][] sim = new double[nLocal][cols];
        for (int i = 0; i < nLocal; i++)
            for (int j = 0; j < cols; j++) sim[i][j] = original.getElemento(i, j);

        snapshots.add(copiar(sim));
        labels.add("Matriz ampliada inicial");
        pivotRows1.add(-1); pivotRows2.add(-1); columnaVerde.add(-1);

        PasoResolucion p0 = new PasoResolucion(contador++, "Matriz ampliada inicial [A|b]");
        p0.matriz = copiar(sim);
        pasosDoc.add(p0);

        for (int k = 0; k < nLocal - 1; k++) {
            int filaMayor = k;
            double maxValor = Math.abs(sim[k][k]);
            
            for (int i = k + 1; i < nLocal; i++) {
                double va = Math.abs(sim[i][k]);
                if ((tipoPivoteo == 1 && va > maxValor) ||
                    (tipoPivoteo == 3 && maxValor < 1e-12 && va > 1e-12)) {
                    maxValor = va;
                    filaMayor = i;
                }
            }

            if (filaMayor != k) {
                snapshots.add(copiar(sim));
                labels.add("Pivoteo: Filas a intercambiar F" + (k + 1) + " y F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor); columnaVerde.add(k);

                for (int j = 0; j < cols; j++) {
                    double tmp = sim[k][j];
                    sim[k][j] = sim[filaMayor][j];
                    sim[filaMayor][j] = tmp;
                }
                
                snapshots.add(copiar(sim));
                labels.add("Pivoteo: Intercambio F" + (k + 1) + " ↔ F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor); columnaVerde.add(k);
                
                PasoResolucion pp = new PasoResolucion(contador++, "Pivoteo · Intercambio F" + (k+1) + " ↔ F" + (filaMayor+1));
                pp.formulas.add("Nuevo pivote: a[" + (k+1) + "][" + (k+1) + "] = " + fmt(sim[k][k]));
                pp.matriz = copiar(sim);
                pp.filaResaltada = k; pp.colResaltada = k;
                pasosDoc.add(pp);
            }

            if (Math.abs(sim[k][k]) > 1e-12) {
                for (int i = k + 1; i < nLocal; i++) {
                    double m = sim[i][k] / sim[k][k];
                    double aAntes = sim[i][k];
                    double pivVal = sim[k][k];
                    for (int j = k; j < cols; j++) sim[i][j] -= m * sim[k][j];
                    
                    PasoResolucion pe = new PasoResolucion(contador++, "Eliminación · F" + (i+1) + " ← F" + (i+1) + " − (" + fmt(m) + ")·F" + (k+1));
                    pe.formulas.add("m" + (i+1) + (k+1) + " = " + fmt(aAntes) + " / " + fmt(pivVal) + " = " + fmt(m));
                    pe.matriz = copiar(sim);
                    pe.filaResaltada = k; pe.colResaltada = k;
                    pasosDoc.add(pe);
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": eliminación bajo pivote F" + (k + 1));
                pivotRows1.add(-1); pivotRows2.add(-1); columnaVerde.add(-1);
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
            final int pCol = columnaVerde.get(s);

            KeyFrame frame = new KeyFrame(Duration.seconds(stepDelay * s), e -> {
                lblPaso.setText(lbl);
                for (int i = 0; i < nLocal; i++) {
                    for (int j = 0; j < cols; j++) {
                        celdas[i][j].setText(String.format("%.2f", snap[i][j]));

                        if (pRow1 != -1 && (i == pRow1 || i == pRow2)) {
                            celdas[i][j].setStyle(ESTILO_PIVOTE); 
                        } else if (pCol != -1 && j == pCol) {
                            celdas[i][j].setStyle(ESTILO_COL_VERDE_AGUA); 
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
            if (mathError != null) {
                mostrarError("Error Matemático", mathError.getMessage());
            } else {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            }
            mostrarPasosEnAcordeon(pasosDoc);
        });
        timeline.getKeyFrames().add(fin);
        timelineActivo = timeline;
        timeline.play();
    }

    private void animarGaussTotal(Matriz original, double[] solucion, Exception mathError) {
        int nLocal = original.getCantFilas();
        int cols = nLocal + 1;

        List<double[][]> snapshots  = new ArrayList<>();
        List<String>     labels     = new ArrayList<>();
        List<Integer>    pivotRows1 = new ArrayList<>();
        List<Integer>    pivotRows2 = new ArrayList<>();
        List<Integer>    pivotCols1 = new ArrayList<>();
        List<Integer>    pivotCols2 = new ArrayList<>();
        List<PasoResolucion> pasosDoc = new ArrayList<>();
        int contador = 1;

        double[][] sim = new double[nLocal][cols];
        for (int i = 0; i < nLocal; i++)
            for (int j = 0; j < cols; j++) sim[i][j] = original.getElemento(i, j);

        snapshots.add(copiar(sim));
        labels.add("Matriz ampliada inicial");
        pivotRows1.add(-1); pivotRows2.add(-1); pivotCols1.add(-1); pivotCols2.add(-1);
        
        PasoResolucion p0 = new PasoResolucion(contador++, "Matriz ampliada inicial [A|b]");
        p0.matriz = copiar(sim);
        pasosDoc.add(p0);

        for (int k = 0; k < nLocal - 1; k++) {
            int filaMayor = k, colMayor = k;
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
                pivotRows1.add(k); pivotRows2.add(filaMayor); pivotCols1.add(-1); pivotCols2.add(-1);

                for (int j = 0; j < cols; j++) {
                    double tmp = sim[k][j]; sim[k][j] = sim[filaMayor][j]; sim[filaMayor][j] = tmp;
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": intercambio F" + (k + 1) + " ↔ F" + (filaMayor + 1));
                pivotRows1.add(k); pivotRows2.add(filaMayor); pivotCols1.add(-1); pivotCols2.add(-1);
            }

            if (colMayor != k) {
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": columnas seleccionadas C" + (k + 1) + " ↔ C" + (colMayor + 1));
                pivotRows1.add(-1); pivotRows2.add(-1); pivotCols1.add(k); pivotCols2.add(colMayor);

                for (int i = 0; i < nLocal; i++) {
                    double tmp = sim[i][k]; sim[i][k] = sim[i][colMayor]; sim[i][colMayor] = tmp;
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": intercambio C" + (k + 1) + " ↔ C" + (colMayor + 1));
                pivotRows1.add(-1); pivotRows2.add(-1); pivotCols1.add(k); pivotCols2.add(colMayor);
            }
            
            if (filaMayor != k || colMayor != k) {
                PasoResolucion pp = new PasoResolucion(contador++, "Pivoteo total · Pivote máximo en (" + (filaMayor+1) + "," + (colMayor+1) + ")");
                pp.formulas.add("Pivote total = " + fmt(sim[k][k]));
                pp.matriz = copiar(sim);
                pp.filaResaltada = k; pp.colResaltada = k;
                pasosDoc.add(pp);
            }

            if (Math.abs(sim[k][k]) > 1e-12) {
                for (int i = k + 1; i < nLocal; i++) {
                    double m = sim[i][k] / sim[k][k];
                    double aAntes = sim[i][k];
                    double pivVal = sim[k][k];
                    for (int j = k; j < cols; j++) sim[i][j] -= m * sim[k][j];
                    
                    PasoResolucion pe = new PasoResolucion(contador++, "Eliminación · F" + (i+1) + " ← F" + (i+1) + " − (" + fmt(m) + ")·F" + (k+1));
                    pe.formulas.add("m" + (i+1) + (k+1) + " = " + fmt(aAntes) + " / " + fmt(pivVal) + " = " + fmt(m));
                    pe.matriz = copiar(sim);
                    pe.filaResaltada = k; pe.colResaltada = k;
                    pasosDoc.add(pe);
                }
                snapshots.add(copiar(sim));
                labels.add("Paso " + (k + 1) + ": eliminación bajo pivote F" + (k + 1));
                pivotRows1.add(-1); pivotRows2.add(-1); pivotCols1.add(-1); pivotCols2.add(-1);
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
                            if (i == pRow1 || i == pRow2) celdas[i][j].setStyle(ESTILO_PIVOTE);
                            else if (j == cols - 1) celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            else celdas[i][j].setStyle(ESTILO_NORMAL);
                        } else if (pCol1 != -1 && pCol2 != -1) {
                            if (j == pCol1 || j == pCol2) celdas[i][j].setStyle(ESTILO_COL_VERDE_AGUA);
                            else if (j == cols - 1) celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            else celdas[i][j].setStyle(ESTILO_NORMAL);
                        } else {
                            if (j == cols - 1) celdas[i][j].setStyle(ESTILO_VECTOR_B);
                            else celdas[i][j].setStyle(ESTILO_NORMAL);
                        }
                    }
                }
            });
            timeline.getKeyFrames().add(frame);
        }

        double finalDelay = stepDelay * snapshots.size();
        KeyFrame fin = new KeyFrame(Duration.seconds(finalDelay), e -> {
            lblPaso.setText("Triangulación finalizada");
            if (mathError != null) {
                mostrarError("Error Matemático", mathError.getMessage());
            } else {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
            }
            mostrarPasosEnAcordeon(pasosDoc);
        });
        timeline.getKeyFrames().add(fin);
        timelineActivo = timeline;
        timeline.play();
    }

    private void ejecutarJordan() {
        setMetodoActivo("Gauss-Jordan");
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            int nLocal = matrizAmp.getCantFilas();
            int cols = nLocal + 1;

            double[][] M = new double[nLocal][cols];
            for (int i = 0; i < nLocal; i++)
                for (int j = 0; j < cols; j++)
                    M[i][j] = matrizAmp.getElemento(i, j);

            List<PasoResolucion> pasos = new ArrayList<>();
            int contador = 1;

            PasoResolucion p0 = new PasoResolucion(contador++, "Matriz ampliada inicial [A|b]");
            p0.matriz = copiar(M);
            pasos.add(p0);

            for (int k = 0; k < nLocal; k++) {
                int piv = k;
                for (int i = k + 1; i < nLocal; i++)
                    if (Math.abs(M[i][k]) > Math.abs(M[piv][k])) piv = i;

                if (piv != k) {
                    double[] tmp = M[k]; M[k] = M[piv]; M[piv] = tmp;
                    PasoResolucion pp = new PasoResolucion(contador++, "Pivoteo parcial · Intercambio F" + (k+1) + " ↔ F" + (piv+1));
                    pp.formulas.add("Nuevo pivote: a[" + (k+1) + "][" + (k+1) + "] = " + fmt(M[k][k]));
                    pp.matriz = copiar(M);
                    pp.filaResaltada = k; pp.colResaltada = k;
                    pasos.add(pp);
                }

                if (Math.abs(M[k][k]) < 1e-14) {
                    mostrarError("Matriz singular", "El pivote en (" + (k+1) + "," + (k+1) + ") es cero.");
                    mostrarPasosEnAcordeon(pasos);
                    return;
                }

                for (int i = k + 1; i < nLocal; i++) {
                    if (Math.abs(M[i][k]) < 1e-14) continue;
                    double m = M[i][k] / M[k][k];
                    for (int j = k; j < cols; j++) M[i][j] -= m * M[k][j];

                    PasoResolucion pe = new PasoResolucion(contador++, "Eliminación · F" + (i+1) + " ← F" + (i+1) + " − (" + fmt(m) + ")·F" + (k+1));
                    pe.matriz = copiar(M);
                    pe.filaResaltada = k; pe.colResaltada = k;
                    pasos.add(pe);
                }
            }

            for (int k = nLocal - 1; k >= 0; k--) {
                double pivote = M[k][k];
                if (Math.abs(pivote) < 1e-14) {
                    mostrarError("Matriz singular", "El pivote en (" + (k+1) + "," + (k+1) + ") es cero.");
                    mostrarPasosEnAcordeon(pasos);
                    return;
                }
                double inv = 1.0 / pivote;
                for (int j = 0; j < cols; j++) M[k][j] *= inv;

                PasoResolucion pn = new PasoResolucion(contador++, "Normalización · F" + (k+1) + " ← F" + (k+1) + " / (" + fmt(pivote) + ")");
                pn.matriz = copiar(M);
                pn.filaResaltada = k; pn.colResaltada = k;
                pasos.add(pn);

                for (int i = k - 1; i >= 0; i--) {
                    if (Math.abs(M[i][k]) < 1e-14) continue;
                    double m = M[i][k];
                    for (int j = 0; j < cols; j++) M[i][j] -= m * M[k][j];

                    PasoResolucion pu = new PasoResolucion(contador++, "Eliminación hacia arriba · F" + (i+1) + " ← F" + (i+1) + " − (" + fmt(m) + ")·F" + (k+1));
                    pu.matriz = copiar(M);
                    pu.filaResaltada = k; pu.colResaltada = k;
                    pasos.add(pu);
                }
            }

            double[] sol = new double[nLocal];
            for (int i = 0; i < nLocal; i++) sol[i] = M[i][cols - 1];

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sol.length; i++) {
                sb.append("x").append(i + 1).append(" = ").append(String.format("%.4f", sol[i]));
                if (i < sol.length - 1) sb.append("   |   ");
            }
            mostrarExito("Gauss-Jordan", sb.toString() + "\nDiagonal reducida a la identidad.", true);
            mostrarPasosEnAcordeon(pasos);

        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private void ejecutarLU() {
        setMetodoActivo("Factorización LU");
        limpiarPantalla();
        try {
            Matriz matrizA = leerMatrizDesdeInterfaz().extraerA();
            MetodosDirectosMatriz.ResultadoLU res = MetodosDirectosMatriz.factorizar_x_gauss(matrizA, 1);
            animarDobleMatriz(res.L, res.U, "L", "U", "Factorización LU", res.huboPermutacion, null);
        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private double[][] matrizArray(Matriz m) {
        int f = m.getCantFilas();
        int c = m.getCantColumnas();
        double[][] r = new double[f][c];
        for (int i = 0; i < f; i++)
            for (int j = 0; j < c; j++)
                r[i][j] = m.getElemento(i, j);
        return r;
    }

    private double[][] combinarLU(double[][] L, double[][] U, boolean mostrarU) {
        int nLocal = L.length;
        double[][] r = new double[nLocal][nLocal];
        for (int i = 0; i < nLocal; i++) {
            for (int j = 0; j < nLocal; j++) {
                if (mostrarU) r[i][j] = (i <= j) ? U[i][j] : L[i][j];
                else          r[i][j] = (i > j) ? L[i][j] : U[i][j];
            }
        }
        return r;
    }

    private void ejecutarCrout(boolean esL1U) {
        setMetodoActivo(esL1U ? "Crout · L1 · U" : "Crout · L · U1");
        limpiarPantalla();
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            Matriz matrizA = matrizAmp.extraerA();
            
            double[] solucion = esL1U ? MetodosDirectosMatriz.CroutSolucionL1U(matrizAmp) : MetodosDirectosMatriz.CroutSolucionLU1(matrizAmp);

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
            double[] solucion = esL ? MetodosDirectosMatriz.CholeskySolucionL(matrizAmp)
                                    : MetodosDirectosMatriz.CholeskySolucionU(matrizAmp);

            int nLocal = matrizAmp.getCantFilas();
            double[][] A = new double[nLocal][nLocal];
            for (int i = 0; i < nLocal; i++)
                for (int j = 0; j < nLocal; j++) A[i][j] = matrizAmp.getElemento(i, j);

            List<PasoResolucion> pasos = new ArrayList<>();
            int cnt = 1;
            PasoResolucion p0 = new PasoResolucion(cnt++, "Matriz A (simétrica definida positiva)");
            p0.matriz = A;
            pasos.add(p0);

            double[][] L = new double[nLocal][nLocal];
            for (int i = 0; i < nLocal; i++) {
                for (int j = 0; j <= i; j++) {
                    double suma = 0;
                    for (int k = 0; k < j; k++) suma += L[i][k] * L[j][k];
                    if (i == j) {
                        double val = Math.sqrt(A[i][i] - suma);
                        L[i][j] = val;
                        PasoResolucion p = new PasoResolucion(cnt++, "Diagonal L" + (i+1) + (i+1));
                        p.matriz = copiar(L);
                        p.filaResaltada = i; p.colResaltada = i;
                        pasos.add(p);
                    } else {
                        double val = (A[i][j] - suma) / L[j][j];
                        L[i][j] = val;
                        PasoResolucion p = new PasoResolucion(cnt++, "Elemento L" + (i+1) + (j+1));
                        p.matriz = copiar(L);
                        p.filaResaltada = i; p.colResaltada = j;
                        pasos.add(p);
                    }
                }
            }

            GridPane gridL = new GridPane();
            gridL.setAlignment(Pos.CENTER);
            gridL.setHgap(4); gridL.setVgap(4);

            GridPane gridA = new GridPane();
            gridA.setAlignment(Pos.CENTER);
            gridA.setHgap(4); gridA.setVgap(4);

            TextField[][] celdasA = new TextField[nLocal][nLocal];
            TextField[][] celdasL = new TextField[nLocal][nLocal];

            for (int i = 0; i < nLocal; i++) {
                for (int j = 0; j < nLocal; j++) {
                    celdasA[i][j] = crearCeldaResultados();
                    celdasA[i][j].setText(String.format("%.2f", A[i][j]));
                    celdasA[i][j].setStyle(ESTILO_NORMAL);
                    gridA.add(celdasA[i][j], j, i);

                    celdasL[i][j] = crearCeldaResultados();
                    celdasL[i][j].setText("0.00");
                    celdasL[i][j].setStyle(ESTILO_CELDA_VACIA_RES);
                    gridL.add(celdasL[i][j], j, i);
                }
            }

            Label lblFlecha = new Label(" ➡ ");
            lblFlecha.setFont(Font.font(FUENTE_UI, 30));
            lblFlecha.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));

            String etiquetaMatriz = esL ? "L" : "U";
            String etiquetaFuente = esL ? "A = L · Lᵀ" : "A = Uᵀ · U";

            HBox filaVisual = new HBox(15,
                new VBox(5, crearSubtitulo(etiquetaFuente), envolverEnParentesis(gridA)),
                lblFlecha,
                new VBox(5, crearSubtitulo(etiquetaMatriz), envolverEnParentesis(gridL))
            );
            filaVisual.setAlignment(Pos.CENTER);
            contenedorResultadosVisuales.getChildren().add(filaVisual);
            animarAparicion(filaVisual);

            Timeline timeline = new Timeline();
            double delaySegundos = 0.0;

            for (int i = 0; i < nLocal; i++) {
                for (int j = 0; j <= i; j++) {
                    final int fila = i;
                    final int col = j;
                    KeyFrame frame = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                        celdasL[fila][col].setText(String.format("%.3f", L[fila][col]));
                        celdasL[fila][col].setStyle(ESTILO_L);
                    });
                    timeline.getKeyFrames().add(frame);
                    delaySegundos += 0.15;
                }
            }

            KeyFrame fin = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                mostrarExito("Sistema Resuelto", formatSolucion(solucion), true);
                mostrarPasosEnAcordeon(pasos);
            });
            timeline.getKeyFrames().add(fin);
            timelineActivo = timeline;
            timeline.play();

        } catch (Exception ex) {
            mostrarError("Error de Cholesky", ex.getMessage());
        }
    }

    private void ejecutarIterativo(Iteracion tipo) {
        limpiarPantalla();

        String nombre = "";
        switch (tipo) {
            case JACOBI: nombre = "Jacobi"; break;
            case SEIDEL: nombre = "Gauss-Seidel"; break;
            case SOR: nombre = "SOR"; break;
            case RELAJACION: nombre = "Relajación"; break;
            case MEJORAMIENTO: nombre = "Mejoramiento Iterativo"; break;
        }
        setMetodoActivo(nombre);

        try {
            double tolerancia = 1e-6;
            int maxIter = 1000;
            double omega = 1.0;

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
                    mostrarError("Error de Convergencia",
                        "El elemento a[" + (i + 1) + "][" + (i + 1) + "] es 0.\nLos métodos iterativos requieren que la diagonal no tenga ceros.");
                    return;
                }
            }

            double[] x0 = new double[n];
            double[] solucion = new double[n];

            switch (tipo) {
                case JACOBI: solucion = MetodosIterativosMatriz.JacobiSolucion(matrizAmp, x0, tolerancia, maxIter); break;
                case SEIDEL: solucion = MetodosIterativosMatriz.GaussSeidelSolucion(matrizAmp, x0, tolerancia, maxIter); break;
                case SOR: solucion = MetodosIterativosMatriz.SORSolucion(matrizAmp, x0, omega, tolerancia, maxIter); break;
                case RELAJACION: solucion = MetodosIterativosMatriz.Relajacion(matrizAmp, x0, tolerancia, maxIter); break;
                case MEJORAMIENTO: solucion = MetodosIterativosMatriz.MejoramientoIterativo(matrizAmp, x0, tolerancia, maxIter); break;
            }

            for (double val : solucion) {
                if (Double.isNaN(val) || Double.isInfinite(val)) {
                    mostrarError("Error de Convergencia",
                        "El método divergió (NaN).\nVerifique que la matriz sea diagonalmente dominante.");
                    return;
                }
            }

            List<PasoResolucion> pasos = new ArrayList<>();
            int iterCount = simularIteraciones(matrizAmp, x0, tolerancia, maxIter, omega, tipo, pasos);

            mostrarExito(nombre + " (" + iterCount + " iter)", formatSolucion(solucion), true);
            mostrarPasosEnAcordeon(pasos);

        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }

    private int simularIteraciones(Matriz matriz, double[] x0, double tol, int maxIter,
                                   double omega, Iteracion tipo, List<PasoResolucion> pasos) {
        int nLoc = matriz.getCantFilas();
        double[] x = x0.clone();
        int iter = 0;
        boolean convergio = false;

        while (iter < maxIter && !convergio) {
            iter++;
            double[] xNew = x.clone();
            double maxDiff = 0;

            PasoResolucion paso = new PasoResolucion(iter, "Iteración " + iter);
            boolean usaNuevos = (tipo == Iteracion.SEIDEL || tipo == Iteracion.SOR
                              || tipo == Iteracion.RELAJACION || tipo == Iteracion.MEJORAMIENTO);

            for (int i = 0; i < nLoc; i++) {
                double sumA = 0;
                StringBuilder desglose = new StringBuilder();

                for (int j = 0; j < nLoc; j++) {
                    if (i == j) continue;
                    double xj = usaNuevos ? xNew[j] : x[j];
                    sumA += matriz.getElemento(i, j) * xj;
                    if (desglose.length() > 0) desglose.append(" + ");
                    desglose.append(fmt(matriz.getElemento(i, j))).append("·").append(fmt(xj));
                }
                if (desglose.length() == 0) desglose.append("0");

                double b = matriz.getElemento(i, nLoc);
                double aii = matriz.getElemento(i, i);
                double valGauss = (b - sumA) / aii;
                double val;

                if (tipo == Iteracion.SOR) {
                    val = (1 - omega) * x[i] + omega * valGauss;
                    paso.formulas.add(String.format("x%d = %s", i + 1, fmt(val)));
                } else {
                    val = valGauss;
                    paso.formulas.add(String.format("x%d = %s", i + 1, fmt(val)));
                }

                xNew[i] = val;
                maxDiff = Math.max(maxDiff, Math.abs(xNew[i] - x[i]));
            }

            x = xNew.clone();
            convergio = maxDiff < tol;
            paso.notaAdicional = String.format("Error: %.2e %s", maxDiff, convergio ? "· CONVERGIÓ" : "");
            pasos.add(paso);
        }
        return iter;
    }

    private void animarDobleMatriz(Matriz M1, Matriz M2, String nom1, String nom2,
                                   String nombreMetodo, boolean huboPermutacion, double[] solucion) {
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

        // --- APILAMIENTO DINÁMICO SEGÚN TAMAÑO 'n' ---
        Pane filaMatrices;
        
        if (n > 5) {
            // VERTICAL (VBox) para matrices grandes
            Label lblFlechaV = new Label("⬇"); 
            lblFlechaV.setFont(Font.font(FUENTE_UI, 30)); 
            lblFlechaV.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
            
            Label lblPorV = new Label("✖"); 
            lblPorV.setFont(Font.font(FUENTE_UI, 30)); 
            lblPorV.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));

            filaMatrices = new VBox(15,
                new VBox(5, crearSubtitulo("A"), envolverEnParentesis(gridCopia)),
                lblFlechaV,
                new VBox(5, crearSubtitulo(nom1), envolverEnParentesis(gridM1)),
                lblPorV,
                new VBox(5, crearSubtitulo(nom2), envolverEnParentesis(gridM2))
            );
            ((VBox) filaMatrices).setAlignment(Pos.CENTER);
            
        } else {
            // HORIZONTAL (HBox) para matrices chicas
            Label lblFlecha = new Label(" ➡ "); 
            lblFlecha.setFont(Font.font(FUENTE_UI, 30)); 
            lblFlecha.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));
            
            Label lblPor = new Label(" × "); 
            lblPor.setFont(Font.font(FUENTE_UI, 30)); 
            lblPor.setTextFill(Paleta.color(Paleta.TEXTO_SECUNDARIO));

            filaMatrices = new HBox(15,
                new VBox(5, crearSubtitulo("A"), envolverEnParentesis(gridCopia)),
                lblFlecha,
                new VBox(5, crearSubtitulo(nom1), envolverEnParentesis(gridM1)),
                lblPor,
                new VBox(5, crearSubtitulo(nom2), envolverEnParentesis(gridM2))
            );
            ((HBox) filaMatrices).setAlignment(Pos.CENTER);
        }
        
        contenedorResultadosVisuales.getChildren().add(filaMatrices);
        animarAparicion(filaMatrices);

        String msgExito = "Descomposición finalizada.\nSe extrajeron las matrices " + nom1 + " y " + nom2 + ".";
        if (huboPermutacion) msgExito += "\n(Permutación: PA = " + nom1 + " · " + nom2 + ")";

        List<PasoResolucion> pasos = new ArrayList<>();
        int cnt = 1;
        double[][] L = matrizArray(M1);
        double[][] U = matrizArray(M2);

        PasoResolucion p0 = new PasoResolucion(cnt++, "Descomposición " + nombreMetodo);
        p0.formulas.add("A = " + nom1 + " · " + nom2);
        pasos.add(p0);

        for (int k = 0; k < n; k++) {
            PasoResolucion pFila = new PasoResolucion(cnt++, "Fila " + (k + 1) + " de " + nom2);
            pFila.matriz = combinarLU(L, U, true);
            pFila.filaResaltada = k;
            pasos.add(pFila);

            if (k < n - 1) {
                PasoResolucion pCol = new PasoResolucion(cnt++, "Columna " + (k + 1) + " de " + nom1);
                pCol.matriz = combinarLU(L, U, false);
                pCol.colResaltada = k;
                pasos.add(pCol);
            }
        }

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
            mostrarPasosEnAcordeon(pasos);
        } else {
            Timeline timeline = new Timeline();
            double delaySegundos = 0.0;

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
                mostrarPasosEnAcordeon(pasos);
            });
            timeline.getKeyFrames().add(fin);
            timelineActivo = timeline;
            timeline.play();
        }
    }

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
}
