package com.example.magosdelbalon.mercado;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MercadoPagerAdapter extends FragmentStateAdapter {
    private final String ligaName;

    public MercadoPagerAdapter(@NonNull Fragment fragment, String ligaName) {
        super(fragment);
        this.ligaName = ligaName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString("leagueName", ligaName);

        if (position == 0) {
            ComprarFragment comprarFragment = new ComprarFragment();
            comprarFragment.setArguments(args);
            return comprarFragment;
        } else {
            VenderFragment venderFragment = new VenderFragment();
            venderFragment.setArguments(args);
            return venderFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
