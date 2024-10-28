package com.example.eversafebaguio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.example.eversafebaguio.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new DashboardFragment());
        binding.bottomnNav.setBackground(null);

        // Use setOnItemSelectedListener instead of setOnItemReselectedListener
        binding.bottomnNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeSection) {
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.warningSection) {
                replaceFragment(new WarningFragment());
            } else if (item.getItemId() == R.id.locationSection) {
                replaceFragment(new LocationFragment());
            } else if (item.getItemId() == R.id.tipsSection) {
                replaceFragment(new TipsFragment());
            } else if (item.getItemId() == R.id.notifSection) {
                replaceFragment(new NotifFragment());
            }
            return true; // Return true to indicate the event was handled
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}