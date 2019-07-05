package fr.lmorin.flip2snooze;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

// This activity is a display activity
// No deleting of alarm !
public class wakeupActivity extends AppCompatActivity {
    private static final String TAG = "flip2Snooze";
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    MediaPlayer mp;
    private float[] gravity ;
    Ringtone ringtone;
    long tStart;
    Intent snoozeIntent;
    AlarmRecord alarm;
    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Rien a faire en cas de changement de précision
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            long tNow = SystemClock.elapsedRealtime();
            double dt = (tNow - tStart) * .001;
            final double t_flip = 1.5; //TODO better be in preference setting (to be implemented)
            tStart = tNow;

            final float alpha = (float) exp(-3 * dt / t_flip);
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] - (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] - (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] - (1 - alpha) * sensorEvent.values[2];


        }
    };
    private boolean stopNow = false;


    private final String FLIP_DURATION = "flip duration";
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int duration = msg.getData().getInt(FLIP_DURATION);
            ringtone.stop();
            mp.start();

            String text = "flipped!";

            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();

            snoozeIntent = new Intent(getBaseContext(), wakeupActivity.class);
            snoozeIntent.setData(Uri.parse("Snooze://" + alarm.mId));
            snoozeIntent.putExtra("alarmId", alarm.mId.toString());
            PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), 0, snoozeIntent, 0);

            long tNow = SystemClock.elapsedRealtime();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long tSnooze = 2; //min//TODO set in preference
            alarmManager.set(AlarmManager.RTC_WAKEUP, tNow + (tSnooze * 60 * 1000), displayIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        Intent intent = getIntent();
        String id =intent.getStringExtra("alarmId");
        AlarmRecordManager alarmRecordManager = new AlarmRecordManager(getApplicationContext());
        alarm = alarmRecordManager.finById(id);


        View vAlarm = getLayoutInflater().inflate( R.layout.list_cell, null);
        LinearLayout layout = findViewById(R.id.wakeupLayout);
        layout.addView(vAlarm);
        TextView name = findViewById(R.id.name);
        TextView description = findViewById(R.id.jour);
        TextView heure = findViewById(R.id.heure);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        Switch IsActive = findViewById(R.id.switch1);
        deleteButton.setVisibility(View.INVISIBLE);

        name.setText(alarm.n);
        heure.setText(alarm.mStringHeure);

        description.setText(alarm.j);
        IsActive.setChecked(alarm.mIsActive);


        Button stop = new Button(this);

        layout.addView(stop);
        stop.setText("STOP !");
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),  alert);
        ringtone.setStreamType(AudioManager.STREAM_RING);

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringtone.stop();
                snoozeIntent = new Intent(getBaseContext(), wakeupActivity.class);
                snoozeIntent.setData(Uri.parse("Snooze://"+alarm.mId));
                snoozeIntent.putExtra("alarmId", alarm.mId.toString());
                PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), 0, snoozeIntent,0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.mIsActive = false;
                alarmManager.cancel(displayIntent);
                stopNow = true;
                finish();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mp = MediaPlayer.create(this, R.raw.piano);
        gravity = new float[]{0f, 0.1f, 0f};
        mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        tStart= SystemClock.elapsedRealtime();
        snoozeIntent = null;
        start_sequence();

    }

    private void start_sequence() {
        Thread background = new Thread(new Runnable() {
            Bundle messageBundle=new Bundle();
            Message myMessage;
            float ux, uy, uz;

            private boolean test(){
                float norm = (float) sqrt(gravity[0]*gravity[0]+gravity[1]*gravity[1]+gravity[2]*gravity[2]);
                float c_alpha = (ux * gravity[0] + uy * gravity[1] + uz * gravity[2]) / norm;
                Log.v(TAG, "c_alpha=" + c_alpha);
                return (c_alpha>cos(30.*Math.PI/180.)) && (norm > (9.81f*0.85f));

            }

            public void run() {
                try {

                    Thread.sleep((long) (1.5*1000));
                    long tStart = SystemClock.elapsedRealtime();
                    ringtone.play();
                    ux = -gravity[0];
                    uy = -gravity[1];
                    uz = -gravity[2];
                    double norm = 1 / sqrt(ux * ux + uy * uy + uz * uz);
                    ux = (float) (ux * norm);
                    uy = (float) (uy * norm);
                    uz = (float) (uz * norm);

                    while (!test()) {
                        Thread.sleep(200);
                        Log.v(TAG, String.format("u_ref = %f, %f, %f", ux, uy, uz ));
                        if (stopNow) throw new Throwable();
                    }
                    long tEnd = SystemClock.elapsedRealtime();

                    myMessage=handler.obtainMessage();
                    messageBundle.putInt(FLIP_DURATION, (int) (tEnd-tStart));
                    myMessage.setData(messageBundle);
                    handler.sendMessage(myMessage);
                }catch (Throwable t) {
                    // Finier le thread est suffisant
                }
            }

        });
        background.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);

    }
}
