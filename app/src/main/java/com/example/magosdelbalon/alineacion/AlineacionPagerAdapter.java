package com.example.magosdelbalon.alineacion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AlineacionPagerAdapter extends FragmentStateAdapter {

    private final String leagueName;

    public AlineacionPagerAdapter(@NonNull FragmentActivity fragmentActivity, String leagueName) {
        super(fragmentActivity);
        this.leagueName = leagueName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString("leagueName", leagueName);

        Fragment fragment;
        if (position == 0) {
            fragment = new AlineacionFragment();
        } else {
            fragment = new TacticasFragment();
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

