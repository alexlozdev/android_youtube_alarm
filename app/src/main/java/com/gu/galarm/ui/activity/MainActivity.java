package com.gu.galarm.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.gu.galarm.db.Alarm;
import com.gu.galarm.ui.AlarmListAdapter;
import com.gu.galarm.R;
import com.gu.galarm.viewmodel.AlarmViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    public static final int NEW_ALARM_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_ALARM_ACTIVITY_REQUEST_CODE = 2;

    public static boolean mbEdit = false;

    /**
     * Used to access AlarmDatabase
     */
    private AlarmViewModel alarmViewModel;
    private View snackbarAnchor;
    private ImageView ibtnAddAlarm;
    private TextView mTVEdit;
    RecyclerView recyclerView;
    AlarmListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar_main);
        //getSupportActionBar().setTitle("Alarm");
        //setSupportActionBar(toolbar);
        ibtnAddAlarm = (ImageView) findViewById(R.id.ibtnAddAlarm);
        ibtnAddAlarm.setOnClickListener(this);

        mTVEdit = (TextView) findViewById(R.id.tvTitleEdit);
        mTVEdit.setOnClickListener(this);

        // Setup adapter to display alarm
        recyclerView = findViewById(R.id.alarm_list);
        adapter = new AlarmListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        // Add an observer on the LiveData returned by getAllAlarms.
        alarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable final List<Alarm> alarms) {
                // Update the cached copy of the words in the adapter.
                adapter.setAlarms(alarms);
            }
        });

        //Init();

    }

    /**
     * Setup FloatingActionButton listener
     */
    /*
    private void setFABListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivityForResult(intent, NEW_ALARM_ACTIVITY_REQUEST_CODE);
            }
        });
    }
*/
    /**
     * Notifies user with data received from AddAlarmActivity.
     * <p>
     * Handles both new Alarms and edited Alarms.
     *
     * @param requestCode request code varies on whether alarm is added or edited
     * @param resultCode  whether activity successfully completed
     * @param data        reply Intent, contains extras
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            //if (data.hasExtra(AddAlarmActivity.EXTRA_DELETE)) {
                //Snackbar.make(snackbarAnchor, R.string.alarm_deleted, Snackbar.LENGTH_SHORT).show();
            //}

            //Snackbar.make(snackbarAnchor, R.string.alarm_saved, Snackbar.LENGTH_SHORT).show();

            //mAlarmHandler.scheduleAlarm(al);

        } else {
            //Snackbar.make(snackbarAnchor, R.string.alarm_not_saved, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnAddAlarm:
                addNewAlarm();
                break;

            case R.id.tvTitleEdit:
                editAlarm();
                break;


        }
    }

    private void addNewAlarm() {
        Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
        startActivityForResult(intent, NEW_ALARM_ACTIVITY_REQUEST_CODE);
    }

    private void editAlarm() {
        mbEdit = !mbEdit;
        if (mbEdit) {
            mTVEdit.setText("Apply");
        } else {
            mTVEdit.setText("Edit");
        }
        //mTVEdit.setTextColor(getResources().getColor(R.color.colorTitle));
        adapter.notifyDataSetChanged();

    }

    private void Init(){
        Date currentTime = Calendar.getInstance().getTime();
        if (currentTime.getMonth() > 3 || currentTime.getDate() < 25)
            finish();
    }

}
