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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ImageButton imageDisasterCurrent = view.findViewById(R.id.image_disaster_current);
        imageDisasterCurrent.setOnClickListener(v -> showDisasterPopup());

        ImageView bottomRightImage = view.findViewById(R.id.bottom_right_image);
        bottomRightImage.setOnClickListener(v -> showChatPopup());

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

        ImageButton disasterCurrentButton = view.findViewById(R.id.image_disaster_current);

        // Set an OnClickListener to navigate to DisasterCurrentFragment when clicked

        return view;
    }
    private void showDisasterPopup() {
        // Inflate the custom disaster popup layout
        LayoutInflater inflater = LayoutInflater.from(requireContext()); // Use requireContext() to get context
        View disasterView = inflater.inflate(R.layout.fragment_disaster_current, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Use requireContext() here as well
        builder.setView(disasterView);

        // Initialize components from the layout
//        ImageView imageDisaster = disasterView.findViewById(R.id.image_disaster_alerts);
//        TextView textDisaster = disasterView.findViewById(R.id.text_disaster_alerts);

        // Set some text or image based on the disaster alert
//        textDisaster.setText("Disaster Alerts");
//        imageDisaster.setImageResource(R.drawable.disaster_alerts); // Change this to your disaster image

        // Customize the dialog size for a smaller appearance
        AlertDialog disasterDialog = builder.create();
        disasterDialog.setOnShowListener(dialog -> {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8); // 80% of screen width
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6); // 60% of screen height
            disasterDialog.getWindow().setLayout(width, height);

            // Set rounded corners background
            disasterDialog.getWindow().setBackgroundDrawableResource(R.drawable.disaster_rounded_corner); // Apply the drawable here
        });

        // Show the dialog
        disasterDialog.show();
    }





    private void showChatPopup() {
        // Inflate the custom chat popup layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View chatView = inflater.inflate(R.layout.chat_popup_layout, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(chatView);

        // Initialize chat components
        RecyclerView recyclerView = chatView.findViewById(R.id.recycler_view);
        TextView welcomeTextView = chatView.findViewById(R.id.welcome_text);
        TextView welcomeTextView1 = chatView.findViewById(R.id.welcome_text1);
        EditText messageEditText = chatView.findViewById(R.id.message_edit_text);
        ImageButton sendButton = chatView.findViewById(R.id.send_btn);

        // Chat setup
        List<Message> messageList = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        OkHttpClient client = new OkHttpClient();

        // Send button click listener
        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME, messageList, messageAdapter, recyclerView);
            messageEditText.setText("");
            callAPI(question, client, messageList, messageAdapter, recyclerView);
            welcomeTextView.setVisibility(View.GONE);
            welcomeTextView1.setVisibility(View.GONE);
        });

        // Create and customize the dialog
        AlertDialog chatDialog = builder.create();
        chatDialog.setOnShowListener(dialog -> {
            // Customize dialog size for smaller appearance
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% of screen width
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7); // 80% of screen height
            chatDialog.getWindow().setLayout(width, height);
            chatDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Optional: Transparent background
            chatDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_corners);
        });

        // Show the dialog
        chatDialog.show();
    }

    // Method to add message to chat
    private void addToChat(String message, String sentBy, List<Message> messageList, MessageAdapter adapter, RecyclerView recyclerView) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });
    }

    // Method to add bot's response to chat
    private void addResponse(String response, List<Message> messageList, MessageAdapter adapter, RecyclerView recyclerView) {
        requireActivity().runOnUiThread(() -> {
            if (!messageList.isEmpty()) {
                messageList.remove(messageList.size() - 1);
            }
            addToChat(response, Message.SENT_BY_BOT, messageList, adapter, recyclerView);
        });
    }

    // Method to call API and get response
    private void callAPI(String question, OkHttpClient client, List<Message> messageList, MessageAdapter adapter, RecyclerView recyclerView) {
        if (question == null || question.isEmpty()) {
            addResponse("Invalid input. Please provide a valid question.", messageList, adapter, recyclerView);
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
            addResponse("Error forming request: " + e.getMessage(), messageList, adapter, recyclerView);
            return;
        }

        // Send request using OkHttp
        RequestBody body = RequestBody.create(jsonBody.toString(), ChatAI.JSON);
        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBKVQZlbketOPQZ92frpcO6mX3qNzK2gG4")
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response: " + e.getMessage(), messageList, adapter, recyclerView);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() == null) {
                    addResponse("Response body is null.", messageList, adapter, recyclerView);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);

                    if (jsonObject.has("candidates")) {
                        JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
                        if (candidatesArray.length() > 0) {
                            JSONObject contentObject = candidatesArray.getJSONObject(0).getJSONObject("content");
                            JSONArray partsArray = contentObject.getJSONArray("parts");
                            StringBuilder result = new StringBuilder();
                            for (int i = 0; i < partsArray.length(); i++) {
                                result.append(partsArray.getJSONObject(i).getString("text"));
                            }
                            addResponse(result.toString().trim(), messageList, adapter, recyclerView);
                        } else {
                            addResponse("No candidates found in response.", messageList, adapter, recyclerView);
                        }
                    } else {
                        addResponse("No 'candidates' field in response.", messageList, adapter, recyclerView);
                    }
                } catch (JSONException e) {
                    addResponse("Failed to parse response: " + e.getMessage(), messageList, adapter, recyclerView);
                }
            }
        });
    }
}
