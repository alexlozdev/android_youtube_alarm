package com.gu.galarm.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.galarm.listener.OnAddAlarmListener;
import com.gu.galarm.R;

public class LabelActivity extends AppCompatActivity implements View.OnClickListener {

    public static OnAddAlarmListener mListener;
    EditText mEdtLabel;
    TextView mTvAdd;
    LinearLayout mLayAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        mTvAdd = (TextView) findViewById(R.id.tvTitleAdd);
        mTvAdd.setOnClickListener(this);

        mLayAdd = (LinearLayout) findViewById(R.id.llAdd);
        mLayAdd.setOnClickListener(this);

        mEdtLabel = (EditText) findViewById(R.id.edtLabel);
        mEdtLabel.setText(AddAlarmActivity.mAlarm.label);
        final Context context = this;

        mEdtLabel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER) ) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEdtLabel.getWindowToken(), 0);
                    return true;
                }

                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTitleAdd:
            case R.id.llAdd:
                addLabel();
                break;
        }
    }

    private void addLabel() {
        if (mListener != null) {
            AddAlarmActivity.mAlarm.label = mEdtLabel.getText().toString();
            mListener.onLabelClicked();
        }

        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
