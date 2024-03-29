package com.dozuki.ifixit.ui.guide.create;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.Image;
import com.dozuki.ifixit.ui.guide.view.GuideViewActivity;
import com.dozuki.ifixit.util.ImageSizes;
import com.dozuki.ifixit.util.PicassoUtils;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiCall;
import com.dozuki.ifixit.util.api.GuideMediaProgress;
import com.dozuki.ifixit.util.transformations.RoundedTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.cardview.widget.CardView;

public class OfflineGuideListItem extends CardView implements
 View.OnClickListener {
   private final TextView mProgressText;
   private TextView mTitleView;
   private ImageButton mProgressButton;
   private ImageView mThumbnail;
   private Activity mActivity;
   private GuideMediaProgress mGuideMedia;

   public OfflineGuideListItem(Activity activity) {
      super(activity);
      mActivity = activity;

      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
       Context.LAYOUT_INFLATER_SERVICE);
      inflater.inflate(R.layout.offline_guide_item, this, true);

      mTitleView = (TextView) findViewById(R.id.offline_guide_title);
      mProgressButton = (ImageButton) findViewById(R.id.offline_guide_remove);
      mThumbnail = (ImageView) findViewById(R.id.offline_guide_thumbnail);
      mProgressText = (TextView) findViewById(R.id.offline_guide_progress);

      mProgressButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {
            App.sendEvent("ui_action", "button_press", "offline_guides_unfavorite_click", null);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

            builder
             .setTitle(R.string.unfavorite_guide)
             .setMessage(R.string.unfavorite_confirmation)
             .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   Api.call(mActivity, ApiCall.favoriteGuide(
                    mGuideMedia.mGuideInfo.mGuideid, false));
                   dialog.dismiss();
                }
             })
             .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
                }
             })
             .setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
         }
      });

      setOnClickListener(this);
      ((RelativeLayout) findViewById(R.id.guide_item_target)).setOnClickListener(this);
   }

   public void setRowData(GuideMediaProgress guideMedia, boolean displayLiveImages,
                          boolean isSyncing) {

      mGuideMedia = guideMedia;

      mTitleView.setText(mGuideMedia.mGuideInfo.mTitle);

      if (!guideMedia.isComplete()) {
         mProgressButton.setVisibility(View.GONE);
         mProgressText.setVisibility(View.VISIBLE);
         final double percentComplete = ((double) mGuideMedia.mMediaProgress / mGuideMedia.mTotalMedia) * 100;
         mProgressText.setText(String.format("%,.2f%% complete", percentComplete));
      } else {
         mProgressButton.setVisibility(View.VISIBLE);
         mProgressText.setVisibility(View.GONE);
      }

      Picasso picasso = Picasso.with(mActivity);
      Transformation transform = new RoundedTransformation(4, 0);
      Image image = mGuideMedia.mGuideInfo.mImage;

      if (image != null) {
         PicassoUtils.displayImage(picasso, image.getPath(ImageSizes.guideList), !displayLiveImages)
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

   @Override
   public void onClick(View view) {
      mActivity.startActivity(GuideViewActivity.viewGuideid(mActivity,
       mGuideMedia.mGuideInfo.mGuideid));
   }
}
