package com.example.magosdelbalon.mensaje;

public class Mensaje {
    private String remitenteId;
    private String contenido;
    private long timestamp;

    public Mensaje() {} // Constructor vacío necesario para Firestore

    public Mensaje(String remitenteId, String contenido, long timestamp) {
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.timestamp = timestamp;
    }

    public String getRemitenteId() { return remitenteId; }
    public String getContenido() { return contenido; }
    public long getTimestamp() { return timestamp; }
}

