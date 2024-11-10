package com.example.eversafebaguio;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CallActivity extends AppCompatActivity {

    private TextView callTimer;
    private Handler handler = new Handler();
    private int seconds = 0;
    private boolean isCalling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        callTimer = findViewById(R.id.text_call_timer);
        ImageButton endCallButton = findViewById(R.id.btn_dialend);

        startCallTimer();

        endCallButton.setOnClickListener(v -> {
            isCalling = false;
            finish();
        });
    }

    private void startCallTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isCalling) {
                    int minutes = seconds / 60;
                    int secs = seconds % 60;
                    String time = String.format("%02d:%02d", minutes, secs);
                    callTimer.setText(time);
                    seconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCalling = false;
    }
}
