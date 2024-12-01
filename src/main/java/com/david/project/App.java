package com.david.project;

// Las librerias de javafx para la pantalla
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Estas son para las camaras con OpenCV
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;

public class App extends Application {
    private VideoCapture capture;
    private ImageView imageView;
    private Label numberLabel;
    private boolean isRunning = true;

    @Override
    public void start(Stage stage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);

        // La label para mostrar el numero generado
        numberLabel = new Label("Numero generado: ");
        numberLabel.setFont(new Font(20));

        // Y esta es como la forma de acomodar la camara y la etiqueta
        VBox root = new VBox(10);
        root.getChildren().addAll(imageView, numberLabel);
        root.setAlignment(Pos.CENTER);

        
        Scene scene = new Scene(root, 640, 540);

        stage.setTitle("Generador de numeros");
        stage.setScene(scene);
        stage.show();

        startCamera();
        generarNumeros();
    }

    // Literalmente hago nuestro propio video porque no encontre algun metodo que 
    // me dejara ver la camara como video
    private void startCamera() {
        capture = new VideoCapture(0);
        
        if (!capture.isOpened()) {
            System.out.println("Error: Camera not found!");
            return;
        }

        // Esto de los threads lo acabamos de ver con salvador xd
        Thread cameraThread = new Thread(() -> {
            Mat frame = new Mat();
            Mat convertedFrame = new Mat();
            while (isRunning) {
                if (capture.read(frame)) {
                    // Le ponemos colorcito a la frame
                    Imgproc.cvtColor(frame, convertedFrame, Imgproc.COLOR_BGR2RGB);
                    
                    // Convertimoz la matriz a una imagen
                    Image image = matrizAImagen(convertedFrame);
                    
                    // Actualizamos la imagen que se ve
                    Platform.runLater(() -> imageView.setImage(image));
                }
                
                // Le ponemos delay pa que no vaya tan lento
                try {
                    Thread.sleep(33); // ~30 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    // TODO: Generar los numeros con la imagen que se muestra.
    private void generarNumeros() {
        // Lo mismo de los threads que acabamos de ver con salvador
        Thread numberThread = new Thread(() -> {
            SecureRandom random = new SecureRandom();
            while (isRunning) {
                long generatedNumber = random.nextLong();
                
                // Actualizamos la etiqueta pa que se ponga el numero ahi
                Platform.runLater(() ->
                    numberLabel.setText("Numero generado: " + generatedNumber)
                );
                
                try {
                    // Se actualiza cada 5 segundos
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        numberThread.setDaemon(true);
        numberThread.start();
    }

    /**
     * Convierte la matriz que recibe la camara a una imagen para que se pueda mostrar
     * 
     * @param mat La imagen en forma de matriz
     * @return Una imagen
     */
    private Image matrizAImagen(Mat mat) {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }

    @Override
    public void stop() {
        isRunning = false;
        if (capture != null) {
            capture.release();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}