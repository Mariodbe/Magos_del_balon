package com.example.magosdelbalon.video;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.MainActivity;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;

public class VideoFragment extends Fragment {

    private FullscreenVideoView videoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).ocultarMenus();
        }
        new Handler().postDelayed(() -> {
            if (getActivity() != null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                View decorView = getActivity().getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }, 100);

        videoView = view.findViewById(R.id.video_view);

        String videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.anuncio_magosdelbalon;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Obtener el nombre de la liga de los argumentos
        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        Log.d("VideoFragment", "Liga recibida en fragment: " + ligaName);

        videoView.setOnCompletionListener(mp -> {
            Toast.makeText(requireContext(), "Video finalizado", Toast.LENGTH_SHORT).show();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FireStoreHelper helper = new FireStoreHelper();

            helper.darRecompensaPorVideo(userId, ligaName, new FireStoreHelper.RecompensaCallback() {
                @Override
                public void onSuccess(String message) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Recompensa otorgada", Toast.LENGTH_SHORT).show();

                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refrescarDatosLiga();
                        }
                    }
                    if (isAdded()) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        });

        videoView.start();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mostrarMenus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }
}

