package com.loan.staffmgr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.loan.staffmgr.R;
import com.loan.staffmgr.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}