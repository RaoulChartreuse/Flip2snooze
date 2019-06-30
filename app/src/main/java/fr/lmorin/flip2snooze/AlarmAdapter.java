package fr.lmorin.flip2snooze;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;


class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {


   AlarmRecordManager alarmRecordManager;



   @Override
   public int getItemCount() {

       return alarmRecordManager.getSize();

   }



    public void addAlarmRecord(AlarmRecord alarmRecord){
        // TODO en fait cette fct ca na rien a faire ici
        // Peut etre pour le notifier
        alarmRecordManager.addAlarmRecord(alarmRecord);
        notifyDataSetChanged();
    }

   public void removeAlarmRecord(int index){
       //TODO idem addAlarmRecord ??
       alarmRecordManager.removeAlarmRecord(index);
       notifyDataSetChanged();
   }



    public AlarmAdapter(Context context){
       alarmRecordManager = new AlarmRecordManager(context);

    }


   @Override
   public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       LayoutInflater inflater = LayoutInflater.from(parent.getContext());

       View view = inflater.inflate(R.layout.list_cell, parent, false);





       return new MyViewHolder(view);

   }


   @Override

   public void onBindViewHolder(MyViewHolder holder, int position) {

       AlarmRecord pair = alarmRecordManager.get(position);

       holder.display(pair);

   }


   public class MyViewHolder extends RecyclerView.ViewHolder {


       private final TextView name;

       private final TextView description;
       private final TextView heure;
       private final ImageButton deleteButton;
       private final Switch IsActive;

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

                           .setMessage(currentAlarm.j + " : "+currentAlarm.mHeure)

                           .show();

               }

           });

           IsActive = itemView.findViewById(R.id.switch1);

       }


       public void display(AlarmRecord alarm) {

           currentAlarm = alarm;

           name.setText(alarm.n);
           heure.setText(alarm.mStringHeure);

           description.setText(alarm.j);
           IsActive.setChecked(alarm.mIsActive);


       }

   }
}
