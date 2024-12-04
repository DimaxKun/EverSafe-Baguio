package com.example.eversafebaguio;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eversafebaguio.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initial fragment
        replaceFragment(new DashboardFragment());
        binding.bottomnNav.setBackground(null);

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
                // Replace with NotifFragment and trigger the notification
                NotifFragment notifFragment = new NotifFragment();
                replaceFragment(notifFragment);
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
