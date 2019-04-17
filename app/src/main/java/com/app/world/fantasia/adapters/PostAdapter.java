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
import com.app.world.fantasia.models.post.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> allPosts;
    private Context mContext;
    private ListItemClickListener itemClickListener;

    public PostAdapter(Context mContext, ArrayList<Post> allPosts) {
        this.mContext = mContext;
        this.allPosts = allPosts;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_list, parent, false);
        return new ViewHolder(view, viewType, itemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgPost, imgFeatured;
        private TextView tvPostTitle, tvPostExcerpt, tvPostDate;
        private ImageButton btnBookmark, btnSharePost;
        private RelativeLayout lytContainer;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            imgFeatured = (ImageView) itemView.findViewById(R.id.featured_img);
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
        return (null != allPosts ? allPosts.size() : 0);

    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder mainHolder, int position) {
        final Post model = allPosts.get(position);

        // setting data over views
        mainHolder.tvPostTitle.setText(Html.fromHtml(model.getTitle().getRendered()));
        mainHolder.tvPostExcerpt.setText(Html.fromHtml(model.getExcerpt().getRendered()));

        if (model.isIsSticky()) {
            mainHolder.imgFeatured.setVisibility(View.VISIBLE);
        } else {
            mainHolder.imgFeatured.setVisibility(View.GONE);
        }

        String imgUrl = null;
        if (model.getEmbedded().getWpFeaturedMedias().size() > 0) {
            if (model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails() != null) {
                if (model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl() != null) {
                    imgUrl = model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                }
            }
        }

        if (imgUrl != null) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgPost);
        } else {
            Glide.with(mContext)
                    .load(R.color.imgPlaceholder)
                    .into(mainHolder.imgPost);
        }

        if (model.isBookmark()) {
            mainHolder.btnBookmark.setImageResource(R.drawable.ic_book);
        } else {
            mainHolder.btnBookmark.setImageResource(R.drawable.ic_un_book);
        }

        mainHolder.tvPostDate.setText(model.getFormattedDate());


    }
}
