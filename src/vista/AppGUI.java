package vista;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import modelo.Matriz;
import servicios.MetodosDirectosMatriz;
import javafx.scene.input.KeyCode;

public class AppGUI extends Application {

    private GridPane gridMatriz;
    private TextField[][] casillas;
    private int n = 3; 
    
    // Paleta de colores 
    private final String ESTILO_L = "-fx-background-color: #d6eaf8; -fx-border-color: #3498db; -fx-background-radius: 5; -fx-text-fill: #2980b9; -fx-font-weight: bold;"; 
    private final String ESTILO_U = "-fx-background-color: #d5f5e3; -fx-border-color: #2ecc71; -fx-background-radius: 5; -fx-text-fill: #27ae60; -fx-font-weight: bold;"; 
    private final String ESTILO_PIVOTE = "-fx-background-color: #fcf3cf; -fx-border-color: #f1c40f; -fx-background-radius: 5;"; 
    private final String ESTILO_NORMAL = "-fx-background-color: #ffffff; -fx-border-color: #ecf0f1; -fx-background-radius: 5;";
    private final String ESTILO_VECTOR_B = "-fx-background-color: #e8f6f3; -fx-border-color: transparent transparent transparent #1abc9c; -fx-border-width: 0 0 0 2; -fx-background-radius: 5;";
    
    private VBox panelResultado;
    private Label lblEstado;
    private Label lblDetalle;
    
    private Label lblAlertaIngreso;
    private VBox contenedorResultadosVisuales;
    private ScrollPane scrollGlobal;
    
    private VBox contenidoGlobal;
    private HBox envolturaMatriz;
    private Label parentesisIzq;
    private Label parentesisDer; 
    
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
        panelLateral.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 0 20 20 0;");
        panelLateral.setPrefWidth(270);

        Label tituloMenu = new Label("SISTEMAS LINEALES");
        tituloMenu.setTextFill(Color.WHITE);
        tituloMenu.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox panelTamanio = new HBox(10);
        panelTamanio.setAlignment(Pos.CENTER_LEFT);
        Label lblTamanio = new Label("Tamaño n:");
        lblTamanio.setTextFill(Color.WHITE);
        
        Spinner<Integer> spinnerN = new Spinner<>(2, Matriz.MAX_DIMENSION, n);
        spinnerN.setPrefWidth(70);
        spinnerN.setEditable(true);

        spinnerN.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    int val = Integer.parseInt(spinnerN.getEditor().getText());
                    if (val >= 2 && val <= Matriz.MAX_DIMENSION) {
                        spinnerN.getValueFactory().setValue(val);
                    }
                } catch (NumberFormatException ex) {
                    spinnerN.getEditor().setText(spinnerN.getValue().toString()); 
                }
            }
        });

        // SOLUCIÓN SPINNER: Redibujamos siempre la grilla al cambiar n
        spinnerN.valueProperty().addListener((obs, viejo, nuevo) -> {
            n = nuevo;
            limpiarPantalla(); 
            generarCuadricula(); // <-- Esto había desaparecido y rompió el comportamiento
        });
        panelTamanio.getChildren().addAll(lblTamanio, spinnerN);

        Button btnGaussSimple = crearBotonUI("Gauss (Pivoteo Simple)");
        Button btnGaussParcial = crearBotonUI("Gauss (Pivoteo Parcial)");
        Button btnGaussTotal = crearBotonUI("Gauss (Pivoteo Total)");
        Button btnJordan = crearBotonUI("Gauss-Jordan");
        Button btnFactorizarLU = crearBotonUI("Factorización LU");
        Button btnCroutL1U = crearBotonUI("Crout (L1 U)");
        Button btnCroutLU1 = crearBotonUI("Crout (L U1)");
        Button btnCholeskyL = crearBotonUI("Cholesky (L L^T)");
        Button btnCholeskyU = crearBotonUI("Cholesky (U^T U)");
        Button btnJacobi = crearBotonUI("Jacobi");
        Button btnSeidel = crearBotonUI("Gauss-Seidel");
        Button btnSOR = crearBotonUI("SOR");
        Button btnRelajamiento = crearBotonUI("Relajamiento");
        Button btnMejoramiento = crearBotonUI("Mejoramiento Iterativo");

        // EVENTOS MATEMÁTICOS
        btnGaussSimple.setOnAction(e -> ejecutarGauss(3));
        btnGaussParcial.setOnAction(e -> ejecutarGauss(1));
        btnGaussTotal.setOnAction(e -> ejecutarGauss(2));
        
        btnFactorizarLU.setOnAction(e -> {
            limpiarPantalla();
            try {
                Matriz matrizA = leerMatrizDesdeInterfaz().extraerA();
                MetodosDirectosMatriz.ResultadoLU res = MetodosDirectosMatriz.factorizar_x_gauss(matrizA, 1);
                animarFactorizacion(res.L, res.U, "Factorización LU", res.huboPermutacion);
            } catch (Exception ex) {
                mostrarError("Error Matemático", ex.getMessage());
            }
        });
        
        btnJordan.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnCroutL1U.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnCroutLU1.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnCholeskyL.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnCholeskyU.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnJacobi.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnSeidel.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnSOR.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnRelajamiento.setOnAction(e -> mostrarAdvertencia("En construcción..."));
        btnMejoramiento.setOnAction(e -> mostrarAdvertencia("En construcción..."));

        panelLateral.getChildren().addAll(
            tituloMenu, panelTamanio, new Separator(),
            crearSubtitulo("RESOLUCIÓN DIRECTA"), btnGaussSimple, btnGaussParcial, btnGaussTotal, btnJordan,
            new Separator(), crearSubtitulo("FACTORIZACIONES"), btnFactorizarLU, btnCroutL1U, btnCroutLU1, btnCholeskyL, btnCholeskyU,
            new Separator(), crearSubtitulo("ITERATIVOS"), btnJacobi, btnSeidel, btnSOR, btnRelajamiento, btnMejoramiento
        );

        // ==========================================
        // 2. PANEL DERECHO GLOBAL 
        // ==========================================
        contenidoGlobal = new VBox(30); 
        contenidoGlobal.setAlignment(Pos.TOP_CENTER); 
        contenidoGlobal.setPadding(new Insets(40, 20, 40, 20));
        contenidoGlobal.setStyle("-fx-background-color: #f4f6f7;");

        Label tituloMatriz = new Label("Ingrese la Matriz Ampliada");
        tituloMatriz.setFont(Font.font("System", FontWeight.BOLD, 26));
        tituloMatriz.setTextFill(Color.web("#2c3e50"));

        lblAlertaIngreso = new Label("⚠️ Carácter inválido: Solo números, signos o puntos.");
        lblAlertaIngreso.setTextFill(Color.web("#e74c3c"));
        lblAlertaIngreso.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblAlertaIngreso.setVisible(false);

        envolturaMatriz = new HBox(-5); 
        envolturaMatriz.setAlignment(Pos.CENTER);
        
        parentesisIzq = new Label("(");
        parentesisIzq.setTextFill(Color.web("#7f8c8d"));
        parentesisDer = new Label(")");
        parentesisDer.setTextFill(Color.web("#7f8c8d"));

        gridMatriz = new GridPane();
        gridMatriz.setAlignment(Pos.CENTER);
        gridMatriz.setHgap(10);
        gridMatriz.setVgap(10);
        
        envolturaMatriz.getChildren().addAll(parentesisIzq, gridMatriz, parentesisDer);
        
        // --- Contenedores Inferiores (Resultados) ---
        contenedorResultadosVisuales = new VBox(20);
        contenedorResultadosVisuales.setAlignment(Pos.CENTER);
        
        panelResultado = new VBox(5);
        panelResultado.setAlignment(Pos.CENTER);
        panelResultado.setPadding(new Insets(15, 20, 15, 20));
        panelResultado.setStyle("-fx-background-radius: 10;");
        panelResultado.setMaxWidth(600); 
        panelResultado.setVisible(false); 

        lblEstado = new Label();
        lblEstado.setFont(Font.font("System", FontWeight.BOLD, 15)); 
        lblDetalle = new Label();
        lblDetalle.setFont(Font.font("Monospaced", 13)); 
        lblDetalle.setWrapText(true);
        panelResultado.getChildren().addAll(lblEstado, lblDetalle);

        generarCuadricula(); 
        
        contenidoGlobal.getChildren().addAll(
            tituloMatriz, 
            lblAlertaIngreso, 
            envolturaMatriz, 
            contenedorResultadosVisuales, 
            panelResultado
        );

        scrollGlobal = new ScrollPane(contenidoGlobal);
        scrollGlobal.setFitToWidth(true); 
        scrollGlobal.setFitToHeight(false); 
        scrollGlobal.setStyle("-fx-background-color: transparent; -fx-background: #f4f6f7;");
        scrollGlobal.setBorder(Border.EMPTY);

        BorderPane root = new BorderPane();
        root.setLeft(panelLateral);
        root.setCenter(scrollGlobal);

        Scene scene = new Scene(root, 1100, 750);
        stage.setTitle("Resolutor de Sistemas - TPI");
        stage.setScene(scene);
        stage.show();
    }

    // ==========================================
    // MÉTODOS VISUALES
    // ==========================================

    private void generarCuadricula() {
        gridMatriz.getChildren().clear();
        int columnasTotales = n + 1; 
        casillas = new TextField[n][columnasTotales];

        Label lblA = new Label("A");
        lblA.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblA.setTextFill(Color.web("#7f8c8d"));
        lblA.setAlignment(Pos.CENTER);
        lblA.setMaxWidth(Double.MAX_VALUE);

        Label lblB = new Label("b");
        lblB.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblB.setTextFill(Color.web("#1abc9c")); 
        lblB.setAlignment(Pos.CENTER);
        lblB.setMaxWidth(Double.MAX_VALUE);

        gridMatriz.add(lblA, 0, 0, n, 1); 
        gridMatriz.add(lblB, n, 0);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnasTotales; j++) {
                TextField tf = new TextField("");
                tf.setPromptText("0");
                tf.setPrefWidth(60);
                tf.setPrefHeight(40);
                tf.setAlignment(Pos.CENTER);
                tf.setFont(Font.font("System", 16));
                
                if (j == columnasTotales - 1) {
                    tf.setStyle("-fx-background-color: #e8f6f3; -fx-border-color: transparent transparent transparent #1abc9c; -fx-border-width: 0 0 0 2; -fx-background-radius: 5;");
                } else {
                    tf.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ecf0f1; -fx-background-radius: 5;");
                }
                
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("-?\\d*([\\.,]\\d*)?")) {
                        if (lblAlertaIngreso != null) lblAlertaIngreso.setVisible(true);
                        tf.setText(oldVal); 
                    } else {
                        if (lblAlertaIngreso != null) lblAlertaIngreso.setVisible(false);
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

        if (parentesisIzq != null && parentesisDer != null) {
            parentesisIzq.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
            parentesisDer.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
            double escalaY = Math.max(1.2, n * 0.6); 
            parentesisIzq.setScaleY(escalaY);
            parentesisIzq.setScaleX(0.5); 
            parentesisDer.setScaleY(escalaY);
            parentesisDer.setScaleX(0.5);
            parentesisIzq.setTranslateY(15);
            parentesisDer.setTranslateY(15);
        }
    }

    private void limpiarPantalla() {
        contenedorResultadosVisuales.getChildren().clear();
        panelResultado.setVisible(false);
        lblAlertaIngreso.setVisible(false);
    }
   
    private TextField crearCeldaResultados() {
        TextField tf = new TextField("");
        tf.setPrefWidth(60); 
        tf.setPrefHeight(40); 
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false); 
        tf.setFont(Font.font("System", FontWeight.BOLD, 14));
        tf.setStyle(ESTILO_NORMAL);
        return tf;
    }

    private HBox envolverEnParentesis(GridPane grid) {
        Label pIzq = new Label("(");
        pIzq.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
        pIzq.setTextFill(Color.web("#bdc3c7"));
        
        Label pDer = new Label(")");
        pDer.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
        pDer.setTextFill(Color.web("#bdc3c7"));
        
        double escalaY = Math.max(1.2, n * 0.6);
        pIzq.setScaleY(escalaY);
        pIzq.setScaleX(0.5);
        pDer.setScaleY(escalaY);
        pDer.setScaleX(0.5);
        
        HBox box = new HBox(-5, pIzq, grid, pDer); 
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void animarFactorizacion(Matriz L, Matriz U, String nombreMetodo, boolean huboPermutacion) {
        GridPane gridCopia = new GridPane(); gridCopia.setAlignment(Pos.CENTER); gridCopia.setHgap(4); gridCopia.setVgap(4);
        GridPane gridL = new GridPane(); gridL.setAlignment(Pos.CENTER); gridL.setHgap(4); gridL.setVgap(4);
        GridPane gridU = new GridPane(); gridU.setAlignment(Pos.CENTER); gridU.setHgap(4); gridU.setVgap(4);

        TextField[][] celdasCopia = new TextField[n][n];
        TextField[][] celdasL = new TextField[n][n];
        TextField[][] celdasU = new TextField[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                celdasCopia[i][j] = crearCeldaResultados(); gridCopia.add(celdasCopia[i][j], j, i);
                celdasL[i][j] = crearCeldaResultados(); gridL.add(celdasL[i][j], j, i);
                celdasU[i][j] = crearCeldaResultados(); gridU.add(celdasU[i][j], j, i);
            }
        }

        Label lblFlecha = new Label(" ➡ "); lblFlecha.setFont(Font.font("System", 30)); lblFlecha.setTextFill(Color.GRAY);
        Label lblPor = new Label(" × "); lblPor.setFont(Font.font("System", 30)); lblPor.setTextFill(Color.GRAY);

        HBox filaMatrices = new HBox(15,
            new VBox(5, crearSubtitulo("A"), envolverEnParentesis(gridCopia)),
            lblFlecha,
            new VBox(5, crearSubtitulo("L"), envolverEnParentesis(gridL)),
            lblPor,
            new VBox(5, crearSubtitulo("U"), envolverEnParentesis(gridU))
        );
        filaMatrices.setAlignment(Pos.CENTER);
        contenedorResultadosVisuales.getChildren().add(filaMatrices);

        String msgExito = "Descomposición finalizada.\nSe extrajeron las matrices L y U.";
        if (huboPermutacion) msgExito += "\n(Hubo permutación de filas para asegurar el pivote: PA = LU)";

        if (n > 5) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean esL = (i >= j);
                    double valor = esL ? L.getElemento(i, j) : U.getElemento(i, j);
                    celdasCopia[i][j].setText(String.format("%.3f", valor));
                    celdasCopia[i][j].setStyle(esL ? ESTILO_L : ESTILO_U);
                    
                    celdasL[i][j].setText(esL ? String.format("%.3f", L.getElemento(i, j)) : "0.00");
                    celdasL[i][j].setStyle(esL ? ESTILO_L : "-fx-text-fill: #bdc3c7; -fx-background-color: #f8f9f9;");
                    
                    celdasU[i][j].setText(i <= j ? String.format("%.3f", U.getElemento(i, j)) : "0.00");
                    celdasU[i][j].setStyle(i <= j ? ESTILO_U : "-fx-text-fill: #bdc3c7; -fx-background-color: #f8f9f9;");
                }
            }
            mostrarExito(nombreMetodo, "Matrices generadas al instante (n > 5).\n" + (huboPermutacion ? "(Hubo permutación de filas: PA = LU)" : ""));
        } else {
            Timeline timeline = new Timeline();
            double delaySegundos = 0.0;
            
            mostrarAdvertencia("Descomponiendo " + nombreMetodo + " paso a paso...");

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    final int fila = i;
                    final int col = j;
                    
                    KeyFrame frame = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                        boolean esL = (fila >= col);
                        double valor = esL ? L.getElemento(fila, col) : U.getElemento(fila, col);
                        
                        celdasCopia[fila][col].setText(String.format("%.3f", valor));
                        celdasCopia[fila][col].setStyle(esL ? ESTILO_L : ESTILO_U);
                        
                        celdasL[fila][col].setText(esL ? String.format("%.3f", L.getElemento(fila, col)) : "0.00");
                        celdasL[fila][col].setStyle(esL ? ESTILO_L : "-fx-text-fill: #bdc3c7; -fx-background-color: #f8f9f9;");
                        
                        celdasU[fila][col].setText(fila <= col ? String.format("%.3f", U.getElemento(fila, col)) : "0.00");
                        celdasU[fila][col].setStyle(fila <= col ? ESTILO_U : "-fx-text-fill: #bdc3c7; -fx-background-color: #f8f9f9;");
                    });
                    timeline.getKeyFrames().add(frame);
                    delaySegundos += 0.15; 
                }
            }
            final String finalMsg = msgExito;
            KeyFrame fin = new KeyFrame(Duration.seconds(delaySegundos), e -> mostrarExito(nombreMetodo, finalMsg));
            timeline.getKeyFrames().add(fin);
            timeline.play();
        }
    }

    // --- NUEVO: Animación Gauss In-Place (Evita arruinar la matriz de entrada) ---
    private void animarGaussSolucion(Matriz original, int fila1, int fila2, double[] solucion) {
        GridPane gridSimulada = new GridPane(); 
        gridSimulada.setAlignment(Pos.CENTER); 
        gridSimulada.setHgap(4); 
        gridSimulada.setVgap(4);
        
        int columnasTotales = n + 1;
        TextField[][] celdasSimuladas = new TextField[n][columnasTotales];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnasTotales; j++) {
                celdasSimuladas[i][j] = crearCeldaResultados(); 
                celdasSimuladas[i][j].setText(String.format("%.1f", original.getElemento(i, j)));
                if (j == columnasTotales - 1) {
                    celdasSimuladas[i][j].setStyle(ESTILO_VECTOR_B);
                }
                gridSimulada.add(celdasSimuladas[i][j], j, i);
            }
        }
        
        HBox envolturaSimulada = envolverEnParentesis(gridSimulada);
        VBox bloqueVisual = new VBox(10, crearSubtitulo("Evaluación de Pivoteo:"), envolturaSimulada);
        bloqueVisual.setAlignment(Pos.CENTER);
        contenedorResultadosVisuales.getChildren().add(bloqueVisual);
        
        Timeline timeline = new Timeline();
        
        // 1. Resaltar
        KeyFrame paso1 = new KeyFrame(Duration.ZERO, e -> {
            for (int j = 0; j < columnasTotales; j++) {
                celdasSimuladas[fila1][j].setStyle(ESTILO_PIVOTE);
                celdasSimuladas[fila2][j].setStyle(ESTILO_PIVOTE);
            }
        });

        // 2. Intercambio de texto
        KeyFrame paso2 = new KeyFrame(Duration.seconds(0.4), e -> {
            for (int j = 0; j < columnasTotales; j++) {
                String temp = celdasSimuladas[fila1][j].getText();
                celdasSimuladas[fila1][j].setText(celdasSimuladas[fila2][j].getText());
                celdasSimuladas[fila2][j].setText(temp);
            }
        });

        // 3. Restaurar colores
        KeyFrame paso3 = new KeyFrame(Duration.seconds(0.8), e -> {
            for (int j = 0; j < columnasTotales; j++) {
                celdasSimuladas[fila1][j].setStyle(j == columnasTotales - 1 ? ESTILO_VECTOR_B : ESTILO_NORMAL);
                celdasSimuladas[fila2][j].setStyle(j == columnasTotales - 1 ? ESTILO_VECTOR_B : ESTILO_NORMAL);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < solucion.length; i++) {
            sb.append("x").append(i+1).append(" = ").append(String.format("%.4f", solucion[i]));
            if (i < solucion.length - 1) sb.append("   |   ");
        }
        
        KeyFrame paso4 = new KeyFrame(Duration.seconds(1.2), e -> mostrarExito("Sistema Resuelto (Gauss)", sb.toString()));
        
        timeline.getKeyFrames().addAll(paso1, paso2, paso3, paso4);
        timeline.play();
    }

    private Label crearSubtitulo(String texto) {
        Label lbl = new Label(texto);
        lbl.setTextFill(Color.web("#bdc3c7"));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        return lbl;
    }

    private Button crearBotonUI(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        return btn;
    }

    // ==========================================
    // MENSAJES Y ERRORES IN-PLACE
    // ==========================================
    
    private void mostrarExito(String titulo, String detalle) {
        panelResultado.setStyle("-fx-background-color: #eafaf1; -fx-border-color: #2ecc71; -fx-border-radius: 15; -fx-background-radius: 15; -fx-border-width: 2;");
        lblEstado.setTextFill(Color.web("#27ae60"));
        lblEstado.setText("✔ " + titulo);
        lblDetalle.setTextFill(Color.web("#2c3e50"));
        lblDetalle.setText(detalle);
        panelResultado.setVisible(true);
    }
    
    private void mostrarError(String titulo, String detalle) {
        panelResultado.setStyle("-fx-background-color: #fdedec; -fx-border-color: #e74c3c; -fx-border-radius: 15; -fx-background-radius: 15; -fx-border-width: 2;");
        lblEstado.setTextFill(Color.web("#c0392b"));
        lblEstado.setText("✖ " + titulo);
        lblDetalle.setTextFill(Color.web("#c0392b"));
        lblDetalle.setText(detalle);
        panelResultado.setVisible(true);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        panelResultado.setStyle("-fx-background-color: #fef9e7; -fx-border-color: #f1c40f; -fx-border-radius: 15; -fx-background-radius: 15; -fx-border-width: 2;");
        lblEstado.setTextFill(Color.web("#d35400"));
        lblEstado.setText("⚠ Aviso");
        lblDetalle.setTextFill(Color.web("#d35400"));
        lblDetalle.setText(mensaje);
        panelResultado.setVisible(true);
    }

    private Matriz leerMatrizDesdeInterfaz() {
        int columnas = n + 1;
        double[][] datosIngresados = new double[n][columnas];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < columnas; j++) {
                String texto = casillas[i][j].getText().trim().replace(",", ".");
                if (texto.isEmpty()) texto = "0"; 
                datosIngresados[i][j] = Double.parseDouble(texto); 
            }
        }
        return new Matriz(datosIngresados);
    }

    private void ejecutarGauss(int tipoPivoteo) {
        limpiarPantalla(); 
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            int nLocal = matrizAmp.getCantFilas();
            
            // 1. Calculamos la solución matemática primero
            double[] solucion;
            if (tipoPivoteo == 1) solucion = MetodosDirectosMatriz.GaussPivoteoParcial(matrizAmp);
            else if (tipoPivoteo == 2) solucion = MetodosDirectosMatriz.GaussPivoteoTotal(matrizAmp);
            else solucion = MetodosDirectosMatriz.GaussPivoteoSimple(matrizAmp);
            
            // 2. Simulamos matemáticamente para hallar qué filas se iluminarán (sin tocar la UI)
            boolean huboPivoteoVisual = false;
            if (nLocal <= 5 && tipoPivoteo != 2) { 
                Matriz simulacion = matrizAmp.copiar();
                for (int k = 0; k < nLocal - 1; k++) {
                    int filaMayor = k;
                    double maxValor = Math.abs(simulacion.getElemento(k, k));
                    
                    for (int i = k + 1; i < nLocal; i++) {
                        double valorActual = Math.abs(simulacion.getElemento(i, k));
                        if ((tipoPivoteo == 1 && valorActual > maxValor) || 
                            (tipoPivoteo == 3 && maxValor < 1e-12 && valorActual > 0)) {
                            maxValor = valorActual;
                            filaMayor = i;
                        }
                    }
                    
                    if (filaMayor != k) {
                        mostrarAdvertencia("Pivoteo detectado en el paso " + (k+1) + ".\nIntercambiando fila " + (k+1) + " con " + (filaMayor+1) + "...");
                        // Llamamos al nuevo método visual que clona la matriz de entrada abajo
                        animarGaussSolucion(matrizAmp, k, filaMayor, solucion); 
                        huboPivoteoVisual = true;
                        break; 
                    }
                    
                    if (Math.abs(simulacion.getElemento(k, k)) > 1e-12) {
                        for (int i = k + 1; i < nLocal; i++) {
                            double m = simulacion.getElemento(i, k) / simulacion.getElemento(k, k);
                            for (int j = k; j < nLocal + 1; j++) {
                                simulacion.setElemento(i, j, simulacion.getElemento(i, j) - m * simulacion.getElemento(k, j));
                            }
                        }
                    }
                }
            }

            // 3. Si no hubo luces, mostramos el resultado al instante
            if (!huboPivoteoVisual) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < solucion.length; i++) {
                    sb.append("x").append(i+1).append(" = ").append(String.format("%.4f", solucion[i]));
                    if (i < solucion.length - 1) sb.append("   |   ");
                }
                mostrarExito("Sistema Resuelto (Gauss)", sb.toString());
            }
            
        } catch (NumberFormatException ex) {
            mostrarError("Error de Ingreso", "Asegúrese de ingresar solo números.");
        } catch (Exception ex) {
            mostrarError("Error Matemático", ex.getMessage());
        }
    }
}
