package com.app.world.fantasia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.world.fantasia.R;
import com.app.world.fantasia.activity.MainActivity;
import com.app.world.fantasia.activity.PostDetailsActivity;
import com.app.world.fantasia.adapters.PostAdapter;
import com.app.world.fantasia.api.ApiUtilities;
import com.app.world.fantasia.data.constant.AppConstant;
import com.app.world.fantasia.data.sqlite.BookmarkDbController;
import com.app.world.fantasia.listeners.ListItemClickListener;
import com.app.world.fantasia.models.bookmark.BookmarkModel;
import com.app.world.fantasia.models.post.Post;
import com.app.world.fantasia.utility.ActivityUtilities;
import com.facebook.ads.*;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.ads.AudienceNetworkAds.TAG;

public class PostListFragment extends Fragment {

    private LinearLayout mLoadingView, mNoDataView;

    private ArrayList<Post> postList;
    private PostAdapter mAdapter = null;

    private RelativeLayout mBottomLayout;
    private LinearLayoutManager mLayoutManager;
    private boolean mUserScrolled = true;
    private RecyclerView mRvPosts;
    private int mCategoryId, mPageNo = 1, mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private String mSearchedText;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;

    private InterstitialAd interstitialAd;
    private final String TAG = MainActivity.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout,nativeAdLayoutt;

    private LinearLayout adView,adVieww;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);


        initVar();
        initView(rootView);
        initFunctionality(rootView);
        initListener();

        implementScrollListener();
        AudienceNetworkAds.isInAdsProcess(rootView.getContext());
        interstitialAd = new InterstitialAd(rootView.getContext(), "1572801019522768_1572824739520396");
      // interstialadFB();
        loadNativeAdfb( rootView);

        return rootView;
    }


    public void initVar() {

        postList = new ArrayList<>();
        mBookmarkList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategoryId = getArguments().getInt(AppConstant.BUNDLE_KEY_CATEGORY_ID);
            mSearchedText = getArguments().getString(AppConstant.BUNDLE_KEY_SEARCH_TEXT);
        }
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
                 interstitialAd.show();
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

    private void loadNativeAdfb(final View view) {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(view .getContext(), "1572801019522768_1572909549511915");

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
                inflateAd(nativeAd,view);

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

    private void inflateAd(NativeAd nativeAd,View view) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout =view.findViewById(R.id.native_ad_containerpostfrag);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);

        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(view.getContext(), nativeAd, nativeAdLayout);
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

    @Override
    public void onDestroy() {

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    public void initView(View rootView) {

        mBottomLayout = (RelativeLayout) rootView.findViewById(R.id.rv_itemload);
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);

        initLoader(rootView);

        mRvPosts = (RecyclerView) rootView.findViewById(R.id.rvPosts);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvPosts.setLayoutManager(mLayoutManager);

        mAdapter = new PostAdapter(getActivity(), postList);


        mRvPosts.setAdapter(mAdapter);


    }

    public void initLoader(View rootView) {
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);
    }

    public void showLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.VISIBLE);
        }
    }


    public void initFunctionality(View rootView) {

        showLoader();

        loadPosts();
    }

    public void initListener() {

        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Post model = postList.get(position);
                switch (view.getId()) {
                    case R.id.btn_book:
                        if (model.isBookmark()) {
                            mBookmarkDbController.deleteEachFav(model.getID().intValue());
                            model.setBookmark(false);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.removed_from_book), Toast.LENGTH_SHORT).show();

                        } else {
                            int postId = model.getID().intValue();
                            String imgUrl = model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                            String postTitle = model.getTitle().getRendered();
                            String postExcerpt = model.getExcerpt().getRendered();
                            String postUrl = model.getPostUrl();
                            String postDate = model.getFormattedDate();

                            mBookmarkDbController.insertData(postId, imgUrl, postTitle, postExcerpt, postUrl, postDate);
                            model.setBookmark(true);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_share:
                        final String appPackageName = getActivity().getPackageName();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(model.getPostUrl())
                                + AppConstant.EMPTY_STRING
                                + getActivity().getResources().getString(R.string.share_text)
                                + " https://play.google.com/store/apps/details?id=" + appPackageName);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        break;
                    case R.id.lyt_container:
                       interstialadFB();

                        ActivityUtilities.getInstance().invokePostDetailsActivity(getActivity(), PostDetailsActivity.class, model.getID().intValue(), false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void loadPosts() {
        switch (mCategoryId) {
            case AppConstant.BUNDLE_KEY_LATEST_POST_ID:
                loadLatestPosts();
                break;
            case AppConstant.BUNDLE_KEY_FEATURED_POST_ID:
                loadFeaturedPosts();
                break;
            case AppConstant.BUNDLE_KEY_SEARCH_POST_ID:
                loadSearchedPosts();
                break;
            default:
                loadCategoryWisePosts();
                break;
        }
    }

    public void loadLatestPosts() {
        ApiUtilities.getApiInterface().getLatestPosts(mPageNo).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadPosts(response);
                } else {
                    hideMoreItemLoader();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadFeaturedPosts() {
        ApiUtilities.getApiInterface().getFeaturedPosts(mPageNo).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadPosts(response);
                } else {
                    hideMoreItemLoader();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadSearchedPosts() {
        ApiUtilities.getApiInterface().getSearchedPosts(mPageNo, mSearchedText).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadPosts(response);
                } else {
                    hideMoreItemLoader();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadCategoryWisePosts() {
        ApiUtilities.getApiInterface().getPostsByCategory(mPageNo, mCategoryId).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadPosts(response);
                } else {
                    hideMoreItemLoader();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadPosts(Response<List<Post>> response) {
        postList.addAll(response.body());

        updateUI();

        hideMoreItemLoader();
    }


    private void hideMoreItemLoader() {
        // After adding new data hide the view.
        mBottomLayout.setVisibility(View.GONE);
        mUserScrolled = true;
    }

    // Implement scroll listener
    private void implementScrollListener() {
        mRvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                // If scroll state is touch scroll then set mUserScrolled
                // true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mUserScrolled = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,
                                   int dy) {

                super.onScrolled(recyclerView, dx, dy);
                // Here get the child count, item count and visibleitems
                // from layout manager

                mVisibleItemCount = mLayoutManager.getChildCount();
                mTotalItemCount = mLayoutManager.getItemCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                // Now check if mUserScrolled is true and also check if
                // the item is end then update recycler view and set
                // mUserScrolled to false
                if (mUserScrolled && (mVisibleItemCount + mPastVisibleItems) == mTotalItemCount) {
                    mUserScrolled = false;

                    updateRecyclerView();
                }

            }

        });

    }

    // Method for repopulating recycler view
    private void updateRecyclerView() {

        // Show Progress Layout
        mBottomLayout.setVisibility(View.VISIBLE);

        // Handler to show refresh for a period of time you can use async task
        // while communicating serve

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                mPageNo++;

                loadPosts();

                // Toast for task completion
                //Toast.makeText(getActivity(), "Items Updated.", Toast.LENGTH_SHORT).show();

            }
        }, 5000);

    }

    private void updateUI() {

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(getActivity());
        }

        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());

        for (int i = 0; i < postList.size(); i++) {
            boolean isBookmarkSet = false;
            for (int j = 0; j < mBookmarkList.size(); j++) {
                if (postList.get(i).getID() == mBookmarkList.get(j).getPostId()) {
                    postList.get(i).setBookmark(true);
                    isBookmarkSet = true;
                    break;
                }
            }
            if (!isBookmarkSet) {
                postList.get(i).setBookmark(false);
            }
        }

        if (postList.size() == 0) {
            showEmptyView();
        } else {
            mAdapter.notifyDataSetChanged();
            hideLoader();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (postList.size() != 0) {
            updateUI();
        }
    }

}
