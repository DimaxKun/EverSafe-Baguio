package com.example.eversafebaguio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

        return view;
    }
}
