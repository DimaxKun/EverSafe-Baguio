package com.example.eversafebaguio;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

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

        // User icon click
        ImageView userIcon = view.findViewById(R.id.icon_right);
        userIcon.setOnClickListener(v -> Toast.makeText(requireContext(), "User Profile", Toast.LENGTH_SHORT).show());

        // Donation button click
        ImageButton donateButton = view.findViewById(R.id.image_donate);
        donateButton.setOnClickListener(v -> {
            String donationUrl = "https://checkurbills.com/cub-portal/resources/view/layout/content/prc_form.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl));
            startActivity(browserIntent);
        });

        // Disaster alerts button click
        ImageButton warningButton = view.findViewById(R.id.image_disaster_alerts);
        warningButton.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);
            bottomNav.setSelectedItemId(R.id.warningSection);
        });

        // Evacuation routes button click
        ImageButton evacuateButton = view.findViewById(R.id.image_evacuation_routes);
        evacuateButton.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);
            bottomNav.setSelectedItemId(R.id.locationSection);
        });

        // Safety tips button click
        ImageButton tipsButton = view.findViewById(R.id.image_safety_tips);
        tipsButton.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);
            bottomNav.setSelectedItemId(R.id.tipsSection);
        });

        return view;
    }
}
