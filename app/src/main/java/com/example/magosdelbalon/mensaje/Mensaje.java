package com.example.magosdelbalon.mensaje;

public class Mensaje {
    private String remitenteId;
    private String contenido;
    private long timestamp;
    private String destinatarioId;
    private String remitenteNombre;

    // Constructor vacío necesario para Firestore
    public Mensaje() {
    }

    // Constructor con parámetros
    public Mensaje(String remitenteId, String contenido, long timestamp) {
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.timestamp = timestamp;
    }

    // Getters y setters para todos los campos
    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }
}


