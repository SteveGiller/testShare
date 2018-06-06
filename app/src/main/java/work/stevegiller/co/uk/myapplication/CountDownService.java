package work.stevegiller.co.uk.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class CountDownService extends Service {

    private final static String TAG = "CountDownService";

    public final static String COUNTDOWN_BR = "work.stevegiller.co.uk.myapplication.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    private long timer;

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = intent.getLongExtra("timer", 30000);
        Log.i(TAG, "Grabbed a timer value of: " + timer + " in onStart");

        Log.i(TAG, "Starting Timer ...");
        cdt = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        };
        cdt.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Log.i("TAG", "Timer Cancelled");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
