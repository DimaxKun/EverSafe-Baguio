package com.example.eversafebaguio;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class TipsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        // Get reference to the ImageButton
        ImageButton btnChat = view.findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(v -> showChatDialog());

        return view;
    }

    // Method to show the chat dialog
    private void showChatDialog() {
        Dialog chatDialog = new Dialog(getActivity());
        chatDialog.setContentView(R.layout.chat_dialog);
        chatDialog.setCancelable(true);

        // Find views in the chat dialog layout
        EditText etMessage = chatDialog.findViewById(R.id.et_message);
        Button btnSend = chatDialog.findViewById(R.id.btn_send);
        LinearLayout chatContainer = chatDialog.findViewById(R.id.chat_container);

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (!message.isEmpty()) {
                // Add message to chat container
                TextView chatMessage = new TextView(getActivity());
                chatMessage.setText(message);
                chatContainer.addView(chatMessage);
                etMessage.setText(""); // Clear the input field
            }
        });

        chatDialog.show();
    }
}
