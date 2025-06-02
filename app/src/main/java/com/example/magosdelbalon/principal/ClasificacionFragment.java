package com.example.magosdelbalon.principal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClasificacionFragment extends Fragment {

    private FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private RecyclerView recyclerView;

    // Vistas para el equipo propio (view fijo)
    private TextView txtEquipoPropio, txtPuntosPropio, txtGanadosPropio, txtEmpatadosPropio, txtPerdidosPropio, txtDiferenciaGolesPropio;

    private String nombreEquipoPropio = ""; // Guardamos el nombre para filtrar de la lista

    public ClasificacionFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clasificacion, container, false);

        recyclerView = view.findViewById(R.id.recycler_clasificacion);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        View layoutEquipoPropio = view.findViewById(R.id.layout_equipo_propio);
        txtEquipoPropio = layoutEquipoPropio.findViewById(R.id.txt_equipo);
        txtPuntosPropio = layoutEquipoPropio.findViewById(R.id.txt_puntos);
        txtGanadosPropio = layoutEquipoPropio.findViewById(R.id.txt_ganados);
        txtEmpatadosPropio = layoutEquipoPropio.findViewById(R.id.txt_empatados);
        txtPerdidosPropio = layoutEquipoPropio.findViewById(R.id.txt_perdidos);
        txtDiferenciaGolesPropio = layoutEquipoPropio.findViewById(R.id.txt_diferencia_goles);

        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        if (ligaName != null) {
            fireStoreHelper.obtenerEstadisticasClasificacion(ligaName, new FireStoreHelper.ClasificacionCallback() {
                @Override
                public void onSuccess(Map<String, Object> equipoPropio) {
                    setDatosEquipoPropio(equipoPropio);
                    nombreEquipoPropio = (String) equipoPropio.get("equipo");

                    fireStoreHelper.obtenerClasificacionCompleta(ligaName, new FireStoreHelper.ListaClasificacionCallback() {
                        @Override
                        public void onSuccess(List<Map<String, Object>> clasificacion) {
                            List<Map<String, Object>> rivales = new ArrayList<>();
                            for (Map<String, Object> equipo : clasificacion) {
                                String nombre = (String) equipo.get("equipo");
                                if (!nombre.equals(nombreEquipoPropio)) {
                                    int ganados = toInt(equipo.get("partidosGanados"));
                                    int empatados = toInt(equipo.get("partidosEmpatados"));
                                    int golesAfavor = toInt(equipo.get("golesAfavor"));
                                    int golesContra = toInt(equipo.get("golesEnContra"));
                                    int puntos = ganados * 3 + empatados;
                                    int diferenciaGoles = golesAfavor - golesContra;

                                    equipo.put("puntos", puntos);
                                    equipo.put("diferenciaGoles", diferenciaGoles);
                                    rivales.add(equipo);
                                }
                            }

                            // Ordenamos por puntos y luego por diferencia de goles
                            rivales.sort((e1, e2) -> {
                                int puntos1 = toInt(e1.get("puntos"));
                                int puntos2 = toInt(e2.get("puntos"));
                                if (puntos1 != puntos2) {
                                    return Integer.compare(puntos2, puntos1); // Orden descendente por puntos
                                } else {
                                    int difGoles1 = toInt(e1.get("diferenciaGoles"));
                                    int difGoles2 = toInt(e2.get("diferenciaGoles"));
                                    return Integer.compare(difGoles2, difGoles1); // Orden descendente por diferencia de goles
                                }
                            });

                            ClasificacionAdapter adapter = new ClasificacionAdapter(rivales);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(getContext(), "Error clasificación: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Error equipo propio: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    private void setDatosEquipoPropio(Map<String, Object> equipo) {
        String nombre = (String) equipo.get("equipo");

        int ganados = toInt(equipo.get("partidosGanados"));
        int empatados = toInt(equipo.get("partidosEmpatados"));
        int perdidos = toInt(equipo.get("partidosPerdidos"));
        int golesAFavor = toInt(equipo.get("golesAfavor"));
        int golesEnContra = toInt(equipo.get("golesContra"));
        int puntos = ganados * 3 + empatados;
        int diferenciaGoles = golesAFavor - golesEnContra;

        txtEquipoPropio.setText(nombre);
        txtGanadosPropio.setText("G: " + ganados);
        txtEmpatadosPropio.setText("E: " + empatados);
        txtPerdidosPropio.setText("P: " + perdidos);
        txtPuntosPropio.setText(puntos + " pts");
        txtDiferenciaGolesPropio.setText("Dif: " + diferenciaGoles);
    }

    private int toInt(Object value) {
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        return 0;
    }

}
