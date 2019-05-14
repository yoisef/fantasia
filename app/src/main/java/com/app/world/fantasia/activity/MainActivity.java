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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.ads.*;


import com.app.world.fantasia.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
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
import com.google.android.gms.ads.MobileAds;


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
    private final String TAG = MainActivity.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudienceNetworkAds.initialize(this);
        AudienceNetworkAds.isInAdsProcess(this);
        MobileAds.initialize(this, getResources().getString(R.string.app_ad_id));





        //  AdSettings.addTestDevice("87e33dd3-571b-4c16-8dc7-c4275ed7589d");





        RateItDialogFragment.show(this, getSupportFragmentManager());

        initVar();
        initView();
        loadData();
      //  initListener();


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


    private void loadNativeAdfb() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, "1572801019522768_1572909549511915");

        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed

                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);

                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}
