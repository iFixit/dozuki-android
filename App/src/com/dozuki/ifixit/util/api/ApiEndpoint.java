package com.dozuki.ifixit.util.api;

import android.net.Uri;
import android.util.Log;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.model.Comment;
import com.dozuki.ifixit.model.dozuki.Site;
import com.dozuki.ifixit.util.JSONHelper;
import com.dozuki.ifixit.util.LocaleManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines all APIEndpoints.
 */
public enum ApiEndpoint {
   SEARCH(
    new Endpoint() {
       public String createUrl(String query) {
          return "search/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Search().setResult(JSONHelper.parseSearchResults(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Search();
       }
    },
    false,
    "GET"
   ),

   CATEGORIES(
    new Endpoint() {
       public String createUrl(String query) {
          return "wikis/CATEGORY?display=hierarchyOrder";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Categories().setResult(JSONHelper.parseTopics(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Categories();
       }
    },
    false,
    "GET"
   ),

   WIKI(
    new Endpoint() {
       public String createUrl(String query) {
          return "wikis/WIKI/" + query;
       }

       public String createUrl(String query, String namespace) {
          return "wikis/" + namespace + "/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.ViewWiki().setResult(JSONHelper.parseWiki(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.ViewGuide();
       }
    },
    false,
    "GET"
   ),

   GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.ViewGuide().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.ViewGuide();
       }
    },
    false,
    "GET"
   ),

   GUIDES(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Guides().setResult(JSONHelper.parseGuides(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Guides();
       }
    },
    false,
    "GET"
   ),

   ADD_COMMENT(
    new Endpoint() {
       public String createUrl(String query) {
          return "comments" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.AddComment().setResult(new Comment(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.AddComment();
       }
    },
    true,
    "POST"
   ),

   EDIT_COMMENT(
    new Endpoint() {
       public String createUrl(String query) {
          return "comments" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.EditComment().setResult(new Comment(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.EditComment();
       }
    },
    true,
    "PATCH"
   ),

   DELETE_COMMENT(
    new Endpoint() {
       public String createUrl(String query) {
          return "comments" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.DeleteComment().setResult("");
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.DeleteComment();
       }
    },
    true,
    "DELETE"
   ),

   TOPIC(
    new Endpoint() {
       public String createUrl(String query) {
          try {
             return "wikis/CATEGORY/" + URLEncoder.encode(query, "UTF-8");
          } catch (Exception e) {
             Log.w("iFixit", "Encoding error: " + e.getMessage());
             return null;
          }
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Topic().setResult(JSONHelper.parseTopicLeaf(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Topic();
       }
    },
    false,
    "GET"
   ),

   ALL_TOPICS(
    new Endpoint() {
       public String createUrl(String query) {
          return "categories/all?limit=100000";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.TopicList().setResult(JSONHelper.parseAllTopics(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.TopicList();
       }
    },
    false,
    "GET"
   ),

   LOGIN(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/token";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Login().setResult(JSONHelper.parseLoginInfo(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Login();
       }
    },
    false,
    "POST",
    true
   ),

   LOGOUT(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/token";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Logout();
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Logout();
       }
    },
    true,
    "DELETE",
    false,
    false
   ),

   REGISTER(
    new Endpoint() {
       public String createUrl(String query) {
          return "users";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Register().setResult(JSONHelper.parseLoginInfo(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Register();
       }
    },
    false,
    "POST",
    true
   ),

   USER_IMAGES(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/media/images" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserImages().setResult(JSONHelper.parseUserImages(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserImages();
       }
    },
    true,
    "GET"
   ),

   USER_VIDEOS(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/media/videos" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserVideos().setResult(JSONHelper.parseUserVideos(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserVideos();
       }
    },
    true,
    "GET"
   ),

   USER_FAVORITES(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/favorites/guides";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserFavorites().setResult(JSONHelper.parseUserFavorites(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserFavorites();
       }
    },
    true,
    "GET"
   ),

   USER_EMBEDS(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/media/embeds" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserEmbeds().setResult(JSONHelper.parseUserEmbeds(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserVideos();
       }
    },
    true,
    "GET"
   ),

   UPLOAD_IMAGE(
    new Endpoint() {
       public String createUrl(String query) {
          String fileName;

          try {
             fileName = URLEncoder.encode(getFileNameFromFilePath(query), "UTF-8");
          } catch (UnsupportedEncodingException e) {
             // Provide a default file name.
             fileName = "uploaded_image.jpg";
          }

          return "user/media/images?file=" + fileName;
       }

       private String getFileNameFromFilePath(String filePath) {
          int index = filePath.lastIndexOf('/');

          if (index == -1) {
             /**
              * filePath doesn't have a '/' in it. That's weird but just
              * return the whole file path.
              */
             return filePath;
          }

          return filePath.substring(index + 1);
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UploadImage().setResult(JSONHelper.parseUploadedImage(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UploadImage();
       }
    },
    true,
    "POST"
   ),

   UPLOAD_STEP_IMAGE(
    new Endpoint() {
       public String createUrl(String query) {
          String fileName;

          try {
             fileName = URLEncoder.encode(getFileNameFromFilePath(query), "UTF-8");
          } catch (UnsupportedEncodingException e) {
             // Provide a default file name.
             fileName = "uploaded_image.jpg";
          }

          return "user/media/images?cropToRatio=FOUR_THREE&file=" + fileName;
       }

       private String getFileNameFromFilePath(String filePath) {
          int index = filePath.lastIndexOf('/');

          if (index == -1) {
             /**
              * filePath doesn't have a '/' in it. That's weird but just
              * return the whole file path.
              */
             return filePath;
          }

          return filePath.substring(index + 1);
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UploadStepImage().setResult(JSONHelper.parseUploadedImage(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UploadStepImage();
       }
    },
    true,
    "POST"
   ),

   COPY_IMAGE(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/media/images/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.DeleteImage().setResult("");
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.DeleteImage();
       }
    },
    true,
    "POST"
   ),

   DELETE_IMAGE(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/media/images" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.DeleteImage().setResult("");
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.DeleteImage();
       }
    },
    true,
    "DELETE"
   ),

   USER_GUIDES(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/guides?limit=10000";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserGuides().setResult(JSONHelper.parseUserGuides(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserGuides();
       }
    },
    true,
    "GET"
   ),

   GUIDE_FOR_EDIT(
    new Endpoint() {
       public final boolean mExcludeLangid = true;

       public String createUrl(String query) {
          return "guides/" + query + "?unpatrolled&excludePrerequisiteSteps";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.GuideForEdit().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.GuideForEdit();
       }
    },
    true,
    "GET",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   FAVORITE_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/favorites/guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.FavoriteGuide().setResult(true);
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.FavoriteGuide();
       }
    },
    true,
    "PUT",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   UNFAVORITE_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "user/favorites/guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.FavoriteGuide().setResult(false);
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.FavoriteGuide();
       }
    },
    true,
    "DELETE",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   CREATE_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.CreateGuide().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.CreateGuide();
       }
    },
    true,
    "POST"
   ),

   EDIT_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.EditGuide().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.EditGuide();
       }
    },
    true,
    "PATCH"
   ),

   DELETE_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.DeleteGuide().setResult(json);
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.DeleteGuide();
       }
    },
    true,
    "DELETE"
   ),

   COMPLETE_GUIDE(
    new Endpoint() {
       public String createUrl(String guideid) {
          return "guides/" + guideid + "/completed";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.CompleteGuide().setResult(true);
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.CompleteGuide();
       }
    },
    true,
    "PUT",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   /***
    * Not currently used.
    *
    UNCOMPLETE_GUIDE(
    new Endpoint() {
    public String createUrl(String guideid) {
    return "guides/" + guideid + "/completed";
    }

    public ApiEvent<?> parse(String json) throws JSONException {
    return new ApiEvent.CompleteGuide().setResult(false);
    }

    public ApiEvent<?> getEvent() {
    return new ApiEvent.UncompleteGuide();
    }
    },
    true,
    "DELETE"
    ),
    *
    ***/

   PUBLISH_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.PublishStatus().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.PublishStatus();
       }
    },
    true,
    "PUT",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   UNPUBLISH_GUIDE(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.PublishStatus().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.PublishStatus();
       }
    },
    true,
    "DELETE",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   REORDER_GUIDE_STEPS(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.StepReorder().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.StepReorder();
       }
    },
    true,
    "PUT",
    false, // forcePublic
    true,  // postResults
    false  // appendLangid
   ),

   ADD_GUIDE_STEP(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.StepAdd().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.StepAdd();
       }
    },
    true,
    "POST"
   ),

   UPDATE_GUIDE_STEP(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.StepSave().setResult(JSONHelper.parseStep(new JSONObject(json), 0));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.StepSave();
       }
    },
    true,
    "PATCH"
   ),

   DELETE_GUIDE_STEP(
    new Endpoint() {
       public String createUrl(String query) {
          return "guides/" + query;
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.StepRemove().setResult(JSONHelper.parseGuide(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.StepRemove();
       }
    },
    true,
    "DELETE"
   ),

   SITES(
    new Endpoint() {
       public String createUrl(String query) {
          return "sites?limit=1000";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.Sites().setResult(JSONHelper.parseSites(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.Sites();
       }
    },
    false,
    "GET"
   ),

   SITE_INFO(
    new Endpoint() {
       public String createUrl(String query) {
          return "sites/info";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.SiteInfo().setResult(JSONHelper.parseSiteInfo(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.SiteInfo();
       }
    },
    false,
    "GET"
   ),

   USER_INFO(
    new Endpoint() {
       public String createUrl(String query) {
          return "user";
       }

       public ApiEvent<?> parse(String json) throws JSONException {
          return new ApiEvent.UserInfo().setResult(JSONHelper.parseLoginInfo(json));
       }

       public ApiEvent<?> getEvent() {
          return new ApiEvent.UserInfo();
       }
    },
    false,
    "GET",
    true
   );

   /**
    * Current version of the API to use.
    */
   private static final String API_VERSION = "2.0";

   /**
    * Defines various methods that each endpoint must provide.
    */
   private interface Endpoint {

      /**
       * Returns the end of a URL that defines this endpoint.
       * <p>
       * The full URL is then: protocol + domain + "/api/" + api version + "/" + createUrl
       */
      public String createUrl(String query);

      /**
       * Returns an ApiEvent given the JSON response of the request.
       */
      public ApiEvent<?> parse(String json) throws JSONException;

      /**
       * Returns an empty ApiEvent that is used for events for this endpoint.
       * <p>
       * This is typically used to put errors into so they still get to the right place.
       */
      public ApiEvent<?> getEvent();
   }

   public static Set<String> VALID_LANGIDS = new HashSet<String>(Arrays.asList(
    new String[]{"en", "jp", "de", "fr", "es", "pt", "it", "nl", "tr", "zh", "ru"}
   ));

   /**
    * Endpoint's functionality.
    */
   private final Endpoint mEndpoint;

   /**
    * Whether or not this is an authenticated endpoint. If true, sends the
    * user's auth token along in a header.
    */
   public final boolean mAuthenticated;

   /**
    * Request method for this endpoint e.g. GET, POST, DELETE
    */
   public final String mMethod;

   /**
    * True if endpoint must be public. This is primarily for login and register so
    * users can actually log in without being prompted for login repeatedly.
    */
   public final boolean mForcePublic;

   /**
    * True if the default langid should be appended to the URL
    */
   public boolean mAppendLangid = true;

   /**
    * True to post results of API calls to the Bus.
    */
   public final boolean mPostResults;

   private ApiEndpoint(Endpoint endpoint, boolean authenticated, String method) {
      this(endpoint, authenticated, method, false);
   }

   private ApiEndpoint(Endpoint endpoint, boolean authenticated,
                       String method, boolean forcePublic) {
      this(endpoint, authenticated, method, forcePublic, true);
   }

   private ApiEndpoint(Endpoint endpoint, boolean authenticated,
                       String method, boolean forcePublic, boolean postResults) {
      this(endpoint, authenticated, method, forcePublic, postResults, true);
   }

   private ApiEndpoint(Endpoint endpoint, boolean authenticated,
                       String method, boolean forcePublic, boolean postResults,
                       boolean appendLangid) {
      mEndpoint = endpoint;
      mAuthenticated = authenticated;
      mMethod = method;
      mForcePublic = forcePublic;
      mPostResults = postResults;
      mAppendLangid = appendLangid;
   }


   /**
    * Returns a unique integer for this endpoint.
    * <p>
    * This value is passed around in Intents to identify what request is being
    * made. It will also be used in the database to store results.
    */
   protected int getTarget() {
      /**
       * Just use the enum's unique integer that auto increments from 0.
       */
      return ordinal();
   }

   /**
    * Returns an absolute URL for this endpoint for the given site and query.
    */
   public String getUrl(Site site, String query) {
      String domain;
      String protocol;
      String url;
      String langid = LocaleManager.getLanguage(App.get().getApplicationContext());

      if (site != null) {
         domain = site.getAPIDomain();
      } else {
         domain = "ifixit.com";
      }

      protocol = "https";
      url = String.format("%s://%s/api/%s/%s", protocol, domain, API_VERSION,
       mEndpoint.createUrl(query));

      if (VALID_LANGIDS.contains(langid) && mAppendLangid) {
         Uri baseUri = Uri.parse(url);
         boolean hasLangid = baseUri.getQueryParameterNames().contains("langid");

         if (hasLangid) {
            url = baseUri.toString();
         } else {
            url = baseUri.buildUpon().appendQueryParameter("langid", langid).build().toString();
         }
      }

      return url;
   }

   public ApiEvent<?> parseResult(String json) throws JSONException {
      return mEndpoint.parse(json).setResponse(json);
   }

   /**
    * Returns a "plain" event that is the correct type for this endpoint.
    */
   public ApiEvent<?> getEvent() {
      return mEndpoint.getEvent();
   }

   public String toString() {
      return mAuthenticated + " | " + ordinal();
   }
}
