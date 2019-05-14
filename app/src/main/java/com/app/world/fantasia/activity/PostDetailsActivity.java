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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();

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

        super.onBackPressed();
    }
}