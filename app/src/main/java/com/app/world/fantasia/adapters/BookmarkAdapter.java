package com.app.world.fantasia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.world.fantasia.R;
import com.app.world.fantasia.listeners.ListItemClickListener;
import com.app.world.fantasia.models.bookmark.BookmarkModel;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<BookmarkModel> mBookmarkList;
    private ListItemClickListener mItemClickListener;

    public BookmarkAdapter(Context mContext, ArrayList<BookmarkModel> mBookmarkList) {
        this.mContext = mContext;
        this.mBookmarkList = mBookmarkList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark_list, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPost;
        private TextView tvPostTitle, tvPostExcerpt, tvPostDate;
        private ImageButton btnBookmark, btnSharePost;
        private RelativeLayout lytContainer;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            tvPostTitle = (TextView) itemView.findViewById(R.id.title_text);
            tvPostExcerpt = (TextView) itemView.findViewById(R.id.excerpt_text);
            tvPostDate = (TextView) itemView.findViewById(R.id.date_text);
            btnBookmark = (ImageButton) itemView.findViewById(R.id.btn_book);
            btnSharePost = (ImageButton) itemView.findViewById(R.id.btn_share);
            lytContainer = (RelativeLayout) itemView.findViewById(R.id.lyt_container);

            btnBookmark.setOnClickListener(this);
            btnSharePost.setOnClickListener(this);
            lytContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mBookmarkList ? mBookmarkList.size() : 0);

    }

    @Override
    public void onBindViewHolder(BookmarkAdapter.ViewHolder mainHolder, int position) {
        final BookmarkModel model = mBookmarkList.get(position);

        // setting data over views
        mainHolder.tvPostTitle.setText(Html.fromHtml(model.getPostTitle()));
        mainHolder.tvPostExcerpt.setText(Html.fromHtml(model.getPostExcerpt()));

        String imgUrl = model.getPostImageUrl();
        if (imgUrl != null) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgPost);
        } else {
            Glide.with(mContext)
                    .load(R.color.imgPlaceholder)
                    .into(mainHolder.imgPost);
        }

        mainHolder.tvPostDate.setText(model.getFormattedDate());

    }
}