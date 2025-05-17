package com.example.magosdelbalon.principal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magosdelbalon.R;

public class PrincipalMainFragment extends Fragment {

    private ViewPager2 viewPager;

    public PrincipalMainFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal_main, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new PrincipalPagerAdapter(this, getArguments()));

        return view;
    }
}
