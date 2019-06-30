package fr.lmorin.flip2snooze;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmRecordManager{
    private List<AlarmRecord> alarmRecordList ;
    private SharedPreferences Prefs;
    private String KEY;
    Context mContext;

    private void registerAlarm(AlarmRecord alarmRecord){
        Intent alarmIntent = new Intent(mContext.getApplicationContext(), wakeupActivity.class);
        alarmIntent.setData(Uri.parse("custom://"+alarmRecord.mId));
        alarmIntent.putExtra("alarmId", alarmRecord.mId);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        //PendingIntent displayIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        PendingIntent displayIntent = PendingIntent.getActivity(mContext, 0, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmRecord.mHeure);
        calendar.set(Calendar.MINUTE, alarmRecord.mMinute);


        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), displayIntent );
        alarmRecord.mIsActive = true;
    }

    void unregisterAlarm(AlarmRecord alarmRecord){
        Intent alarmIntent = new Intent(mContext.getApplicationContext(), wakeupActivity.class);
        alarmIntent.setData(Uri.parse("custom://"+alarmRecord.mId));
        alarmIntent.putExtra("alarmId", alarmRecord.mId);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        //PendingIntent displayIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        PendingIntent displayIntent = PendingIntent.getActivity(mContext, 0, alarmIntent, 0);

        alarmRecord.mIsActive = false;
        alarmManager.cancel(displayIntent);
    }

    public void addAlarmRecord(AlarmRecord alarmRecord){
        alarmRecordList.add(alarmRecord);
        registerAlarm(alarmRecord);
        updateAlarmList();
    }

    public void removeAlarmRecord(int index){
        unregisterAlarm(alarmRecordList.get(index));
        alarmRecordList.remove(index);
        updateAlarmList();
    }

    private void updateAlarmList(){


        //TODO et peut etre a part dans une class singleton pour gere les alarmes

        Thread background = new Thread(new Runnable() {

            @Override
            public void run() {
                Gson gson = new Gson();
                String jsonText = gson.toJson(alarmRecordList);
                SharedPreferences.Editor editor = Prefs.edit();
                editor.putString(KEY, jsonText);
                editor.apply();

            }
        });

        background.start();

        //TODO verifier que cette ligne n'est plus utile
        //notifyDataSetChanged();

    }


    protected AlarmRecordManager(Context context){
        mContext = context;
        KEY = mContext.getString(R.string.pref_alarmArray);
        Prefs = mContext.getSharedPreferences( KEY , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = Prefs.getString(KEY, null);
        if (jsonText == null){
            alarmRecordList = new ArrayList<>();
        }
        else
            alarmRecordList = gson.fromJson(jsonText,  new TypeToken<ArrayList<AlarmRecord>>(){}.getType());
    }


    public int getSize() {
        return alarmRecordList.size();
    }

    public AlarmRecord get(int position) {
        return alarmRecordList.get(position);
    }

    public AlarmRecord finById(String id) {
        //TODO reflechir a la possibilité d'une table de Hashage
        // Pro : Temps linéaire
        // Con : On a probablement moins d'une 50 aine d'alarme
        for (AlarmRecord al : alarmRecordList){
            if(al.mId.toString().equals(id)) return al;

        }
        return null;

    }
}
