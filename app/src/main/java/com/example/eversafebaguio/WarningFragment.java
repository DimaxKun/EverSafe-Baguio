package com.example.eversafebaguio;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class WarningFragment extends Fragment {

    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        Button emergencyButton = view.findViewById(R.id.emergencyButton);
        emergencyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CallActivity.class);
            startActivity(intent);
        });

        ImageButton volumeButton = view.findViewById(R.id.playAudioButton);
        volumeButton.setOnClickListener(v -> showAudioPopup());

        return view;
    }

    private void showAudioPopup() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_audio_player);
        dialog.setCancelable(true);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.audioplayback);

        TextView audioTitle = dialog.findViewById(R.id.audioTitle);
        SeekBar seekBar = dialog.findViewById(R.id.seekBar);
        ImageButton playButton = dialog.findViewById(R.id.playButton);
        ImageButton pauseButton = dialog.findViewById(R.id.pauseButton);
        ImageButton stopButton = dialog.findViewById(R.id.stopButton);

        audioTitle.setText("audio.mp3");

        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.audioplayback);
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
