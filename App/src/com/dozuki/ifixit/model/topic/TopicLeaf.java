package com.dozuki.ifixit.model.topic;

import android.text.Html;
import android.text.Spanned;

import com.dozuki.ifixit.model.Document;
import com.dozuki.ifixit.model.Flag;
import com.dozuki.ifixit.model.Image;
import com.dozuki.ifixit.model.Item;
import com.dozuki.ifixit.model.Wiki;
import com.dozuki.ifixit.model.guide.GuideInfo;
import com.dozuki.ifixit.util.Utils;
import com.dozuki.ifixit.util.WikiHtmlTagHandler;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class TopicLeaf implements Serializable {
   private static final long serialVersionUID = 1L;

   private String mName;
   private String mTitle;
   private Image mImage;
   private String mDescription = "";
   private ArrayList<Flag> mFlags = new ArrayList<>();
   private ArrayList<GuideInfo> mGuides = new ArrayList<>();
   private ArrayList<GuideInfo> mFeaturedGuides = new ArrayList<>();
   private ArrayList<Document> mDocuments = new ArrayList<>();
   private String mSolutionsUrl;
   private ArrayList<Item> mParts = new ArrayList<>();
   private ArrayList<Item> mTools = new ArrayList<>();
   private String mContentsRendered;
   private String mLocale;
   private String mContentsRaw;
   private ArrayList<Wiki> mWikis = new ArrayList<>();

   public TopicLeaf(String name) {
      mName = name;
   }


   public void addDocument(Document document) {
      mDocuments.add(document);
   }

   public ArrayList<Document> getDocuments() {
      Collections.sort(mDocuments, new Comparator<Document>() {
         @Override
         public int compare(Document d1, Document d2) {
            return d1.getTitle().compareToIgnoreCase(d2.getTitle());
         }
      });

      return mDocuments;
   }

   public void addFeaturedGuide(GuideInfo guideInfo) {
      mFeaturedGuides.add(guideInfo);
   }

   public ArrayList<GuideInfo> getFeaturedGuides() {
      return mFeaturedGuides;
   }

   public void addGuide(GuideInfo guideInfo) {
      mGuides.add(guideInfo);
   }

   public void addWiki(Wiki wiki) {
      mWikis.add(wiki);
   }

   public String getName() {
      return mName;
   }

   public ArrayList<GuideInfo> getGuides() {
      return mGuides;
   }

   /**
    * All the guides that should be displayed in the topic list.
    * Filters out Archived guides, and sorts with the display language first.
    *
    * @return
    */
   public ArrayList<GuideInfo> getDisplayGuides(final String displayLocale) {
      ArrayList<GuideInfo> result = new ArrayList<>(mGuides);

      for (Iterator<GuideInfo> it = result.iterator(); it.hasNext(); ) {
         GuideInfo current = it.next();
         if (Arrays.asList(current.mFlags).contains("GUIDE_ARCHIVED") ||
          mFeaturedGuides.contains(current)) {
            it.remove();
         }
      }

      Collections.sort(result, new Comparator<GuideInfo>() {
         @Override
         public int compare(GuideInfo g1, GuideInfo g2) {
            if (g2.mLocale.equalsIgnoreCase(displayLocale)) {
               return 1;
            }

            return g1.mLocale.equalsIgnoreCase(g2.mLocale)
             ? 0 : -1;
         }
      });

      return result;
   }

   public ArrayList<Wiki> getRelatedWikis() {
      return mWikis;
   }

   public void setImage(Image image) {
      mImage = image;
   }

   public void setSolutionsUrl(String url) {
      mSolutionsUrl = url;
   }

   public String getSolutionsUrl() {
      return mSolutionsUrl;
   }

   public void setDescription(String description) {
      mDescription = description;
   }

   public String getDescription() {
      return mDescription;
   }

   public void addPart(Item part) {
      mParts.add(part);
   }

   public void addParts(JSONArray parts) {
      int numParts = parts.length();

      for (int i = 0; i < numParts; i++) {

      }
   }

   public void addTool(Item tool) {
      mTools.add(tool);
   }

   public void addFlag(Flag flag) {
      mFlags.add(flag);
   }

   public String toString() {
      return mName + ", " + mGuides;
   }

   public boolean equals(Object other) {
      return other instanceof TopicLeaf &&
       ((TopicLeaf) other).getName().equals(mName);
   }

   public void setLocale(String locale) {
      mLocale = locale;
   }

   public void setContentsRaw(String contentsRaw) {
      mContentsRaw = contentsRaw;
   }

   public void setContentsRendered(String contentsRendered) {
      mContentsRendered = contentsRendered;
   }

   public String getTitle() {
      return mTitle;
   }

   public void setTitle(String title) {
      mTitle = title;
   }

   public String getContentRendered() {
      return mContentsRendered;
   }

   public Spanned getContentSpanned(Html.ImageGetter imgGetter) {
      String html = Utils.cleanWikiHtml(mContentsRendered);

      Spanned htmlParsed;

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
         htmlParsed = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imgGetter, new WikiHtmlTagHandler());
      } else {
         htmlParsed = Html.fromHtml(mContentsRendered, imgGetter, new WikiHtmlTagHandler());
      }

      htmlParsed = Utils.correctLinkPaths(htmlParsed);

      return Utils.correctLinkPaths(htmlParsed);
   }

   public Image getImage() {
      return mImage;
   }
}
