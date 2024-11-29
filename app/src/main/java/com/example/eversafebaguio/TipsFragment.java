package com.example.eversafebaguio;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TipsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and assign it to a View object
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        // Emergency button setup
        Button emergencyButton = view.findViewById(R.id.emergencyButton);
        emergencyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CallActivity.class);
            startActivity(intent);
        });

        return view; // Return the root view
    }
}
