package com.example.magosdelbalon;

import android.content.Intent; // Importa la clase Intent
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Mostrar el BottomNavigationView cuando estemos en PrincipalFragment
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE); // Hacer visible el menú
        }
        View view = inflater.inflate(R.layout.fragment_principal, container, false);
        ImageButton backToHomeButton = view.findViewById(R.id.btn_back_to_home);
        if (backToHomeButton != null) {
            backToHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Iniciar HomeActivity
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(0, 0); // <- desde la actividad contenedora
                    }                }
            });
        }

        return view;
    }
}