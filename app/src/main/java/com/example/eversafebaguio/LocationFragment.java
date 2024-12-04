package com.example.eversafebaguio;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    TextView welcomeTextView1;
    TextView welcomeTextView2;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    private GoogleMap gMap;

    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

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

        messageList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        welcomeTextView1 = view.findViewById(R.id.welcome_text1);
        welcomeTextView2 = view.findViewById(R.id.welcome_text2);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_btn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
            welcomeTextView1.setVisibility(View.GONE);
            welcomeTextView2.setVisibility(View.GONE);
        });

        // Initialize SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void addResponse(String response) {
        requireActivity().runOnUiThread(() -> {
            if (!messageList.isEmpty()) {
                messageList.remove(messageList.size() - 1);
            }
            addToChat(response, Message.SENT_BY_BOT);
        });
    }

    void callAPI(String question) {
        if (question == null || question.isEmpty()) {
            addResponse("Invalid input. Please provide a valid question.");
            return;
        }

        // Add "Typing..." message
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));

        // Prepare JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", question);

            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", new JSONArray().put(partsObject));

            contentsArray.put(contentObject);
            jsonBody.put("contents", contentsArray);
        } catch (JSONException e) {
            addResponse("Error forming request: " + e.getMessage());
            return;
        }

        // Send request using OkHttp
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBKVQZlbketOPQZ92frpcO6mX3qNzK2gG4")
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() == null) {
                    addResponse("Response body is null.");
                    return;
                }

                String responseBody = response.body().string();
                Log.d("API Response", responseBody); // Log the entire response

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);

                    // Extract the response text from the "candidates" array
                    if (jsonObject.has("candidates")) {
                        JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
                        if (candidatesArray.length() > 0) {
                            JSONObject contentObject = candidatesArray.getJSONObject(0).getJSONObject("content");
                            JSONArray partsArray = contentObject.getJSONArray("parts");
                            StringBuilder result = new StringBuilder();
                            for (int i = 0; i < partsArray.length(); i++) {
                                result.append(partsArray.getJSONObject(i).getString("text"));
                            }
                            addResponse(result.toString().trim());
                        } else {
                            addResponse("No candidates found in response.");
                        }
                    } else {
                        addResponse("No 'candidates' field in response.");
                    }
                } catch (JSONException e) {
                    addResponse("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Define Baguio City's location
        LatLng baguioCity = new LatLng(16.4023, 120.5960);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baguioCity, 12));

        // Define additional locations
        LatLng cityCamp = new LatLng(16.413843, 120.590805);
        LatLng bayanPark = new LatLng(16.427900, 120.608564);
        LatLng loakanApugan = new LatLng(16.378541, 120.622015);
        LatLng bakakengCentral = new LatLng(16.395410, 120.581504);
        LatLng eastQuirinoHill = new LatLng(16.432127, 120.590433);

        // Add markers for the additional locations
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
