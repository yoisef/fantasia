package com.app.world.fantasia.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.app.world.fantasia.R;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;


public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        interstialads();
    }

    private void initView() {
        setContentView(R.layout.activity_settings);

        // replace linear layout by preference screen
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();

        initToolbar(true);
        setToolbarTitle(getString(R.string.settings));
        enableUpButton();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }

    public void interstialads()
    {
        StartAppSDK.init(this, "203750050", true);
        StartAppAd.enableAutoInterstitial();
        StartAppAd.setAutoInterstitialPreferences(
                new AutoInterstitialPreferences()
                        .setSecondsBetweenAds(60)
        );
    }
}