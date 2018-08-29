package com.dozuki.ifixit.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {
   private static final long serialVersionUID = 772113480839309007L;

   /**
    * Local images (captured by the camera) will not have an imageid until it's been uploaded and processed by the
    * server.  Until then, we set the imageid to -1 so we can tell which image needs to be updated.
    */
   private static final int LOCAL_IMAGE_ID = -1;
   private static final String TAG = "Image";

   @SerializedName("id") public int mId;
   @SerializedName("original") public String mPath = "";
   @SerializedName("standard") public String mStandard = "";
   @SerializedName("large") public String mLarge = "";
   @SerializedName("mini") public String mMini = "";
   @SerializedName("thumbnail") public String mThumbnail = "";
   @SerializedName("huge") public String mHuge = "";
   @SerializedName("medium") public String mMedium = "";

   private String mLocalPath;
   private Bitmap mBitmap;

   public Image() {
      this(LOCAL_IMAGE_ID);
   }

   public Image(int id) {
      this(id, "");
   }

   public Image(int id, String path) {
      mId = id;
      mPath = path;
      mLocalPath = "";
   }

   public Image(Image image) {
      mId = image.mId;
      mPath = image.mPath;
      mStandard = image.mStandard;
      mLarge = image.mLarge;
      mMini = image.mMini;
      mThumbnail = image.mThumbnail;
      mHuge = image.mHuge;
      mMedium = image.mMedium;
      mLocalPath = image.mLocalPath;
      mBitmap = image.mBitmap;
   }

   public void setLocalImage(String path) {
      mId = LOCAL_IMAGE_ID;
      mPath = path;
      mLocalPath = mPath;
   }

   public void setId(int id) {
      mId = id;
   }

   public int getId() {
      return mId;
   }

   public boolean hasLocalPath() {
      return mLocalPath != null && mLocalPath.length() > 0;
   }

   public String getLocalPath() {
      return mLocalPath;
   }

   public void setLocalPath(String path) {
      mLocalPath = path;
   }

   public void setPath(String path) {
      mPath = path;
   }

   public String getPath() {
      return mPath;
   }

   public String getPath(String size) {
      String result;

      switch (size.replace(".", "")) {
         case "mini":
            result = mMini;
            break;
         case "thumbnail":
            result = mThumbnail;
            break;
         case "standard":
            result = mStandard;
            break;
         case "medium":
            result = mMedium;
            break;
         case "large":
            result = mLarge;
            break;
         case "huge":
            result = mHuge;
            break;
         default:
            result = mPath;
      }

      return result;
   }

   /**
    * Return true if this is an actual image. Really this image shouldn't exist if
    * it isn't valid but this is currently how the JSON parsing and guide creation code
    * is currently setup.
    */
   public boolean isValid() {
      return mId != LOCAL_IMAGE_ID;
   }

   public boolean isLocal() {
      return mId == LOCAL_IMAGE_ID;
   }

   @Override
   public String toString() {
      return "{\n" +
       "path: " + mPath + ",\n" +
       "large: " + mLarge + ",\n" +
       "localPath: " + mLocalPath + ",\n" +
       "id: " + mId + "\n" +
       "}";
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Image)) {
         return false;
      }

      return ((Image)obj).getId() == mId && ((Image)obj).getPath().equals(mPath);
   }

   public void setBitmap(Bitmap bitmap) {
      mBitmap = bitmap;
   }

   public Bitmap getBitmap() {
      return mBitmap;
   }
}
