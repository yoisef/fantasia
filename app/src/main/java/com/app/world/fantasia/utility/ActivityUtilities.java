package com.app.world.fantasia.utility;

import android.app.Activity;
import android.content.Intent;

import com.app.world.fantasia.data.constant.AppConstant;

public class ActivityUtilities {

    private static ActivityUtilities sActivityUtilities = null;

    public static ActivityUtilities getInstance() {
        if (sActivityUtilities == null) {
            sActivityUtilities = new ActivityUtilities();
        }
        return sActivityUtilities;
    }

    public void invokeNewActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeCustomUrlActivity(Activity activity, Class<?> tClass, String pageTitle, String pageUrl, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_TITLE, pageTitle);
        intent.putExtra(AppConstant.BUNDLE_KEY_URL, pageUrl);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokePostDetailsActivity(Activity activity, Class<?> tClass, int clickedPostId, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_POST_ID, clickedPostId);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeCommentListActivity(Activity activity, Class<?> tClass, int clickedPostId, String commentsLink, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_POST_ID, clickedPostId);
        intent.putExtra(AppConstant.BUNDLE_KEY_COMMENTS_LINK, commentsLink);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

}
