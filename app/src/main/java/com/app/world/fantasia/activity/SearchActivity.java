package com.app.world.fantasia.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;
import com.app.world.fantasia.R;
import com.app.world.fantasia.data.constant.AppConstant;
import com.app.world.fantasia.fragment.PostListFragment;
import com.app.world.fantasia.utility.AdsUtilities;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class SearchActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private Fragment mPostListFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        interstialads();
    }

    private void initVar() {
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

    }

    private void initView() {
        setContentView(R.layout.activity_search);
        mFragmentManager = getSupportFragmentManager();
        mPostListFragment = mFragmentManager.findFragmentById(R.id.fragment_container);


        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
    }

    private void initFunctionality() {
        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Bundle args = new Bundle();
                mPostListFragment = new PostListFragment();
                args.putInt(AppConstant.BUNDLE_KEY_CATEGORY_ID, AppConstant.BUNDLE_KEY_SEARCH_POST_ID);
                args.putString(AppConstant.BUNDLE_KEY_SEARCH_TEXT, newText);
                mPostListFragment.setArguments(args);
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mPostListFragment)
                        .commit();

                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
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