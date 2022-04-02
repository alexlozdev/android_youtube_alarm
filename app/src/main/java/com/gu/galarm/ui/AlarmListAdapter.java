package com.gu.galarm.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gu.galarm.ui.activity.AddAlarmActivity;
import com.gu.galarm.R;
import com.gu.galarm.alarmmanager.AlarmHandler;
import com.gu.galarm.db.Alarm;
import com.gu.galarm.ui.activity.MainActivity;
import com.gu.galarm.viewmodel.AlarmViewModel;
import com.suke.widget.SwitchButton;

import java.util.Collections;
import java.util.List;

/**
 * ArrayAdapter used to populate MainActivity Alarms ListView
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private final String TAG = "AlarmListAdapter";

    /**
     * View for each alarm
     */
    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relativeLayoutItem;
        private final TextView timeTextView;
        private final TextView repetitionTextView;
        private final SwitchButton activeSwitch;
        private final ImageButton editButton;
        private final Button deleteButton;
        private final LinearLayout llItem;


        private AlarmViewHolder(View itemView) {
            super(itemView);
            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
            timeTextView = itemView.findViewById(R.id.alarm_time_text);
            repetitionTextView = itemView.findViewById(R.id.alarm_repetition_text);
            activeSwitch = (SwitchButton) itemView.findViewById(R.id.alarm_active_switch);
            editButton = itemView.findViewById(R.id.alarm_edit_button);
            deleteButton = (Button) itemView.findViewById(R.id.btnDelete);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);

            activeSwitch.setChecked(true);
            activeSwitch.isChecked();
            activeSwitch.toggle();     //switch state
            activeSwitch.toggle(true);//switch without animation
            activeSwitch.setShadowEffect(true);//disable shadow effect
            activeSwitch.setEnabled(true);//disable button
            activeSwitch.setEnableEffect(false);//disable the switch animation
        }
    }

    // Layout members
    private final LayoutInflater mInflater;
    private final TextView mEmptyTextView;
    // Data list (cached copy of alarms)
    private List<Alarm> mAlarms = Collections.emptyList();
    // To handle interactions with database
    private AlarmViewModel mAlarmViewModel;
    // To schedule alarms
    private AlarmHandler mAlarmHandler;

    public AlarmListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mEmptyTextView = ((MainActivity) context).findViewById(R.id.no_alarms_text);

        mAlarmViewModel = ViewModelProviders.of((MainActivity) context).get(AlarmViewModel.class);
        mAlarmHandler = new AlarmHandler(context);



        setHasStableIds(true); // so Switch interaction has smooth animations
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder viewHolder, int position) {
        Resources resources = viewHolder.itemView.getContext().getResources();
        Alarm alarm = mAlarms.get(position);
        boolean bEdit = MainActivity.mbEdit;
        boolean bDelete = false;

        viewHolder.timeTextView.setText(alarm.getStringTime()); // set alarm time

        if (bEdit) {
            viewHolder.editButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editButton.setVisibility(View.GONE);
            viewHolder.deleteButton.setVisibility(View.GONE);
        }

        // Set repeatTextView text
        if (alarm.isRepeating()) {
            String repetitionText = alarm.getStringOfActiveDays();
            viewHolder.repetitionTextView.setText(repetitionText);
        } else {
            viewHolder.repetitionTextView.setText(resources.getString(R.string.no_repeat));
        }

        // Set TextView colors based on alarm's active state
        if (alarm.isActive()) {
            viewHolder.activeSwitch.setChecked(true);
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorAccent));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorDarkText));

            viewHolder.relativeLayoutItem.setBackgroundColor(resources.getColor(R.color.colorBg1));
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorText1));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorText1));

        } else {
            viewHolder.activeSwitch.setChecked(false);
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));

            viewHolder.relativeLayoutItem.setBackgroundColor(resources.getColor(R.color.colorBg2));
            viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorText2));
            viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorText2));
        }

        // Add Button click listeners
        addSwitchListener(alarm, viewHolder, resources);
        addEditButtonListener(alarm, viewHolder);
        addDeleteButtonListener(alarm, viewHolder);
        addEditAlarmListener(alarm, viewHolder);
    }

    /**
     * Update current list of alarms and update UI
     *
     * @param alarms List of updated alarms
     */
    public void setAlarms(List<Alarm> alarms) {
        Log.i(TAG, "updating alarms data-set");
        this.mAlarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        mEmptyTextView.setVisibility(mAlarms.size() > 0 ? View.GONE : View.VISIBLE);
        return mAlarms.size();
    }

    @Override
    public long getItemId(int position) {
        return mAlarms.get(position).id;
    }

    /**
     * Add OnCheckedChange Listener to alarm's "active" switch
     *
     * @param alarm      Alarm object
     * @param viewHolder Alarm's ViewHolder object, containing Switch
     * @param resources  Resources file to get color values from
     */

    private void addSwitchListener(final Alarm alarm, final AlarmViewHolder viewHolder, final Resources resources) {
        /*
        viewHolder.activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    alarm.setActive(true);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorAccent));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorDarkText));
                    // schedule alarm
                    Log.i(TAG, "scheduling alarm: " + alarm.id);
                    mAlarmHandler.scheduleAlarm(alarm);
                } else {
                    alarm.setActive(false);
                    viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                    viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorGrey500));
                    mAlarmHandler.cancelAlarm(alarm);
                }

                // Update database and schedule alarm
                Log.i(TAG, "updating database with alarm: " + alarm.id);
                mAlarmViewModel.updateActive(alarm);
            }
        });
        */

        viewHolder.activeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    alarm.setActive(true);
                    //viewHolder.relativeLayoutItem.setBackgroundColor(resources.getColor(R.color.colorBg1));
                    //viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorText1));
                    //viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorText1));
                    // schedule alarm
                    notifyDataSetChanged();
                    Log.i(TAG, "scheduling alarm: " + alarm.id);
                    mAlarmHandler.scheduleAlarm(alarm);
                    mAlarmViewModel.update(alarm);
                } else {
                    alarm.setActive(false);
                    //viewHolder.relativeLayoutItem.setBackgroundColor(resources.getColor(R.color.colorBg2));
                    //viewHolder.timeTextView.setTextColor(resources.getColor(R.color.colorText2));
                    //viewHolder.repetitionTextView.setTextColor(resources.getColor(R.color.colorText2));
                    notifyDataSetChanged();
                    mAlarmViewModel.update(alarm);
                    mAlarmHandler.cancelAlarm(alarm);
                }
            }
        });
    }

    /**
     * Add OnClickListener to alarm's edit button to open EditAlarmActivity (AddAlarmActivity.java)
     *
     * @param alarm      Alarm object
     * @param viewHolder Alarm's ViewHolder object, containing edit button
     */
    private void addEditButtonListener(final Alarm alarm, final AlarmViewHolder viewHolder) {
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
                viewHolder.editButton.setVisibility(View.GONE);
            }
        });
    }

    private void addDeleteButtonListener(final Alarm alarm, final AlarmViewHolder viewHolder) {
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.deleteButton.setVisibility(View.GONE);
                viewHolder.editButton.setVisibility(View.GONE);
                mAlarmHandler.cancelAlarm(alarm);
                mAlarmViewModel.delete(alarm);
                notifyDataSetChanged();
            }
        });
    }

    private void addEditAlarmListener(final Alarm alarm, final AlarmViewHolder viewHolder) {
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AddAlarmActivity.class);

                Bundle args = new Bundle();
                args.putParcelable(AddAlarmActivity.EXTRA_ALARM, alarm);
                intent.putExtra(AddAlarmActivity.EXTRA_BUNDLE, args);

                ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE);

            }
        });
    }

}
