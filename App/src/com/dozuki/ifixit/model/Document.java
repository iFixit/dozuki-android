package com.dozuki.ifixit.model;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.model.dozuki.Site;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Document implements Serializable {
   private static final long serialVersionUID = 1L;

   public String text;
   @SerializedName("url")
   public String relativeUrl;
   @SerializedName("download_url")
   public String url;
   @SerializedName("documentid")
   public int id;
   public int wikiid;
   public long date;
   public long size;
   public int pages;
   public String filename;
   public String title;
   public String guid;

   public String getTitle() {
      return title != null ? title : text != null ? text : filename;
   }

   public String getFullUrl() {
      Site site = App.get().getSite();

      return "https://" + site.mDomain + this.relativeUrl;
   }
}
