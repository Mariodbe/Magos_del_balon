package com.example.magosdelbalon;

public class Jugador {
    private String nombre;
    private String posicion;
    private int velocidad;
    private int resistencia;
    private int habilidad;
    private int defensa;
    private int ataque;

    // Constructor, getters y setters
    public Jugador(String nombre, String posicion, int velocidad, int resistencia, int habilidad, int defensa, int ataque) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.resistencia = resistencia;
        this.habilidad = habilidad;
        this.defensa = defensa;
        this.ataque = ataque;
    }

    // Getters y setters...


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getResistencia() {
        return resistencia;
    }

    public void setResistencia(int resistencia) {
        this.resistencia = resistencia;
    }

    public int getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(int habilidad) {
        this.habilidad = habilidad;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }
}
