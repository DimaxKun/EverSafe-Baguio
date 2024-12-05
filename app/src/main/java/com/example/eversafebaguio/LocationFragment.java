package com.example.eversafebaguio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Location currentLocation;
    private Marker nearestEvacuationMarker;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        new AlertDialog.Builder(requireContext())
                                .setMessage("Are you sure you want to exit?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> requireActivity().finish())
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

        // Initialize SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the location layer
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission denied, notify the user
                Toast.makeText(requireContext(), "Location permissions are required to show your location.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermissions();
        }

        // Define Baguio City's location
        LatLng baguioCity = new LatLng(16.4023, 120.5960);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baguioCity, 13));

        // Define additional locations
        LatLng cityCamp = new LatLng(16.413843, 120.590805);
        LatLng bayanPark = new LatLng(16.427900, 120.608564);
        LatLng loakanApugan = new LatLng(16.378541, 120.622015);
        LatLng bakakengCentral = new LatLng(16.395410, 120.581504);
        LatLng eastQuirinoHill = new LatLng(16.432127, 120.590433);

        // Map evacuation centers to their names and details
        List<LatLng> evacuationCenters = new ArrayList<>();
        evacuationCenters.add(cityCamp);
        evacuationCenters.add(bayanPark);
        evacuationCenters.add(loakanApugan);
        evacuationCenters.add(bakakengCentral);
        evacuationCenters.add(eastQuirinoHill);

        HashMap<LatLng, String> centerNames = new HashMap<>();
        centerNames.put(cityCamp, "City Camp LQ Evacuation Center");
        centerNames.put(bayanPark, "East Bayan Park Evacuation Center");
        centerNames.put(loakanApugan, "Loakan-Apugan Evacuation Center");
        centerNames.put(bakakengCentral, "Bakakeng Central Evacuation Center");
        centerNames.put(eastQuirinoHill, "East Quirino Hill Evacuation Center");

        // Add markers to the map for each evacuation center
        gMap.addMarker(new MarkerOptions().position(cityCamp).title("City Camp LQ Evacuation Center").snippet(
                "Accomm. Area: YES\n" +
                        "Comfort Rooms: YES\n" +
                        "Kitchen: YES\n" +
                        "DRRM Office: YES\n" +
                        "Health Station: YES\n" +
                        "Breast Feeding Area: YES\n" +
                        "Capacity (no. of HHs): 80\n" +
                        "Floor Area: 800 sqm"));

        gMap.addMarker(new MarkerOptions().position(bayanPark).title("East Bayan Park Evacuation Center").snippet(
                "Floor Area: N/A\n" +
                        "Capacity (no. of HHs): 40"));

        gMap.addMarker(new MarkerOptions().position(eastQuirinoHill).title("East Quirino Hill Evacuation Center").snippet(
                "Floor Area: N/A\n" +
                        "Capacity (no. of HHs): 50"));

        gMap.addMarker(new MarkerOptions().position(loakanApugan).title("Loakan-Apugan Evacuation Center").snippet(
                "Floor Area: 408 sqm\n" +
                        "Capacity (no. of HHs): 30"));

        gMap.addMarker(new MarkerOptions().position(bakakengCentral).title("Bakakeng Central Evacuation Center").snippet(
                "Accomm. Area: YES\n" +
                        "Comfort Rooms: YES\n" +
                        "Kitchen: YES\n" +
                        "Health Station: YES\n" +
                        "Breast Feeding Area: YES\n" +
                        "Capacity (no. of HHs): 80\n" +
                        "Floor Area: 280 sqm"));

        // Fetch current location and calculate the closest evacuation center
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Location currentLocation = location;
                String nearestCenter = null;
                LatLng closestCenter = null;
                float shortestDistance = Float.MAX_VALUE;
                float nearestDistance = 0f; // Store the nearest distance in meters

                for (LatLng center : evacuationCenters) {
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(center.latitude);
                    targetLocation.setLongitude(center.longitude);

                    float distance = currentLocation.distanceTo(targetLocation);
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestCenter = center;
                        nearestCenter = centerNames.get(center); // Get the name of the nearest center
                        nearestDistance = distance; // Store the nearest distance
                    }
                }

                if (closestCenter != null) {
                    // Draw a line to the closest evacuation center
                    gMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), closestCenter)
                            .color(Color.GREEN)
                            .width(7f)); // Line width

                    // Convert distance from meters to kilometers
                    float distanceInKm = nearestDistance / 1000f; // Convert to kilometers

                    // Display the nearest evacuation center and the distance in the TextViews
                    TextView nearestCenterText = getView().findViewById(R.id.nearestEvacuationCenter);
                    TextView distanceText = getView().findViewById(R.id.distanceEvacuationCenter);
                    nearestCenterText.setText(nearestCenter);
                    distanceText.setText(String.format("%.2f km", distanceInKm)); // Display the distance with 2 decimal points
                }
            } else {
                Toast.makeText(requireContext(), "Unable to fetch your current location.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a click listener for the markers to display detailed information
        gMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        // Set a custom info window adapter (optional)
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null; // Use default window frame
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                // Custom layout for the info window
                LinearLayout info = new LinearLayout(requireContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(requireContext());
                title.setText(marker.getTitle());
                title.setTypeface(null, Typeface.BOLD);
                title.setPadding(0, 10, 0, 5);
                title.setTextColor(Color.BLACK);

                TextView snippet = new TextView(requireContext());
                snippet.setText(marker.getSnippet());
                snippet.setTextColor(Color.BLACK);

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

}
