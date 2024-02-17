package com.loan.staffmgr.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.loan.staffmgr.R;
import com.loan.staffmgr.base.BaseActivity;
import com.loan.staffmgr.ui.fragment.AboutFragment;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_color));
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_about);
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        replaceFragment(new AboutFragment(), R.id.fl_container);
    }
}
