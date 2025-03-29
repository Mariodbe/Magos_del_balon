package com.example.magosdelbalon;

public class Equipo {
    private String nombre;
    private String estado; // Ej: "libre", "ocupado", o directamente userId si está asignado

    public Equipo() {} // Necesario para Firestore

    public Equipo(String nombre, String estado) {
        this.nombre = nombre;
        this.estado = estado;
    }

    // Getters y setters
}


