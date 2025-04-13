package com.example.magosdelbalon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EstadioFragment extends Fragment {@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_estadio, container, false);
    ImageButton btnBackToHome = view.findViewById(R.id.btn_back_to_home);
    btnBackToHome.setOnClickListener(v -> {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(0, 0);
        }
    });

    return view;
}
}
