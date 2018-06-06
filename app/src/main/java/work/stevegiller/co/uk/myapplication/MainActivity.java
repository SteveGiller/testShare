package work.stevegiller.co.uk.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    Button startButton;
    TextView countdownText;
    TextView halfwayAlert;

    String countdownValue;
    boolean passedHalfway = false;
    long timerLength = 15500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_button);
        countdownText = findViewById(R.id.text_countdown);
        halfwayAlert = findViewById(R.id.text_halfway_point);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CountDownService.class);
                intent.putExtra("timer", timerLength);
                startService(intent);
                Log.i(TAG, "Started Service");
                startButton.setEnabled(false);
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    private void updateGUI(Intent intent) {
        if(intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            countdownValue = String.format("Seconds (milliseconds) remaining: %d (%d)", millisUntilFinished / 1000, millisUntilFinished);
            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
            countdownText.setText(countdownValue);
            if(millisUntilFinished <= timerLength /2 && passedHalfway == false) {
                passedHalfway = true;
                halfwayAlert.setText("HALFWAY POINT");
            } else {
                halfwayAlert.setText("");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(CountDownService.COUNTDOWN_BR));
        Log.i(TAG, "Registered Broadcast Receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered Broadcast Receiver in onPause");
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(br);
            Log.i(TAG, "Unregistered Broadcast Receiver in onStop");
        } catch (Exception e) {
            Log.i(TAG, "Could not unregister Broadcast Receiver in onStop");
            Log.i(TAG, "Assume Broadcast Receiver was unregistered in onPause");
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CountDownService.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

}
