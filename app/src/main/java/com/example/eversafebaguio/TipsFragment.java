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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class TipsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and assign it to a View object
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

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
