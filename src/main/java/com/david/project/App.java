package com.david.project;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.opencv.core.Core;

/**
 * Clase principal que genera numeros aleatorios "reales" y
 * los muestra en pantalla junto con la camara
 */
public class App extends Application {
    /**
     * La camara del usuario
     */

    private AdministradorCamara camara;
    /**
     * LA etiqueta donde se muestran los numeros creados
     */
    private Label etiquetaNum;

    /**
     * ESta funcion comienza la ejecucion del Thread, y la llama
     * JavaFX directamente al correr el codigo
     * 
     * @param stage la escena donde se muestra la camara y el numero
     */
    @Override
    public void start(Stage stage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Acomodo toda la UI
        ImageView imageView = crearImageView();
        etiquetaNum = crearLabel();

        VBox pantalla = new VBox(10, imageView, etiquetaNum);
        pantalla.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pantalla, 640, 540);

        stage.setTitle("Generador de números random xd");
        stage.setScene(scene);
        stage.show();


        camara = new AdministradorCamara(imageView);
        camara.iniciarCamara();

        Generador generador = new Generador(camara);
        generador.crearNumeros(etiquetaNum);
    }

    /**
     * Crea un "visualizador" donde se pueden mostrar las imagenes o video
     * en este caso
     * 
     * @return un image view donde se muestran las imagenes o videos
     */
    private ImageView crearImageView() {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        return imageView;
    }

    /**
     * Crea una etiqueta donde se pueden mostrar los nmeros
     * 
     * @return una etiqueta para mostrar cosas en pantalla
     */
    private Label crearLabel() {
        Label etiqueta = new Label("Número generado:");
        etiqueta.setFont(new Font(20));
        return etiqueta;
    }

    /**
     * Lo que pasa cuando cerramos el codigo. TAmbien la llama JavaFX
     * automaticamente al cerrar
     */
    @Override
    public void stop() {
        camara.detenerCamara();
    }

    /**
     * La funcion principal que usa JavaFX para iniciar el codigo
     * en cuanto lo ejecutamos
     * 
     * @param args los parametros de linea (no se usan)
     */
    public static void main(String[] args) {
        launch();
    }
}
