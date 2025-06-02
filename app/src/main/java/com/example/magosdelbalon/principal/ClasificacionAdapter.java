package com.example.magosdelbalon.principal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.R;

import java.util.List;
import java.util.Map;

public class ClasificacionAdapter extends RecyclerView.Adapter<ClasificacionAdapter.ViewHolder> {
    private List<Map<String, Object>> clasificacionList;

    public ClasificacionAdapter(List<Map<String, Object>> clasificacionList) {
        this.clasificacionList = clasificacionList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEquipo, txtPuntos, txtGanados, txtEmpatados, txtPerdidos, txtDiferenciaGoles;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEquipo = itemView.findViewById(R.id.txt_equipo);
            txtPuntos = itemView.findViewById(R.id.txt_puntos);
            txtGanados = itemView.findViewById(R.id.txt_ganados);
            txtEmpatados = itemView.findViewById(R.id.txt_empatados);
            txtPerdidos = itemView.findViewById(R.id.txt_perdidos);
            txtDiferenciaGoles = itemView.findViewById(R.id.txt_diferencia_goles);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clasificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, Object> equipo = clasificacionList.get(position);

        String nombre = (String) equipo.get("equipo");
        int ganados = ((Long) equipo.get("partidosGanados")).intValue();
        int empatados = ((Long) equipo.get("partidosEmpatados")).intValue();
        int perdidos = ((Long) equipo.get("partidosPerdidos")).intValue();
        int golesAFavor = ((Long) equipo.get("golesAFavor")).intValue();
        int golesEnContra = ((Long) equipo.get("golesEnContra")).intValue();
        int puntos = ganados * 3 + empatados;
        int diferenciaGoles = golesAFavor - golesEnContra;

        holder.txtEquipo.setText(nombre);
        holder.txtGanados.setText("G: " + ganados);
        holder.txtEmpatados.setText("E: " + empatados);
        holder.txtPerdidos.setText("P: " + perdidos);
        holder.txtPuntos.setText(puntos + " pts");
        holder.txtDiferenciaGoles.setText("Dif: " + diferenciaGoles);
    }

    @Override
    public int getItemCount() {
        return clasificacionList.size();
    }
}
