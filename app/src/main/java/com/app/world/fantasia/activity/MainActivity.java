package com.app.world.fantasia.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.world.fantasia.R;
import com.google.android.gms.ads.AdView;

import com.app.world.fantasia.adapters.CategoryPagerAdapter;
import com.app.world.fantasia.api.ApiUtilities;
import com.app.world.fantasia.api.HttpParams;
import com.app.world.fantasia.data.constant.AppConstant;
import com.app.world.fantasia.data.sqlite.NotificationDbController;
import com.app.world.fantasia.models.category.Category;
import com.app.world.fantasia.models.notification.NotificationModel;
import com.app.world.fantasia.utility.ActivityUtilities;
import com.app.world.fantasia.utility.AdsUtilities;
import com.app.world.fantasia.utility.AppUtilities;
import com.app.world.fantasia.utility.RateItDialogFragment;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private RelativeLayout mNotificationView;
    private ImageButton mImgBtnSearch;

    private ArrayList<Category> mCategoryList;
    private ViewPager mViewPager;
    private CategoryPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private int mItemCount = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "203750050", true);



        RateItDialogFragment.show(this, getSupportFragmentManager());

        initVar();
        initView();
        loadData();
        initListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    // received new broadcast
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };


    @Override
    public void onBackPressed() {
        AppUtilities.tapPromptToExit(mActivity);
        StartAppAd.onBackPressed(this);
    }

    private void initVar() {
        mActivity = MainActivity.this;
        mContext = getApplicationContext();

        mCategoryList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mImgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mPagerAdapter = new CategoryPagerAdapter(mActivity, getSupportFragmentManager(), mCategoryList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        initToolbar(false);
        initDrawer();
        initLoader();
    }

    private void initListener() {
        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, NotificationListActivity.class, false);
            }
        });

        // Search button click listener
        mImgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);
            }
        });
    }

    private void loadData() {
        showLoader();

        loadCategories();

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void loadCategories() {
        ApiUtilities.getApiInterface().getCategories(mItemCount).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {

                    int totalPages = Integer.parseInt(response.headers().get(HttpParams.TOTAL_PAGE));

                    if (totalPages > 1) {
                        mItemCount = mItemCount * totalPages;
                        loadCategories();

                    } else {
                        mCategoryList.add(null); // Make space for Latest Posts Tab
                        mCategoryList.add(null); // Make space for Featured Posts Tab
                        mCategoryList.addAll(response.body());
                        mPagerAdapter.notifyDataSetChanged();
                    }

                    hideLoader();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }


    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(mContext);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }
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
