package com.example.magosdelbalon.principal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PrincipalPagerAdapter extends FragmentStateAdapter {

    private final Bundle fragmentArgs;

    public PrincipalPagerAdapter(@NonNull Fragment fragment, Bundle args) {
        super(fragment);
        this.fragmentArgs = args;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        if (position == 0) {
            fragment = new PrincipalFragment();
        } else {
            fragment = new ClasificacionFragment();
        }

        fragment.setArguments(fragmentArgs); // para pasar "leagueName" u otros datos
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
