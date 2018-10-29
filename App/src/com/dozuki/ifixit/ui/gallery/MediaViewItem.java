package com.dozuki.ifixit.ui.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.gallery.GalleryImage;
import com.dozuki.ifixit.util.ImageSizes;
import com.dozuki.ifixit.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;

public class MediaViewItem extends RelativeLayout {
   private final Context mContext;
   private RelativeLayout mSelectImage;
   private GalleryFallbackImage mImageView;
   private RelativeLayout mLoadingBar;
   private int mTargetWidth;
   private int mTargetHeight;
   private com.squareup.picasso.Picasso mPicasso;
   private GalleryImage mImage;

   public MediaViewItem(Context context) {
      super(context);
      mContext = context;

      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      inflater.inflate(R.layout.gallery_cell, this, true);

      mImageView = (GalleryFallbackImage) findViewById(R.id.media_image);
      mSelectImage = ((RelativeLayout) findViewById(R.id.selected_image));
      mLoadingBar = (RelativeLayout) findViewById(R.id.gallery_cell_progress_bar);

      mSelectImage.setVisibility(View.INVISIBLE);
      mLoadingBar.setVisibility(View.GONE);
      Resources res = context.getResources();
      mTargetWidth = res.getDimensionPixelSize(R.dimen.gallery_grid_column_width);
      mTargetHeight = res.getDimensionPixelSize(R.dimen.gallery_grid_item_height);
   }

   public GalleryImage getImage() {
      return mImage;
   }

   public void setImage(GalleryImage image) {

      if (!image.isValid()) {
         mLoadingBar.setVisibility(VISIBLE);
      } else {
         mLoadingBar.setVisibility(GONE);
      }

      if (image.isLocal()) {
         Uri temp = Uri.parse(image.getPath());
         image.setLocalImage(temp.toString());
         mImage = image;
         mImageView.setImage(mImage);

         if (image.fromMediaStore()) {
            this.setImageItem(image.getLocalPath());
         } else {
            // image was added locally from camera
            this.setImageItem(new File(temp.toString()));
         }
      } else {
         mImage = image;
         mImageView.setImage(mImage);
         String src = image.getPath(ImageSizes.guideList);
         src = !src.isEmpty() ? src : image.getPath();
         this.setImageItem(src);
      }
   }

   public void setImageItem(String image) {
      buildImage(Picasso.with(mContext)
       .load(image));
   }

   public void setImageItem(File image) {
      buildImage(Picasso.with(mContext).load(image));
   }

   private void buildImage(RequestCreator builder) {
      builder
       .resize(mTargetWidth, mTargetHeight)
       .centerCrop()
       .error(R.drawable.no_image)
       .tag(mContext)
       .into((Target) mImageView);
   }

   public void setSelected(boolean selected) {
      mSelectImage.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
   }

   public void clearImage() {
      mLoadingBar.setVisibility(VISIBLE);
      Utils.safeStripImageView(mImageView);
      mImageView.setImageResource(R.color.image_border);
   }
}
