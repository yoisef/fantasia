package com.app.world.fantasia.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdView;
import com.app.world.fantasia.R;
import com.app.world.fantasia.api.ApiUtilities;
import com.app.world.fantasia.api.HttpParams;
import com.app.world.fantasia.data.constant.AppConstant;
import com.app.world.fantasia.data.sqlite.BookmarkDbController;
import com.app.world.fantasia.listeners.WebListener;
import com.app.world.fantasia.models.bookmark.BookmarkModel;
import com.app.world.fantasia.models.comment.Comments;
import com.app.world.fantasia.models.post.PostDetails;
import com.app.world.fantasia.utility.ActivityUtilities;
import com.app.world.fantasia.utility.AdsUtilities;
import com.app.world.fantasia.utility.AppUtilities;
import com.app.world.fantasia.utility.TtsEngine;
import com.app.world.fantasia.webengine.PostWebEngine;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.ads.AudienceNetworkAds.TAG;

public class PostDetailsActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;

    private ImageView mPostImage;
    private FloatingActionButton mFab;
    private TextView mTvTitle, mTvDate, mTvComment;
    private int mPostId;
    private PostDetails mModel = null;
    private RelativeLayout mLytContainer;
    private ArrayList<Comments> mCommentList;
    private String mCommentsLink;
    private int mItemCount = 5;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;
    private boolean mIsBookmark;

    private TtsEngine mTtsEngine;
    private boolean mIsTtsPlaying = false;
    private String mTtsText;
    private MenuItem menuItemTTS;

    private WebView mWebView;
    private PostWebEngine mPostWebEngine;

    private Bitmap bitmap;
    private String imgUrl = null;
    private InterstitialAd interstitialAd;
    private final String TAG = MainActivity.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout,nativeAdLayoutt;

    private LinearLayout adView,adVieww;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();

        interstitialAd = new InterstitialAd(this, "1572801019522768_1572824739520396");

        loadNativeAdfb();
        interstialadFB();
    }

    public void interstialadFB()
    {
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
               // interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
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
        nativeAdLayout = findViewById(R.id.native_ad_containerpost);
        LayoutInflater inflater = LayoutInflater.from(PostDetailsActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);

        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(PostDetailsActivity.this, nativeAd, nativeAdLayout);
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





    private void initVar() {
        mActivity = PostDetailsActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mPostId = intent.getIntExtra(AppConstant.BUNDLE_KEY_POST_ID, 0);
        }
        mBookmarkList = new ArrayList<>();
        mCommentList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_post_details);

        mPostImage = (ImageView) findViewById(R.id.post_img);
        mFab = (FloatingActionButton) findViewById(R.id.share_post);
        mTvTitle = (TextView) findViewById(R.id.title_text);
        mTvDate = (TextView) findViewById(R.id.date_text);
        mTvComment = (TextView) findViewById(R.id.comment_text);
        mLytContainer = (RelativeLayout) findViewById(R.id.lyt_container);

        initWebEngine();

        initLoader();
        initToolbar(false);
        enableUpButton();
    }

    public void initWebEngine() {

        mWebView = (WebView) findViewById(R.id.web_view);

        mPostWebEngine = new PostWebEngine(mWebView, mActivity);
        mPostWebEngine.initWebView();


        mPostWebEngine.initListeners(new WebListener() {
            @Override
            public void onStart() {
                showLoader();
            }

            @Override
            public void onLoaded() {
                hideLoader();
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onNetworkError() {
                showEmptyView();
            }

            @Override
            public void onPageTitle(String title) {
            }
        });
    }

    private void initFunctionality() {

        showLoader();

        mTtsEngine = new TtsEngine(mActivity);

        loadPostDetails();
        updateUI();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    if (mIsBookmark) {
                        mBookmarkDbController.deleteEachFav(mModel.getID().intValue());
                        Toast.makeText(mActivity, getString(R.string.removed_from_book), Toast.LENGTH_SHORT).show();
                    } else {
                        int postId = mModel.getID().intValue();
                        String imgUrl = mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                        String postTitle = mModel.getTitle().getRendered();
                        String postExcerpt = mModel.getExcerpt().getRendered();
                        String postUrl = mModel.getPostUrl();
                        String postDate = mModel.getFormattedDate();

                        mBookmarkDbController.insertData(postId, imgUrl, postTitle, postExcerpt, postUrl, postDate);
                        Toast.makeText(mActivity, getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show();
                    }
                    mIsBookmark = !mIsBookmark;
                    setFabImage();
                }
            }
        });

        mLytContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeCommentListActivity(mActivity, CommentListActivity.class, mPostId, mCommentsLink, false);
                }
            }
        });
    }

    private void loadPostDetails() {
        ApiUtilities.getApiInterface().getPostDetails(mPostId).enqueue(new Callback<PostDetails>() {
            @Override
            public void onResponse(Call<PostDetails> call, Response<PostDetails> response) {
                if (response.isSuccessful()) {

                    mModel = response.body();

                    mCommentsLink = mModel.getLinks().getComments().get(0).getHref();
                    loadComments();

                    mTvTitle.setText(Html.fromHtml(mModel.getTitle().getRendered()));

                    if (mModel.getEmbedded().getWpFeaturedMedias().size() > 0) {
                        if (mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails() != null) {
                            if (mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl() != null) {
                                imgUrl = mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                            }
                        }
                    }
                    if (imgUrl != null) {
                        Glide.with(getApplicationContext())
                                .load(imgUrl)
                                .into(mPostImage);
                        getBitmap();
                    }

                    mTvDate.setText(mModel.getFormattedDate());

                    String postContent = mModel.getContent().getRendered();
                    mTtsText = new StringBuilder(Html.fromHtml(mModel.getTitle().getRendered())).append(AppConstant.DOT).append(Html.fromHtml(mModel.getContent().getRendered())).toString();
                    postContent = new StringBuilder().append(AppConstant.CSS_PROPERTIES).append(postContent).toString();
                    mPostWebEngine.loadHtml(postContent);

                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<PostDetails> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    private void loadComments() {

        ApiUtilities.getApiInterface().getComments(mCommentsLink, mItemCount).enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response) {
                if (response.isSuccessful()) {

                    int totalPages = Integer.parseInt(response.headers().get(HttpParams.TOTAL_PAGE));

                    if (totalPages > 1) {
                        mItemCount = mItemCount * totalPages;
                        loadComments();
                    } else {
                        mCommentList.clear();
                        mCommentList.addAll(response.body());

                        int commentCount = 0;
                        for (int i = 0; i < mCommentList.size(); i++) {
                            if (mCommentList.get(i).getParent() == 0) {
                                commentCount++;
                            }
                        }

                        mTvComment.setText(String.valueOf(commentCount));
                        hideLoader();
                    }

                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<Comments>> call, Throwable t) {
                showEmptyView();
                t.printStackTrace();
            }
        });
    }

    public void updateUI() {

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(mContext);
        }
        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());


        for (int i = 0; i < mBookmarkList.size(); i++) {
            if (mPostId == mBookmarkList.get(i).getPostId()) {
                mIsBookmark = true;
                break;
            }
        }
        setFabImage();
    }

    private void setFabImage() {
        if (mIsBookmark) {
            mFab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_book));
        } else {
            mFab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_un_book));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menus_read_article:
                if (mModel != null) {
                    toggleTtsPlay();
                }
                return true;
            case R.id.menus_share_post:
                if (mModel != null) {
                    final String appPackageName = mActivity.getPackageName();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mModel.getPostUrl())
                            + AppConstant.EMPTY_STRING
                            + mActivity.getResources().getString(R.string.share_text)
                            + " https://play.google.com/store/apps/details?id=" + appPackageName);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                }
                break;

            case R.id.menus_set_image:
                if (mModel != null) {
                    try {
                        WallpaperManager wm = WallpaperManager.getInstance(mContext);
                        wm.setBitmap(bitmap);
                        Toast.makeText(mActivity, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mActivity, getString(R.string.wallpaper_set_failed), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTtsPlay() {
        if (mIsTtsPlaying) {
            mTtsEngine.releaseEngine();
            mIsTtsPlaying = false;
        } else {
            mTtsEngine.startEngine(mTtsText);
            mIsTtsPlaying = true;
        }
        toggleTtsView();
    }

    private void toggleTtsView() {
        if (mIsTtsPlaying) {
            menuItemTTS.setTitle(R.string.site_menu_stop_reading);
        } else {
            menuItemTTS.setTitle(R.string.read_post);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTtsEngine.releaseEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        mTtsEngine.releaseEngine();
        mModel = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsTtsPlaying) {
            mIsTtsPlaying = false;
            menuItemTTS.setTitle(R.string.read_post);
        }

        if (mCommentsLink != null) {
            loadComments();
        }
        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        menuItemTTS = menu.findItem(R.id.menus_read_article);

        return true;
    }

    public void getBitmap() {
        Glide.with(mContext)
                .asBitmap()
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }
                });
    }

    @Override
    public void onBackPressed() {

       interstitialAd.show();

        super.onBackPressed();
    }
}