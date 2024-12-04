package com.example.eversafebaguio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Field;

public class NotifFragment extends Fragment {

    private static final String CHANNEL_ID = "high_priority_channel";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notif, container, false);

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

        // Create Notification Channel
        createNotificationChannel();

        // Find delete icons
        ImageView deleteFloodWarning = view.findViewById(R.id.deleteFloodWarning);
        ImageView deleteEvacuationOrder = view.findViewById(R.id.deleteEvacuationOrder);

        ImageView userIcon = view.findViewById(R.id.icon_right);
        userIcon.setOnClickListener(v -> Toast.makeText(requireContext(), "User Profile", Toast.LENGTH_SHORT).show());

        TextView markAsReadFlood = view.findViewById(R.id.markAsReadFlood);
        markAsReadFlood.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Marked as Read", Toast.LENGTH_SHORT).show();
            showHeadsUpNotification(
                    "Flood Warning in Barangay",
                    "Heavy rains are expected to continue and floodwaters may rise further. Stay alert and monitor local updates."
            );
        });

        TextView markAsReadEvacuation = view.findViewById(R.id.markAsReadEvacuation);
        markAsReadEvacuation.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Marked as Read", Toast.LENGTH_SHORT).show();
            showHeadsUpNotification(
                    "Evacuation Order",
                    "Evacuation order in effect for Residents. Please follow the evacuation procedures and move to the nearest safe area."
            );
        });

        // Set OnClickListeners for delete icons
        deleteFloodWarning.setOnClickListener(v -> showPopupMenu(v, "Flood Warning"));
        deleteEvacuationOrder.setOnClickListener(v -> showPopupMenu(v, "Evacuation Order"));

        // Automatically trigger notification when the fragment is opened
        showHeadsUpNotification(
                "Flood Warning in Barangay",
                "Heavy rains are expected to continue and floodwaters may rise further. Stay alert and monitor local updates."
        );

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "High Priority Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Heads-up notifications for alerts");
            NotificationManager manager = requireActivity().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showHeadsUpNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_flood_24) // Replace with your app's small icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showPopupMenu(View view, String notificationType) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.notification_menu, popupMenu.getMenu());

        // Force icons to display in the popup menu
        try {
            Field popup = PopupMenu.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            Object menuPopupHelper = popup.get(popupMenu);
            Class<?> helperClass = Class.forName(menuPopupHelper.getClass().getName());
            helperClass.getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Handle menu clicks
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_delete) {
                Toast.makeText(requireContext(), notificationType + " deleted", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_report) {
                Toast.makeText(requireContext(), "Reported " + notificationType, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
}
