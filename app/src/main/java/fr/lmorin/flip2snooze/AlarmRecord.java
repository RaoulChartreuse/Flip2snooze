package fr.lmorin.flip2snooze;

import android.support.annotation.Nullable;

import java.net.URI;

public class AlarmRecord{
    String cal;
    String j;
    String n;
    URI a_resource;

    public AlarmRecord(String calendar, String jour, String name){
        init(calendar, jour, name, null);
    }

    public AlarmRecord(String calendar, String jour, String name, URI Audioresource){
        init(calendar, jour, name, Audioresource);
    }

    private void init(String calendar, String jour, String name, @Nullable URI Audioresource){
        cal = calendar;
        j = jour;
        n = name;
        a_resource = Audioresource;

    }


}
