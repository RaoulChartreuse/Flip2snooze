package fr.lmorin.flip2snooze;

import android.support.annotation.Nullable;

import java.net.URI;
import java.util.UUID;

public class AlarmRecord{
    int mHeure, mMinute;
    String mStringHeure;
    String j;
    String n;
    URI a_resource;
    UUID mId;
    Boolean mIsActive;

    public AlarmRecord(int heure, int minute, String jour, String name){
        init(heure, minute, jour, name, null);
    }

    public AlarmRecord(int heure, int minute, String jour, String name, URI Audioresource){
        init(heure, minute, jour, name, Audioresource);
    }

    private void init(int heure, int minute, String jour, String name, @Nullable URI Audioresource){

        //TODO (Non prioritaire) rajouter la prise en charde de audioressource
        mHeure = heure;
        mMinute = minute;
        mStringHeure = String.format("%02d:%02d",mHeure, mMinute);
        j = jour;
        n = name;
        a_resource = Audioresource;
        mId = UUID.randomUUID();
        mIsActive=true;
    }


}


;