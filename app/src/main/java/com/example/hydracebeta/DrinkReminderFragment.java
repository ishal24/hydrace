package com.example.hydracebeta;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class DrinkReminderFragment extends Fragment {

    private int drinkCount = 0;
    private TextView drinkCountText;
    private static final String CHANNEL_ID = "hydrace_channel";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drink_reminder, container, false);

        // Initialize UI elements
        drinkCountText = view.findViewById(R.id.drink_count_text);
        Button incrementButton = view.findViewById(R.id.increment_button);
        Button decrementButton = view.findViewById(R.id.decrement_button);
        Button notifyButton = view.findViewById(R.id.notify_button);  // Notify button

        // Set up button click listeners
        incrementButton.setOnClickListener(v -> {
            drinkCount++;
            updateDrinkCount();
        });

        decrementButton.setOnClickListener(v -> {
            if (drinkCount > 0) {
                drinkCount--;
            }
            updateDrinkCount();
        });

        // Notify button click listener to send notification
        notifyButton.setOnClickListener(v -> sendNotification());

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        sendNotification();
                    }
                });

        // Create notification channel for Android 8.0 and above
        createNotificationChannel();

        return view;
    }

    // Update the drink count display
    private void updateDrinkCount() {
        drinkCountText.setText(drinkCount + " Cups");
    }

    private void sendNotification() {
        // Check Android version and permission status
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                    .setContentTitle("Drink Reminder")
                    .setContentText("Time to drink water! Stay hydrated!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensures heads-up for pre-Oreo devices
                    .setDefaults(NotificationCompat.DEFAULT_ALL) // Sound, vibration, and lights
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            notificationManager.notify(1, builder.build());

        } else {
            // Request POST_NOTIFICATIONS permission if not granted (Android 13+ only)
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Drink Reminder Channel";
            String description = "Channel for drink reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH; // High importance for heads-up notifications
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // Show on lock screen

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
