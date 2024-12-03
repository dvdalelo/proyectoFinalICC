package com.david.project;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;

/**
 * Clase que permite manejar distintas funciones con la camara
 */
public class AdministradorCamara {
    /**
     * La camara a partir de la que se toman fotos
     */
    private final VideoCapture captura;

    /**
     * El visualizador de imagenes que se muestran en pantalla
     */
    private final ImageView imageView;

    /**
     * VAriable que permite saber si el codigo sigue en ejecutcion
     */
    private boolean ejecutando = true;

    /**
     * Constructor que crear una instancia para administrar la camara
     * 
     * @param imageView El lugar donde se van a visualizar las imagenes
     */
    public AdministradorCamara(ImageView imageView) {
        this.captura = new VideoCapture(0);
        this.imageView = imageView;

        if (!captura.isOpened()) {
            throw new RuntimeException("Error: No se encontró la cámara.");
        }
    }

    /**
     * Metodo que permite crear un Thread para la camara y
     * mostrar la imagen en la pantalla
     */
    public void iniciarCamara() {
        Thread cameraThread = new Thread(() -> {
            Mat frame = new Mat();
            Mat matrizImagen = new Mat();

            while (ejecutando) {
                if (captura.read(frame)) {
                    // ESto es porque las imagenes salen con un formato raro y hay que conertirlos
                    Imgproc.cvtColor(frame, matrizImagen, Imgproc.COLOR_BGR2RGB);
                    Image imagen = matrizAImagen(matrizImagen);
                    Platform.runLater(() -> imageView.setImage(imagen));
                }
                sleep(100);
            }
        });

        // ESto es basicamente decir que el thread corra en el fondo
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    /**
     * Metodo que crea una imagen a partir de una matriz
     * 
     * @param mat una matriz a partir de la que se crea la imagen
     * @return Una imagen creada a ppartir dde la matriz
     */
    private Image matrizAImagen(Mat mat) {
        // ESta me costo porq mat tiene la peor documentacion jamas vista
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    /**
     * Metodo que permite detener la Thread por un tiempo determinado
     * 
     * @param millis LOs milisegundos que va a dormir la tHREAD
     */
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * PAra evitar que el dispositivo explote se liberan recursos
     */
    public void detenerCamara() {
        ejecutando = false;
        captura.release();
    }

    /**
     * BAsicamente toma una foto. Crea una matriz a partir de la camara
     * 
     * @return una matriz que representa la imagen, o null si no hay camara
     */
    public Mat tomarFrame() {
        Mat frame = new Mat();
        if (captura.read(frame)) {
            return frame;
        }
        return null;
    }
}
