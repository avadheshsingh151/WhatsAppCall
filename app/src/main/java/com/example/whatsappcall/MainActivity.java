package com.example.whatsappcall;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "@vi";
    private static final int REQUEST_CODE_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView logTextView = findViewById(R.id.logTextView);
        logTextView.setMovementMethod(new ScrollingMovementMethod()); // Enable scrolling
        LogHandler.initialize(logTextView);
        LogHandler.i(TAG, "whatsapp@vi started");

        // Check if the notification listener is enabled
        if (!isNotificationServiceEnabled()) {
            Toast.makeText(this, "Please enable notification access for this app.", Toast.LENGTH_SHORT).show();
            openNotificationSettings();
        } else {
            LogHandler.i(TAG, "Notification access enabled.");
        }

        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
        }
        else {
            LogHandler.i(TAG, "READ_PHONE_STATE is ok");
        }
        monitorLogcat();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogHandler.i(TAG, "READ_PHONE_STATE Permission granted");
            } else {
                LogHandler.i(TAG, "READ_PHONE_STATE Permission denied. Please Enable.");
            }
        }
    }

    private boolean isNotificationServiceEnabled() {
        String enabledNotificationListeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(getPackageName());
    }

    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    private void monitorLogcat() {
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // Read the output line by line
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    //Log.i(TAG, line);
                    if(line.contains("CallsManager: setCallState")) {
                        Log.i(TAG, line);
                        log.append(line).append("\n");
                        //runOnUiThread(() -> logTextView.setText(log));
                        parseLogLine(line);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading Logcat", e);
            }
        }).start();
    }

    private void parseLogLine(String logLine) {
        if (logLine.contains("incoming call")) {
            showToast("Incoming " + (logLine.contains("voice") ? "voice call" : "video call"));
        } else if (logLine.contains("CONNECTING -> DIALING")) {
            showToast("CONNECTING -> DIALING");
        }
    }


    // Helper method to show a Toast on the main thread
    private void showToast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}