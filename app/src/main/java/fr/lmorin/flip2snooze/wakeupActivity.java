package fr.lmorin.flip2snooze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class wakeupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        Intent intent = getIntent();
        String id =intent.getStringExtra("alarmId");
        AlarmRecordManager alarmRecordManager = new AlarmRecordManager(getApplicationContext());
        AlarmRecord alarm = alarmRecordManager.finById(id);


        View vAlarm = getLayoutInflater().inflate( R.layout.list_cell, null);
        LinearLayout layout = findViewById(R.id.wakeupLayout);
        layout.addView(vAlarm);
        TextView name = ((TextView) findViewById(R.id.name));
        TextView description = ((TextView) findViewById(R.id.jour));
        TextView heure = findViewById(R.id.heure);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        Switch IsActive = findViewById(R.id.switch1);
        deleteButton.setVisibility(View.INVISIBLE);

        name.setText(alarm.n);
        heure.setText(alarm.mStringHeure);

        description.setText(alarm.j);
        IsActive.setChecked(alarm.mIsActive);


        /*TODO Remove the alarm when done
        Intent intent = getIntent();
intent.getExtra("someKey")
         */

          /*  <TextView
        android:id="@+id/wakeupTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Alarme"
        android:textSize="?android:attr/textAppearanceLarge" />*/
    }
}
