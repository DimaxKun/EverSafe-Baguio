package com.example.eversafebaguio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ImageButton donateButton = view.findViewById(R.id.image_donate);
        donateButton.setOnClickListener(v -> {

            String donationUrl = "https://checkurbills.com/cub-portal/resources/view/layout/content/prc_form.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl));
            startActivity(browserIntent);
        });

        ImageButton warningButton = view.findViewById(R.id.image_disaster_alerts); // Replace with your button ID
        warningButton.setOnClickListener(v -> {
            // Access the BottomNavigationView from the parent activity
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);

            // Programmatically set the selected item
            bottomNav.setSelectedItemId(R.id.warningSection);
        });

        ImageButton evacuateButton = view.findViewById(R.id.image_evacuation_routes); // Replace with your button ID
        evacuateButton.setOnClickListener(v -> {
            // Access the BottomNavigationView from the parent activity
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);

            // Programmatically set the selected item
            bottomNav.setSelectedItemId(R.id.locationSection);
        });

        ImageButton tipsButton = view.findViewById(R.id.image_safety_tips); // Replace with your button ID
        tipsButton.setOnClickListener(v -> {
            // Access the BottomNavigationView from the parent activity
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomnNav);

            // Programmatically set the selected item
            bottomNav.setSelectedItemId(R.id.tipsSection);
        });

        return view;
    }
}
