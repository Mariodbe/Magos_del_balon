package com.example.magosdelbalon;

import java.util.ArrayList;

public class Liga {
    private String nombre;
    private String propietarioId;
    private ArrayList<String> equipos;
    private String tipoLiga;

    public Liga() {
        // Necesario para Firebase
    }

    public Liga(String nombre, String propietarioId, ArrayList<String> equipos,String tipoLiga) {
        this.nombre = nombre;
        this.propietarioId = propietarioId;
        this.equipos = equipos;
        this.tipoLiga = tipoLiga;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }

    public ArrayList<String> getEquipos() {
        return equipos;
    }

    public void setEquipos(ArrayList<String> equipos) {
        this.equipos = equipos;
    }

    public String getTipoLiga() {return tipoLiga;}

    public void setTipoLiga(String tipoLiga) {this.tipoLiga = tipoLiga;}
}


