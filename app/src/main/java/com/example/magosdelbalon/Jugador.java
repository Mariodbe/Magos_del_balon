package com.example.magosdelbalon;

public class Jugador {
    private String nombre;
    private String posicion;
    private int overall;


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

    // Getters y Setters
    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getOverall() { return overall; }

    @Override
    public String toString() {
        return nombre + ": "+ overall;
    }
}
