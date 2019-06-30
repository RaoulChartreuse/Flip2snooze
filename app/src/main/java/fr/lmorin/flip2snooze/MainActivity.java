package fr.lmorin.flip2snooze;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private PopupWindow mPopupWindow;
    private AlarmAdapter mAlarmAdapter;
    TimePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView rv = findViewById(R.id.mAlarmList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAlarmAdapter = new AlarmAdapter(getBaseContext());
        rv.setAdapter(mAlarmAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //final Dialog dialog = new Dialog();


                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View customView;
                customView = inflater.inflate(R.layout.popup_add_alarm, null);
                mPopupWindow = new PopupWindow(customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }
                ImageButton closeButton =  customView.findViewById(R.id.ib_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                //mPopupWindow.showAsDropDown(findViewById(R.id.cl));
                picker= findViewById(R.id.timePicker);



                final AlarmRecord alarm = new AlarmRecord(12,13, "All", "Default");



                Button mButton = customView.findViewById(R.id.addButton);
                mButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        EditText qJour = customView.findViewById(R.id.qJour);
                        alarm.j = qJour.getText().toString();
                        EditText qNom = customView.findViewById(R.id.qNom);
                        alarm.n = qNom.getText().toString();
                        mAlarmAdapter.addAlarmRecord(alarm);

                        mPopupWindow.dismiss();
                    }
                });
                picker= customView.findViewById(R.id.timePicker);
                picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        alarm.mHeure = hourOfDay;
                        alarm.mMinute = minute;
                        alarm.mStringHeure = String.format("%02d:%02d",hourOfDay,minute) ;

                    }
                });

                picker.setIs24HourView(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.setHour(12);
                    picker.setMinute(34);
                }
                else {
                    picker.setCurrentHour(12);
                    picker.setCurrentMinute(34);
                }

                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(findViewById(R.id.cl), Gravity.CENTER,0,0);

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_debug){
            Calendar calendar = Calendar.getInstance();
            long a = System.currentTimeMillis();
            calendar.setTimeInMillis(a);
            int h = calendar.get(calendar.HOUR_OF_DAY);
            int m = calendar.get(calendar.MINUTE);

            AlarmRecord alarm = new AlarmRecord(h, m , "J Debug", "Debug");
            mAlarmAdapter.addAlarmRecord(alarm);
            mAlarmAdapter.alarmRecordManager.unregisterAlarm(alarm);// I want a custom intent for debug
            Intent alarmIntent = new Intent(getApplicationContext(), wakeupActivity.class);
            alarmIntent.setData(Uri.parse("custom://"+alarm.mId));
            alarmIntent.putExtra("alarmId", alarm.mId.toString());
            startActivity(alarmIntent);


        }

        return super.onOptionsItemSelected(item);
    }
}
