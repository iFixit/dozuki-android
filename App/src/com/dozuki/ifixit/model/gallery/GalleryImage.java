package com.dozuki.ifixit.model.gallery;

import com.dozuki.ifixit.model.user.UserImage;

import java.io.Serializable;

public class GalleryImage extends UserImage implements Serializable {
   private static final long serialVersionUID = 772113423839309007L;

   private boolean mSelected = false;

   public GalleryImage() {
      super();

      mSelected = false;
   }

   public GalleryImage(UserImage image) {
      super(image);

      mSelected = false;
   }

   public void setSelected(boolean selected) {
      mSelected = selected;
   }

   public boolean isSelected() {
      return mSelected;
   }

   public void toggleSelected() {
      setSelected(!mSelected);
   }

   public boolean fromMediaStore() {
      return !getPath().contains(".jpg");
   }
}
