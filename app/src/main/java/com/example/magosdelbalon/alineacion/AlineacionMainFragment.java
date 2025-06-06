package com.example.magosdelbalon.alineacion;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.magosdelbalon.MainActivity;
import com.example.magosdelbalon.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AlineacionMainFragment extends Fragment {

    public AlineacionMainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alineacion_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        // Obtener el nombre de liga (si usas argumentos)
        String leagueName = getArguments() != null ? getArguments().getString("leagueName") : null;

        // 1) Crear y setear el Adapter UNA sola vez
        AlineacionPagerAdapter adapter = new AlineacionPagerAdapter(requireActivity(), leagueName);
        viewPager.setAdapter(adapter);

        // 2) Vincular TabLayout con ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Alineación");
            else tab.setText("Tácticas");
        }).attach();

        // 3) Registrar callback para mostrar/ocultar menú al cambiar de pestaña
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (getActivity() instanceof MainActivity) {
                    if (position == 0) {
                        ((MainActivity) getActivity()).ocultarMenuSuperior();
                    } else {
                        ((MainActivity) getActivity()).mostrarMenus();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Justo cuando este fragmento entra en pantalla, ocultamos el menú
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).ocultarMenuSuperior();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cuando salgamos completamente de este fragmento (ya no se verá), volvemos a mostrar el menú
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mostrarMenus();
        }
    }
}
