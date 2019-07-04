package fr.lmorin.flip2snooze;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {


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
       private PopupWindow mPopupWindow;

       public MyViewHolder(final View itemView) {

           super(itemView);


           name = itemView.findViewById(R.id.name);
           name.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   showMyPopupWindow(view, false, true, false);
               }

           });

           description = itemView.findViewById(R.id.jour);
           description.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   showMyPopupWindow(view, false, false, true);
               }

           });
           heure = itemView.findViewById(R.id.heure);
           heure.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   showMyPopupWindow(view, true, false, false);
               }

           });
           deleteButton = itemView.findViewById(R.id.deleteButton);
           deleteButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   removeAlarmRecord(getAdapterPosition());
                   notifyItemRemoved(getAdapterPosition());
                   notifyItemRangeChanged(getAdapterPosition(),getItemCount());
               }
           });

           itemView.setOnClickListener(new OnClickListener() {

               @Override

               public void onClick(View view) {

                   showMyPopupWindow(view, true, true, true);
               }

           });

           IsActive = itemView.findViewById(R.id.switch1);
           IsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   currentAlarm.mIsActive = isChecked;
                   if (isChecked) {
                       alarmRecordManager.registerAlarm(currentAlarm);

                   } else {
                       alarmRecordManager.unregisterAlarm(currentAlarm);
                   }
               }
           });

       }

       private void showMyPopupWindow(View view, final boolean showTime, final boolean showJour, final boolean showNom) {

           LayoutInflater inflater = (LayoutInflater) view.getContext().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
           final View customView;
           customView = inflater.inflate(R.layout.popup_add_alarm, null);
           mPopupWindow = new PopupWindow(customView,
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                   ViewGroup.LayoutParams.WRAP_CONTENT);
           ImageButton closeButton = customView.findViewById(R.id.ib_close);
           closeButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View view) {
                   // Dismiss the popup window
                   mPopupWindow.dismiss();
               }
           });

           final TimePicker picker = customView.findViewById(R.id.timePicker);
           picker.setIs24HourView(true);
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               picker.setHour(currentAlarm.mHeure);
               picker.setMinute(currentAlarm.mMinute);
           } else {
               picker.setCurrentHour(currentAlarm.mHeure);
               picker.setCurrentMinute(currentAlarm.mMinute);
           }
           picker.setVisibility(showTime ? View.VISIBLE : View.GONE);

           TextView tJour = customView.findViewById(R.id.tJour);
           tJour.setVisibility(showJour ? View.VISIBLE : View.GONE);
           final EditText qJour = customView.findViewById(R.id.qJour);
           qJour.setVisibility(showJour ? View.VISIBLE : View.GONE);

           TextView tNom = customView.findViewById(R.id.tNom);
           tNom.setVisibility(showNom ? View.VISIBLE : View.GONE);
           final EditText qNom = customView.findViewById(R.id.qNom);
           qNom.setVisibility(showNom ? View.VISIBLE : View.GONE);

           Button bModifier = customView.findViewById(R.id.addButton);
           bModifier.setText(view.getContext().getString(R.string.modifier));
           bModifier.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (showJour) currentAlarm.j = qJour.getText().toString();
                   if (showNom) currentAlarm.n = qNom.getText().toString();
                   if (showTime) {
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           currentAlarm.mHeure = picker.getHour();
                           currentAlarm.mMinute = picker.getMinute();
                           currentAlarm.mStringHeure = String.format("%02d:%02d",
                                   picker.getHour(),
                                   picker.getMinute());
                       } else {
                           currentAlarm.mHeure = picker.getCurrentHour();
                           currentAlarm.mMinute = picker.getCurrentMinute();
                           currentAlarm.mStringHeure = String.format("%02d:%02d",
                                   picker.getCurrentHour(),
                                   picker.getCurrentMinute());
                       }

                   }

                   mPopupWindow.dismiss();
               }
           });
           mPopupWindow.setFocusable(true);
           //customView.findViewById(R.id.cl)
           mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

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
