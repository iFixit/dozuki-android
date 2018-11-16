package com.dozuki.ifixit.ui.guide.create;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.GuideInfo;
import com.dozuki.ifixit.util.ImageSizes;
import com.dozuki.ifixit.util.transformations.RoundedTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.recyclerview.widget.RecyclerView;

public class GuideListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
   private Context mContext;

   private TextView mTitleView;
   private ImageView mThumbnail;
   private TextView mPublishText;
   private GuideInfo mGuideInfo;
   private GuideListItemListener mListener;

   public GuideListItemHolder(View itemView, GuideListItemListener listener) {
      super(itemView);

      mListener = listener;
      mContext = itemView.getContext();
      mTitleView = (TextView)  itemView.findViewById(R.id.guide_create_item_title);
      mThumbnail = (ImageView)  itemView.findViewById(R.id.guide_create_item_thumbnail);
      mPublishText = (TextView)  itemView.findViewById(R.id.guide_create_item_publish_status);

      itemView.setOnClickListener(this);
      ((RelativeLayout)itemView.findViewById(R.id.guide_item_target)).setOnClickListener(this);
      itemView.setOnLongClickListener(this);
      ((RelativeLayout)itemView.findViewById(R.id.guide_item_target)).setOnLongClickListener(this);
   }

   private void publishGuide() {
      mPublishText.setText(mGuideInfo.mPublic ? R.string.unpublishing : R.string.publishing);
      mPublishText.setTextColor(mContext.getResources().getColor(R.color.text_light));
      mListener.onPublishItemClicked(mGuideInfo);
   }

   public void setItem(GuideInfo guideInfo) {
      mGuideInfo = guideInfo;

      mTitleView.setText(mGuideInfo.mTitle);

      if (mThumbnail != null) {
         com.squareup.picasso.Picasso picasso = Picasso.with(mContext);

         Transformation transform = new RoundedTransformation(4, 0);

         if (mGuideInfo.hasImage()) {
            picasso
             .load(mGuideInfo.getImagePath(ImageSizes.guideList))
             .noFade()
             .fit()
             .transform(transform)
             .error(R.drawable.no_image)
             .into(mThumbnail);
         } else {
            picasso
             .load(R.drawable.no_image)
             .noFade()
             .fit()
             .transform(transform)
             .into(mThumbnail);
         }
      }

      setPublished(mGuideInfo.mPublic);
   }

   public void setPublished(boolean published) {
      if (published) {
         buildPublishView(R.drawable.ic_list_item_unpublish, Color.rgb(0, 191, 0),
          R.string.published, mGuideInfo.mIsPublishing ? R.string.unpublishing : R.string.unpublish);
      } else {
         buildPublishView(R.drawable.ic_list_item_publish, Color.RED,
          R.string.unpublished, mGuideInfo.mIsPublishing ? R.string.publishing : R.string.publish);
      }
   }
   private void buildPublishView(int drawable, int color, int textString, int buttonString) {
      Drawable img = mContext.getResources().getDrawable(drawable);
      img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());

      mPublishText.setText(textString);
      mPublishText.setTextColor(color);
   }

   @Override
   public void onClick(View v) {
      mListener.onEditItemClicked(mGuideInfo);
   }

   @Override
   public boolean onLongClick(View v) {
      mListener.onItemLongClick(mGuideInfo);
      return true;
   }
}
