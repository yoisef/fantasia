package com.app.world.fantasia.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Html;

import com.app.world.fantasia.R;
import com.app.world.fantasia.data.constant.AppConstant;
import com.app.world.fantasia.fragment.PostListFragment;
import com.app.world.fantasia.models.category.Category;

import java.util.ArrayList;

public class CategoryPagerAdapter extends FragmentStatePagerAdapter {

    private Activity mActivity;

    private ArrayList<Category> categoryList;

    public CategoryPagerAdapter(Activity mActivity, FragmentManager fm, ArrayList<Category> categoryList) {
        super(fm);
        this.mActivity = mActivity;
        this.categoryList = categoryList;
    }

    @Override
    public Fragment getItem(int position) {
        int categoryId;
        switch (position) {
            case AppConstant.FIRST_TAB_INDEX:
                categoryId = AppConstant.BUNDLE_KEY_LATEST_POST_ID;
                break;
            case AppConstant.SECOND_TAB_INDEX:
                categoryId = AppConstant.BUNDLE_KEY_FEATURED_POST_ID;
                break;
            default:
                categoryId = categoryList.get(position).getID().intValue();
                break;
        }

        Fragment postListFragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstant.BUNDLE_KEY_CATEGORY_ID, categoryId);
        postListFragment.setArguments(args);
        return postListFragment;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case AppConstant.FIRST_TAB_INDEX:
                title = mActivity.getString(R.string.latest_post);
                break;
            case AppConstant.SECOND_TAB_INDEX:
                title = mActivity.getString(R.string.featured_post);
                break;
            default:
                title = categoryList.get(position).getName();
                break;
        }

        return Html.fromHtml(title);
    }
}


