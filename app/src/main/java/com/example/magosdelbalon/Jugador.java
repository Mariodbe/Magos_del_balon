package com.example.magosdelbalon;

public class Jugador {
    private String nombre;
    private String posicion;
    private int overall;
    private int ritmo;
    private int disparo;
    private int pase;
    private int regate;
    private int defensa;
    private int fisico;

    // Constructor
    public Jugador(String nombre, String posicion, int overall, int ritmo, int disparo, int pase, int regate, int defensa, int fisico) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.overall = overall;
        this.ritmo = ritmo;
        this.disparo = disparo;
        this.pase = pase;
        this.regate = regate;
        this.defensa = defensa;
        this.fisico = fisico;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getOverall() { return overall; }
    public int getRitmo() { return ritmo; }
    public int getDisparo() { return disparo; }
    public int getPase() { return pase; }
    public int getRegate() { return regate; }
    public int getDefensa() { return defensa; }
    public int getFisico() { return fisico; }
}
