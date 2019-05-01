package com.app.world.fantasia.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.world.fantasia.R;
import com.app.world.fantasia.utility.ActivityUtilities;
import com.app.world.fantasia.utility.AppUtilities;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private ImageView mImageView;
    private Animation mAnimation_1;
    private ProgressBar mProgressBar;
    private RelativeLayout mRootLayout;

    // Constants
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVar();
        initView();

        changelanguage("ar");

    }

    public void changelanguage(String localcode)
    {



        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration conf=res.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17)
        {
            conf.setLocale(new Locale(localcode.toLowerCase()));
        }
        else{
            conf.locale=new Locale(localcode.toLowerCase());
        }
        res.updateConfiguration(conf,dm);
    }


    private void initVar() {
        mContext = getApplicationContext();
        mActivity = SplashActivity.this;
    }

    private void initView() {
        setContentView(R.layout.activity_splash);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRootLayout = (RelativeLayout) findViewById(R.id.splashBody);

        mImageView = (ImageView) findViewById(R.id.splashIcon);
        mAnimation_1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
    }

    private void initFunctionality() {
        if (AppUtilities.isNetworkAvailable(mContext)) {
            mRootLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mImageView.startAnimation(mAnimation_1);
                    mAnimation_1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }, SPLASH_DURATION);
        } else {
            AppUtilities.noInternetWarning(mRootLayout, mContext);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
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

