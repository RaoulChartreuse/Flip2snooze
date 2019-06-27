package fr.lmorin.flip2snooze;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

/*   private List<AlarmRecord> alarmRecordList = Arrays.asList(
           new AlarmRecord( "12:12", "Lundi", "1Er Alarme"),
           new AlarmRecord( "09;08", "KJGH", "2eme Alarme"),
           new AlarmRecord( "591", "sdf", "Autre Alarme")
   );*/
   private List<AlarmRecord> alarmRecordList = new ArrayList<>(Arrays.asList(
           new AlarmRecord( "12:12", "Lundi", "1Er Alarme"),
           new AlarmRecord( "09;08", "KJGH", "2eme Alarme"),
           new AlarmRecord( "591", "sdf", "Autre Alarme") ));

      @Override

   public int getItemCount() {

       return alarmRecordList.size();

   }

   public void addAlarmRecord(AlarmRecord alarmRecord){
          alarmRecordList.add(alarmRecord);
   }

   public void removeAlarmRecord(int index){
          alarmRecordList.remove(index);
   }

   @Override

   public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       LayoutInflater inflater = LayoutInflater.from(parent.getContext());

       View view = inflater.inflate(R.layout.list_cell, parent, false);

       return new MyViewHolder(view);

   }


   @Override

   public void onBindViewHolder(MyViewHolder holder, int position) {

       AlarmRecord pair = alarmRecordList.get(position);

       holder.display(pair);

   }


   public class MyViewHolder extends RecyclerView.ViewHolder {


       private final TextView name;

       private final TextView description;
       private final TextView heure;
       private final ImageButton deleteButton;

       private AlarmRecord currentAlarm;


       public MyViewHolder(final View itemView) {

           super(itemView);


           name = ((TextView) itemView.findViewById(R.id.name));

           description = ((TextView) itemView.findViewById(R.id.jour));
           heure = itemView.findViewById(R.id.heure);
           deleteButton = itemView.findViewById(R.id.deleteButton);
           deleteButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   removeAlarmRecord(getAdapterPosition());
                   notifyItemRemoved(getAdapterPosition());
                   notifyItemRangeChanged(getAdapterPosition(),getItemCount());
               }
           });

           itemView.setOnClickListener(new View.OnClickListener() {

               @Override

               public void onClick(View view) {

                   new AlertDialog.Builder(itemView.getContext())

                           .setTitle(currentAlarm.n)

                           .setMessage(currentAlarm.j + " : "+currentAlarm.cal)

                           .show();

               }

           });

       }


       public void display(AlarmRecord alarm) {

           currentAlarm = alarm;

           name.setText(alarm.n);
           heure.setText(alarm.cal);

           description.setText(alarm.j);

       }

   }
}
