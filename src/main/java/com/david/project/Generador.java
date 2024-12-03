package com.david.project;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clare que permite generar un numero aleatorio "real" a
 * partir de las imagenes procesadas por la camara
 */
public class Generador {
    /**
     * La camara que se usa para generar los numeros
     */
    private final AdministradorCamara camara;
    
    /**
     * Variable para saber si el codigo sigue en ejecucion
     */
    private boolean ejecutando = true;

    /**
     * Constructor que cra un generador de numeros random
     * 
     * @param camara la camara que se va a usar
     */
    public Generador(AdministradorCamara camara) {
        this.camara = camara;
    }

    /**
     * Esta funcion llama a generarNumeroConFrame() y se los pone 
     * a la label que se muestra en pantalla
     * 
     * @param etiqueta la etiqueta donde se muestran los numeros
     */
    public void crearNumeros(Label etiqueta) {
        Thread creador = new Thread(() -> {
            while (ejecutando) {
                Mat frame = camara.tomarFrame();
                if (frame != null) {
                    long numero = generarNumeroConFrame(frame);
                    Platform.runLater(() -> etiqueta.setText("Número generado: " + numero));
                }
                sleep(5000); // Repetir cada 5 segundos
            }
        });

        creador.setDaemon(true);
        creador.start();
    }

    /**
     * Crea un numero aleatorio "real" a partir de la imagen que se recibe
     * desde la camara
     * 
     * @param frame la imagen en forma de matriz
     * @return el numero aleatorio
     */
    private long generarNumeroConFrame(Mat frame) {
        List<Integer> colores = jalarColoresRandom(frame, 5);
        // Aqui para obtener el promedio use un stream, porque con un for crasheaba el codigo
        double promedio = colores.isEmpty() ? 0 : colores.stream().mapToInt(Integer::intValue).average().orElse(0);
        long semilla = (long) (promedio * 1000) + System.nanoTime() % 1000;
        Random random = new Random((long) semilla);
        return random.nextLong();
    }

    /**
     * Agarra colores random desde una matriz que representa una imagen
     * 
     * @param frame la imagen en forma de matriz
     * @param num el numero de numeros random que queremos
     * @return la lista con los numeros
     */
    private List<Integer> jalarColoresRandom(Mat frame, int num) {
        List<Integer> colores = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < num; i++) {
            int x = random.nextInt(frame.cols());
            int y = random.nextInt(frame.rows());
            double[] bgr = frame.get(y, x);
            if (bgr != null) {
                for (double valor : bgr) {
                    colores.add((int) valor);
                }
            }
        }
        return colores;
    }

    /**
     * Namas hace que se duerma la thread. La cree porque
     * se veia feo tener todo el try catch en el codigo
     * 
     * @param millis los milisegundos a pausar el Thread
     */
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
