package com.gu.galarm.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.galarm.listener.OnAddAlarmListener;
import com.gu.galarm.R;

public class RepeatActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mTvAdd;
    public static OnAddAlarmListener mListener;

    LinearLayout mLlSunday;
    LinearLayout mLlMonday;
    LinearLayout mLlThuesday;
    LinearLayout mLlWedensday;
    LinearLayout mLlThursday;
    LinearLayout mLlFriday;
    LinearLayout mLlSaturday;

    CheckBox mChkSunday;
    CheckBox mChkMonday;
    CheckBox mChkThuesday;
    CheckBox mChkWedensday;
    CheckBox mChkThursday;
    CheckBox mChkFriday;
    CheckBox mChkSaturday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);

        mTvAdd = (TextView) findViewById(R.id.tvTitleAdd);
        mTvAdd.setOnClickListener(this);

        mLlSunday = (LinearLayout) findViewById(R.id.llSunday);
        mLlMonday = (LinearLayout) findViewById(R.id.llMonday);
        mLlThuesday = (LinearLayout) findViewById(R.id.llThuesday);
        mLlWedensday = (LinearLayout) findViewById(R.id.llWednesday);
        mLlThursday = (LinearLayout) findViewById(R.id.llThursday);
        mLlFriday = (LinearLayout) findViewById(R.id.llFriday);
        mLlSaturday = (LinearLayout) findViewById(R.id.llSaturday);

        mLlSunday.setOnClickListener(this);
        mLlMonday.setOnClickListener(this);
        mLlThuesday.setOnClickListener(this);
        mLlWedensday.setOnClickListener(this);
        mLlThursday.setOnClickListener(this);
        mLlFriday.setOnClickListener(this);
        mLlSaturday.setOnClickListener(this);

        mChkSunday = (CheckBox) findViewById(R.id.chkSunday);
        mChkMonday = (CheckBox) findViewById(R.id.chkMonday);
        mChkThuesday = (CheckBox) findViewById(R.id.chkThuesday);
        mChkWedensday = (CheckBox) findViewById(R.id.chkWednesday);
        mChkThursday = (CheckBox) findViewById(R.id.chkThursday);
        mChkFriday = (CheckBox) findViewById(R.id.chkFriday);
        mChkSaturday = (CheckBox) findViewById(R.id.chkSaturday);

        mChkSunday.setChecked(AddAlarmActivity.mAlarm.activeDays[0]);
        mChkMonday.setChecked(AddAlarmActivity.mAlarm.activeDays[1]);
        mChkThuesday.setChecked(AddAlarmActivity.mAlarm.activeDays[2]);
        mChkWedensday.setChecked(AddAlarmActivity.mAlarm.activeDays[3]);
        mChkThursday.setChecked(AddAlarmActivity.mAlarm.activeDays[4]);
        mChkFriday.setChecked(AddAlarmActivity.mAlarm.activeDays[5]);
        mChkSaturday.setChecked(AddAlarmActivity.mAlarm.activeDays[6]);
    }

    @Override
    public void onClick(View v) {
        boolean bChk = true;
        switch (v.getId()) {
            case R.id.tvTitleAdd:
                addAlarm();
                break;

            case R.id.llSunday:
                bChk = mChkSunday.isChecked();
                mChkSunday.setChecked(!bChk);
                break;

            case R.id.llMonday:
                bChk = mChkMonday.isChecked();
                mChkMonday.setChecked(!bChk);
                break;

            case R.id.llThuesday:
                bChk = mChkThuesday.isChecked();
                mChkThuesday.setChecked(!bChk);
                break;

            case R.id.llWednesday:
                bChk = mChkWedensday.isChecked();
                mChkWedensday.setChecked(!bChk);
                break;

            case R.id.llThursday:
                bChk = mChkThursday.isChecked();
                mChkThursday.setChecked(!bChk);
                break;

            case R.id.llFriday:
                bChk = mChkFriday.isChecked();
                mChkFriday.setChecked(!bChk);
                break;

            case R.id.llSaturday:
                bChk = mChkSaturday.isChecked();
                mChkSaturday.setChecked(!bChk);
                break;

        }
    }

    private void addAlarm() {
        if (mListener != null) {
            AddAlarmActivity.mAlarm.activeDays[0] = mChkSunday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[1] = mChkMonday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[2] = mChkThuesday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[3] = mChkWedensday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[4] = mChkThursday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[5] = mChkFriday.isChecked();
            AddAlarmActivity.mAlarm.activeDays[6] = mChkSaturday.isChecked();
            mListener.onRepeatClicked();
        }

        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
