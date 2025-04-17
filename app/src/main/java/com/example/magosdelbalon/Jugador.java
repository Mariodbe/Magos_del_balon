package com.example.magosdelbalon;

public class Jugador {
    private String nombre;
    private String posicion;
    private int overall;
    private boolean enEntrenamiento;
    private long tiempoFinalizacionEntrenamiento;
    private long timestampFinEntrenamiento; // en milisegundos



    // Constructor
    public Jugador(String nombre, String posicion, int overall) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.overall = overall;

    }
    public void entrenar() {
        overall += 1;
        if (overall > 99) {
            overall = 99;
        }
    }
    public long getTimestampFinEntrenamiento() {
        return timestampFinEntrenamiento;
    }

    public void setTimestampFinEntrenamiento(long timestampFinEntrenamiento) {
        this.timestampFinEntrenamiento = timestampFinEntrenamiento;
    }

    public void iniciarEntrenamiento() {
        this.enEntrenamiento = true;
        this.tiempoFinalizacionEntrenamiento = System.currentTimeMillis() + 10000; // 10 segundos
    }

    public void finalizarEntrenamiento() {
        this.enEntrenamiento = false;
        this.tiempoFinalizacionEntrenamiento = 0;
        this.entrenar(); // Aumenta el overall
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getOverall() { return overall; }

    @Override
    public String toString() {
        return nombre + ": "+ overall;
    }
}
