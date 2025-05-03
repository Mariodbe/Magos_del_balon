package com.example.magosdelbalon.mercado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magosdelbalon.Jugador;
import com.example.magosdelbalon.JugadorAdapter;
import com.example.magosdelbalon.R;

import java.util.ArrayList;
import java.util.List;

public class MercadoFragment extends Fragment {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private String ligaName;

    private static final String TAG = "MercadoFragment";

    public MercadoFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mercado, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerMercado);

        if (getArguments() != null) {
            ligaName = getArguments().getString("leagueName");
        }

        if (ligaName == null) {
            Toast.makeText(getContext(), "Liga no seleccionada.", Toast.LENGTH_SHORT).show();
            return view;
        }

        MercadoPagerAdapter pagerAdapter = new MercadoPagerAdapter(this, ligaName);
        viewPager.setAdapter(pagerAdapter);

        return view;
    }


}
