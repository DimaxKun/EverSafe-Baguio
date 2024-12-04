package com.example.eversafebaguio;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class TipsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and assign it to a View object
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        // Handle back press for this fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Show confirmation dialog
                        new AlertDialog.Builder(requireContext())
                                .setMessage("Are you sure you want to exit?")
                                .setCancelable(false) // Prevent closing dialog when touched outside
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    // Exit the app
                                    requireActivity().finish(); // Close the activity if confirmed
                                })
                                .setNegativeButton("No", null) // Do nothing when "No" is clicked
                                .show();
                    }
                });

        ImageView userIcon = view.findViewById(R.id.icon_right);
        userIcon.setOnClickListener(v ->
                Toast.makeText(requireContext(), "User Profile", Toast.LENGTH_SHORT).show()
        );

        // Emergency button setup
        Button emergencyButton = view.findViewById(R.id.emergencyButton);
        emergencyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CallActivity.class);
            startActivity(intent);
        });

        return view; // Return the root view
    }
}
