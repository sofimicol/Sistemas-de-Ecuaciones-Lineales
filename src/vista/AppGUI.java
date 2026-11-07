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
    private final String ESTILO_L = "-fx-background-color: #d6eaf8; -fx-border-color: #3498db; -fx-background-radius: 5; -fx-text-fill: #2980b9; -fx-font-weight: bold;"; // Azul pastel
    private final String ESTILO_U = "-fx-background-color: #d5f5e3; -fx-border-color: #2ecc71; -fx-background-radius: 5; -fx-text-fill: #27ae60; -fx-font-weight: bold;"; // Verde pastel
    private final String ESTILO_PIVOTE = "-fx-background-color: #fcf3cf; -fx-border-color: #f1c40f; -fx-background-radius: 5;"; // Amarillo resaltado
    private final String ESTILO_NORMAL = "-fx-background-color: #ffffff; -fx-border-color: #ecf0f1; -fx-background-radius: 5;";
    private final String ESTILO_VECTOR_B = "-fx-background-color: #e8f6f3; -fx-border-color: transparent transparent transparent #1abc9c; -fx-border-width: 0 0 0 2; -fx-background-radius: 5;";
    
    private VBox panelResultado;
    private Label lblEstado;
    private Label lblDetalle;
    
    // NUEVO: Cartel de alerta para validación
    private Label lblAlertaIngreso;
    private VBox contenedorResultadosVisuales;
    private ScrollPane scrollCentral;
    private Label parentesisIzq;
    private Label parentesisDer; 
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // ==========================================
        // 1. PANEL LATERAL (Barra de Menú Redondeada)
        // ==========================================
        VBox panelLateral = new VBox(12);
        panelLateral.setPadding(new Insets(20));
        panelLateral.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 0 20 20 0;");
        panelLateral.setPrefWidth(270);

        Label tituloMenu = new Label("SISTEMAS LINEALES");
        tituloMenu.setTextFill(Color.WHITE);
        tituloMenu.setFont(Font.font("System", FontWeight.BOLD, 18));

     // Control dinámico de Tamaño
        HBox panelTamanio = new HBox(10);
        panelTamanio.setAlignment(Pos.CENTER_LEFT);
        Label lblTamanio = new Label("Tamaño n:");
        lblTamanio.setTextFill(Color.WHITE);
        
        Spinner<Integer> spinnerN = new Spinner<>(2, Matriz.MAX_DIMENSION, n);
        spinnerN.setPrefWidth(70);
        spinnerN.setEditable(true); // <-- NUEVO: Permite escribir el número

        // Guarda el número tipeado si el usuario hace clic afuera (sin necesidad de dar Enter)
        spinnerN.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    int val = Integer.parseInt(spinnerN.getEditor().getText());
                    if (val >= 2 && val <= Matriz.MAX_DIMENSION) {
                        spinnerN.getValueFactory().setValue(val);
                    } else {
                        spinnerN.getEditor().setText(spinnerN.getValue().toString());
                    }
                } catch (NumberFormatException ex) {
                    spinnerN.getEditor().setText(spinnerN.getValue().toString()); // Rechaza letras
                }
            }
        });

        spinnerN.valueProperty().addListener((obs, viejo, nuevo) -> {
            n = nuevo;
            generarCuadricula();
            ocultarResultado();
        });
        panelTamanio.getChildren().addAll(lblTamanio, spinnerN);

     // --- Botones de Métodos ---
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

        // EVENTOS BÁSICOS
        btnGaussSimple.setOnAction(e -> ejecutarGauss(3));
        btnGaussParcial.setOnAction(e -> ejecutarGauss(1));
        btnGaussTotal.setOnAction(e -> ejecutarGauss(2));
        btnJordan.setOnAction(e -> mostrarAdvertencia("Gauss-Jordan en construcción..."));
        
        btnFactorizarLU.setOnAction(e -> {
            limpiarPantalla();
            try {
                Matriz matrizA = leerMatrizDesdeInterfaz().extraerA();
                MetodosDirectosMatriz.ResultadoLU res = MetodosDirectosMatriz.factorizar_x_gauss(matrizA, 1);
                
                // Le enviamos el booleano al método visual
                animarFactorizacion(res.L, res.U, "Factorización LU", res.huboPermutacion);
                
            } catch (Exception ex) {
                mostrarError("Error Matemático", ex.getMessage());
            }
        });
        
        btnCroutL1U.setOnAction(e -> mostrarAdvertencia("Crout (L1 U) en construcción..."));
        btnCroutLU1.setOnAction(e -> mostrarAdvertencia("Crout (L U1) en construcción..."));
        btnCholeskyL.setOnAction(e -> mostrarAdvertencia("Cholesky (L L^T) en construcción..."));
        btnCholeskyU.setOnAction(e -> mostrarAdvertencia("Cholesky (U^T U) en construcción..."));
        
        btnJacobi.setOnAction(e -> mostrarAdvertencia("Jacobi en construcción..."));
        btnSeidel.setOnAction(e -> mostrarAdvertencia("Gauss-Seidel en construcción..."));
        btnSOR.setOnAction(e -> mostrarAdvertencia("SOR en construcción..."));
        btnRelajamiento.setOnAction(e -> mostrarAdvertencia("Relajamiento en construcción..."));
        btnMejoramiento.setOnAction(e -> mostrarAdvertencia("Mejoramiento Iterativo en construcción..."));

        // ENSAMBLAJE DEL PANEL LATERAL
        panelLateral.getChildren().addAll(
            tituloMenu, panelTamanio, new Separator(),
            crearSubtitulo("RESOLUCIÓN DIRECTA"), btnGaussSimple, btnGaussParcial, btnGaussTotal, btnJordan,
            new Separator(), crearSubtitulo("FACTORIZACIONES"), btnFactorizarLU, btnCroutL1U, btnCroutLU1, btnCholeskyL, btnCholeskyU,
            new Separator(), crearSubtitulo("ITERATIVOS"), btnJacobi, btnSeidel, btnSOR, btnRelajamiento, btnMejoramiento
        );

     // ==========================================
        // 2. PANEL CENTRAL (Contenedores y Título Estático)
        // ==========================================
        VBox panelDerecho = new VBox(10); // Contenedor que no se mueve
        panelDerecho.setPadding(new Insets(20));
        panelDerecho.setAlignment(Pos.TOP_CENTER);
        panelDerecho.setStyle("-fx-background-color: #f4f6f7;");

        Label tituloMatriz = new Label("Ingrese la Matriz Ampliada");
        tituloMatriz.setFont(Font.font("System", FontWeight.BOLD, 26));
        tituloMatriz.setTextFill(Color.web("#2c3e50"));

        lblAlertaIngreso = new Label("⚠️ Carácter inválido: Solo números, signos o puntos.");
        lblAlertaIngreso.setTextFill(Color.web("#e74c3c"));
        lblAlertaIngreso.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblAlertaIngreso.setVisible(false);

        // NUEVO: Contenedor que envuelve SOLO a las matrices para que puedan scrollear
        VBox contenidoScroll = new VBox(20);
        contenidoScroll.setAlignment(Pos.CENTER);
        contenidoScroll.setPadding(new Insets(20));
        
        HBox envolturaMatriz = new HBox(-5); // Margen negativo para acercar los corchetes
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
        generarCuadricula();

     // ==========================================
        // 3. PANEL DE RESULTADO IN-PLACE Y VISUAL
        // ==========================================
        panelResultado = new VBox(5); // Reducido de 10 a 5
        panelResultado.setAlignment(Pos.CENTER);
        panelResultado.setPadding(new Insets(10, 20, 10, 20)); // Padding vertical reducido a la mitad
        panelResultado.setStyle("-fx-background-radius: 10;");
        panelResultado.setMaxWidth(500); // Reducido para que no ocupe tanto espacio
        panelResultado.setVisible(false); 

        lblEstado = new Label();
        lblEstado.setFont(Font.font("System", FontWeight.BOLD, 15)); // Fuente más chica
        
        lblDetalle = new Label();
        lblDetalle.setFont(Font.font("Monospaced", 13)); // Fuente más chica
        lblDetalle.setWrapText(true);

        panelResultado.getChildren().addAll(lblEstado, lblDetalle);

        contenedorResultadosVisuales = new VBox(20);
        contenedorResultadosVisuales.setAlignment(Pos.CENTER);
        contenedorResultadosVisuales.setPadding(new Insets(10));

// === ENSAMBLAJE CON ESPACIADO EXTRA Y SCROLL GLOBAL ===
        
        // Creamos un "resorte" transparente para inyectar espacio vacío forzado entre la entrada y las salidas
        Region espaciador1 = new Region();
        espaciador1.setMinHeight(30); // 30 píxeles de aire garantizado
        
        Region espaciador2 = new Region();
        espaciador2.setMinHeight(20); // 20 píxeles de aire antes del cartel final

        // Metemos TODOS los elementos (Título, Entrada, Resultados Visuales y Cartel Final) en un solo VBox
        VBox contenidoGlobal = new VBox(10);
        contenidoGlobal.setAlignment(Pos.TOP_CENTER);
        contenidoGlobal.setPadding(new Insets(30));
        contenidoGlobal.getChildren().addAll(
            tituloMatriz, 
            lblAlertaIngreso, 
            envolturaMatriz, 
            espaciador1, // Aire
            contenedorResultadosVisuales, 
            espaciador2, // Aire
            panelResultado
        );

        // Envolvemos TODO el contenido derecho en un ScrollPane
        scrollCentral = new ScrollPane(contenidoGlobal);
        scrollCentral.setFitToWidth(true); // Se adapta al ancho de la ventana
        scrollCentral.setFitToHeight(true);
        scrollCentral.setStyle("-fx-background-color: transparent; -fx-background: #f4f6f7;");
        scrollCentral.setBorder(Border.EMPTY);

        // Ensamblaje Final: Menú Fijo a la Izquierda, Scroll Global a la Derecha
        BorderPane root = new BorderPane();
        root.setLeft(panelLateral);
        root.setCenter(scrollCentral);

        Scene scene = new Scene(root, 1100, 750);
        stage.setTitle("Resolutor de Sistemas - TPI");
        stage.setScene(scene);
        stage.show();
    }

    // ==========================================
    // MÉTODOS VISUALES
    // ==========================================

    private TextField crearCeldaResultados() {
        TextField tf = new TextField("");
        tf.setPrefWidth(60);  // IGUAL AL INGRESO
        tf.setPrefHeight(40); // IGUAL AL INGRESO
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
        contenedorResultadosVisuales.getChildren().clear(); 

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

        // Preparamos el mensaje definitivo de éxito
        String msgExito = "Descomposición finalizada.\nSe extrajeron las matrices L y U.";
        if (huboPermutacion) {
            msgExito += "\n(Hubo permutación de filas para asegurar el pivote: PA = LU)";
        }

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
            double velocidad = 0.15; 

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
                    delaySegundos += velocidad; 
                }
            }

            final String finalMsg = msgExito;
            KeyFrame fin = new KeyFrame(Duration.seconds(delaySegundos), e -> {
                mostrarExito(nombreMetodo, finalMsg);
            });
            timeline.getKeyFrames().add(fin);
            timeline.play();
        }
    }
    
    private void limpiarPantalla() {
        contenedorResultadosVisuales.getChildren().clear();
        panelResultado.setVisible(false);
        lblAlertaIngreso.setVisible(false);
    }
    
   private void animarIntercambioVisual(int fila1, int fila2) {
       if (n > 5 || fila1 == fila2) return; // Evita animar matrices grandes o pivotes estáticos

       Timeline timeline = new Timeline();
       
       // Paso 1: Resaltar ambas filas en amarillo
       KeyFrame paso1 = new KeyFrame(Duration.ZERO, e -> {
           for (int j = 0; j <= n; j++) {
               casillas[fila1][j].setStyle(ESTILO_PIVOTE);
               casillas[fila2][j].setStyle(ESTILO_PIVOTE);
           }
       });

       // Paso 2: Intercambiar los textos físicamente en la grilla
       KeyFrame paso2 = new KeyFrame(Duration.seconds(0.4), e -> {
           for (int j = 0; j <= n; j++) {
               String temp = casillas[fila1][j].getText();
               casillas[fila1][j].setText(casillas[fila2][j].getText());
               casillas[fila2][j].setText(temp);
           }
       });

       // Paso 3: Devolver los estilos a la normalidad
       KeyFrame paso3 = new KeyFrame(Duration.seconds(0.8), e -> {
           for (int j = 0; j <= n; j++) {
               casillas[fila1][j].setStyle(j == n ? ESTILO_VECTOR_B : ESTILO_NORMAL);
               casillas[fila2][j].setStyle(j == n ? ESTILO_VECTOR_B : ESTILO_NORMAL);
           }
       });

       timeline.getKeyFrames().addAll(paso1, paso2, paso3);
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

    private void generarCuadricula() {
        gridMatriz.getChildren().clear();
        int columnasTotales = n + 1; 
        casillas = new TextField[n][columnasTotales];

        // NUEVO: Letras A y b sobre la matriz
        Label lblA = new Label("A");
        lblA.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblA.setTextFill(Color.web("#7f8c8d"));
        lblA.setAlignment(Pos.CENTER);
        lblA.setMaxWidth(Double.MAX_VALUE);

        Label lblB = new Label("b");
        lblB.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblB.setTextFill(Color.web("#1abc9c")); // Color que combina con la línea divisoria
        lblB.setAlignment(Pos.CENTER);
        lblB.setMaxWidth(Double.MAX_VALUE);

        // Se insertan en la Fila 0. "A" ocupa 'n' columnas de ancho, "b" ocupa 1 columna.
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
                
                // VALIDACIÓN EN TIEMPO REAL (Evita letras)
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("-?\\d*([\\.,]\\d*)?")) {
                        if (lblAlertaIngreso != null) lblAlertaIngreso.setVisible(true);
                        tf.setText(oldVal); 
                    } else {
                        if (lblAlertaIngreso != null) lblAlertaIngreso.setVisible(false);
                    }
                });

                // NAVEGACIÓN DINÁMICA CON FLECHAS DEL TECLADO
                final int fila = i;
                final int col = j;
                tf.setOnKeyPressed(e -> {
                    if (e.getCode() == javafx.scene.input.KeyCode.UP) {
                        if (fila > 0) casillas[fila - 1][col].requestFocus();
                    } else if (e.getCode() == javafx.scene.input.KeyCode.DOWN) {
                        if (fila < n - 1) casillas[fila + 1][col].requestFocus();
                    } else if (e.getCode() == javafx.scene.input.KeyCode.LEFT) {
                        if (tf.getCaretPosition() == 0 && col > 0) casillas[fila][col - 1].requestFocus();
                    } else if (e.getCode() == javafx.scene.input.KeyCode.RIGHT) {
                        if (tf.getCaretPosition() == tf.getText().length() && col < columnasTotales - 1) casillas[fila][col + 1].requestFocus();
                    }
                });
                
                casillas[i][j] = tf;
                // CAMBIO: i + 1 desplaza las celdas hacia abajo para hacerle lugar a "A" y "b"
                gridMatriz.add(tf, j, i + 1); 
            }
        }

        // Paréntesis envolventes exagerados
        if (parentesisIzq != null && parentesisDer != null) {
            parentesisIzq.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
            parentesisDer.setFont(Font.font("System", FontWeight.EXTRA_LIGHT, 100));
            
            // Fórmula que garantiza que el paréntesis crezca más rápido que las celdas
            double escalaY = Math.max(1.2, n * 0.6); 
            parentesisIzq.setScaleY(escalaY);
            parentesisIzq.setScaleX(0.5); // Lo mantiene delgado
            parentesisDer.setScaleY(escalaY);
            parentesisDer.setScaleX(0.5);
            
            // Los bajamos levemente para compensar el espacio que ocupan los títulos A y b arriba
            parentesisIzq.setTranslateY(15);
            parentesisDer.setTranslateY(15);
        }
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

    private void ocultarResultado() {
        panelResultado.setVisible(false);
    }

    // ==========================================
    // LÓGICA DE EXTRACCIÓN
    // ==========================================

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
        limpiarPantalla(); // <--- BORRA LO ANTERIOR
        try {
            Matriz matrizAmp = leerMatrizDesdeInterfaz();
            double[] solucion;
            
            if (tipoPivoteo == 1) {
                solucion = MetodosDirectosMatriz.GaussPivoteoParcial(matrizAmp);
            } else {
                solucion = MetodosDirectosMatriz.GaussPivoteoTotal(matrizAmp);
            }
            
            // Construir el string de solución bonito
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < solucion.length; i++) {
                sb.append("x").append(i+1).append(" = ").append(String.format("%.4f", solucion[i]));
                if (i < solucion.length - 1) sb.append("   |   ");
            }
            
            mostrarExito("Sistema Resuelto (Gauss)", sb.toString());
            
        } catch (NumberFormatException ex) {
            mostrarError("Error de Ingreso", "Revise las celdas. Asegúrese de ingresar solo números.");
        } catch (ArithmeticException | IllegalArgumentException ex) {
            mostrarError("Error Matemático", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error Crítico", ex.getMessage());
        }
    }
}
