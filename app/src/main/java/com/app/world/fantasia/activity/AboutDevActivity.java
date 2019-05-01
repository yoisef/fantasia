package com.app.world.fantasia.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.world.fantasia.R;
import com.startapp.android.publish.adsCommon.StartAppAd;

public class AboutDevActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_about_dev);

        initToolbar(true);
        setToolbarTitle(getString(R.string.about_dev));
        enableUpButton();
        interstialads();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }
}

