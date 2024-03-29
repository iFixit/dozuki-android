package com.dozuki.ifixit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.dozuki.Site;
import com.dozuki.ifixit.model.dozuki.SiteChangedEvent;
import com.dozuki.ifixit.model.user.LoginEvent;
import com.dozuki.ifixit.model.user.User;
import com.dozuki.ifixit.ui.auth.LoginFragment;
import com.dozuki.ifixit.util.LocaleManager;
import com.dozuki.ifixit.util.ViewServer;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiEvent;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Base Activity that performs various functions that all Activities in this app
 * should do. Such as:
 *
 * Registering for the event bus. Setting the current site's theme. Finishing
 * the Activity if the user logs out but the Activity requires authentication.
 */
public abstract class BaseActivity extends AppCompatActivity {
   protected static final String LOADING = "LOADING_FRAGMENT";
   private static final String ACTIVITY_ID = "ACTIVITY_ID";
   private static final String USERID = "USERID";
   private static final String SITE = "SITE";
   // If an Intent has a site argument it will change sites before displaying any content.
   private static final String SITE_ARGUMENT = "SITE_ARGUMENT";
   public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 8347;


   private static final int LOGGED_OUT_USERID = -1;
   private static final String TAG = "BaseActivity";
   protected Toolbar mToolbar;
   protected FrameLayout mContentFrame;

   private int mActivityid;
   private int mUserid;
   private Site mSite;
   private LoginFragment.GoogleSignInActivityResult mPendingGoogleSigninResult;


   /**
    * This is incredibly hacky. The issue is that Otto does not search for @Subscribed
    * methods in parent classes because the performance hit is far too big for
    * Android because of the deep inheritance with the framework and views.
    * Because of this, @Subscribed methods on BaseActivity itself don't get
    * registered. The workaround is to make an anonymous object that is registered
    * on behalf of the parent class. Workaround courtesy of:
    * https://github.com/square/otto/issues/26
    *
    * Note: The '@SuppressWarnings("unused")' is to prevent
    * warnings that are incorrect (the methods *are* actually used.
    */
   private Object mBaseActivityListener = new Object() {
      @SuppressWarnings("unused")
      @Subscribe
      public void onLoginEvent(LoginEvent.Login event) {
         onLogin(event);
      }

      @SuppressWarnings("unused")
      @Subscribe
      public void onLogoutEvent(LoginEvent.Logout event) {
         onLogout(event);
      }

      @SuppressWarnings("unused")
      @Subscribe
      public void onCancelEvent(LoginEvent.Cancel event) {
         onCancelLogin(event);
      }

      @SuppressWarnings("unused")
      @Subscribe
      public void onUnauthorized(ApiEvent.Unauthorized event) {
         openLoginDialogIfLoggedOut();
      }

      @SuppressWarnings("unused")
      @Subscribe
      public void onApiCall(ApiEvent.ActivityProxy activityProxy) {
         if (activityProxy.getActivityid() == mActivityid) {
            // Send the real event off to the real handler.
            App.getBus().post(activityProxy.getApiEvent());
         } else {
            // Send the event back to Api so it can retry it for the
            // intended Activity.
            App.getBus().post(new DeadEvent(App.getBus(),
             activityProxy.getApiEvent()));
         }
      }

      @SuppressWarnings("unused")
      @Subscribe
      public void onSiteChanged(SiteChangedEvent event) {
         mSite = event.mSite;
         // Reset the userid so we don't erroneously finish the Activity.
         setUserid();
      }
   };

   @Override
   protected void attachBaseContext(Context base) {
      super.attachBaseContext(LocaleManager.setLocale(base));
      Log.d(TAG, "attachBaseContext");
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      // LoginFragment doesn't get all of the onActivityResults for google sign in
      // so the activity needs to proxy them through but only after the LoginFragment has
      // been registered with the event bus.
      if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
         mPendingGoogleSigninResult = new LoginFragment.GoogleSignInActivityResult(requestCode,
          resultCode, data);
      }
   }

   @Override
   public void onCreate(Bundle savedState) {

      // Enable vector drawables (icons) on older devices.
      // build.gradle also requires `vectorDrawables.useSupportLibrary = true` in the defaultConfig
      AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

      App app = App.get();
      Site currentSite = app.getSite();

      if (savedState != null) {
         mActivityid = savedState.getInt(ACTIVITY_ID);
         mUserid = savedState.getInt(USERID);
         mSite = (Site)savedState.getSerializable(SITE);

         // If the site associated with this Activity is different than the current site,
         // set it to the one this Activity wants. Don't always do this because of the
         // overhead of reading the user from SharedPreferences.
         if (mSite.mSiteid != currentSite.mSiteid) {
            app.setSite(mSite);
         }
      } else {
         mActivityid = generateActivityid();
         setUserid();

         Site siteArgument = (Site)getIntent().getSerializableExtra(SITE_ARGUMENT);
         if (siteArgument != null && siteArgument.mSiteid != currentSite.mSiteid) {
            mSite = siteArgument;
            app.setSite(mSite);
         } else {
            mSite = app.getSite();
         }
      }

      /**
       * Set the current site's theme. Must be before onCreate because of
       * inflating views.
       */
      setTheme(app.getSiteTheme());
      super.onCreate(savedState);

      /**
       * There is another register call in onResume but we also need it here for the onUnauthorized
       * call that is usually triggered in onCreate of derived Activities.
       */
      App.getBus().register(this);
      App.getBus().register(mBaseActivityListener);

      if (App.inDebug()) {
         ViewServer.get(this).addWindow(this);
      }

      Api.retryDeadEvents(this);
   }

   /**
    * Returns a unique integer for use as an activity id.
    */
   private static int sActivityIdCounter = 0;
   private int generateActivityid() {
      return sActivityIdCounter++;
   }

   public int getActivityid() {
      return mActivityid;
   }

   public void setTitle(String title) {
      getSupportActionBar().setTitle(title);
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);

      outState.putInt(ACTIVITY_ID, mActivityid);
      outState.putInt(USERID, mUserid);
      outState.putSerializable(SITE, mSite);
   }

   /**
    * If the user is coming back to this Activity make sure they still have
    * permission to view it. onRestoreInstanceState is for Activities that are
    * being recreated and onRestart is for Activities who are merely being
    * restarted. Unfortunately both are needed.
    */
   @Override
   public void onRestoreInstanceState(Bundle savedState) {
      super.onRestoreInstanceState(savedState);
      finishActivityIfPermissionDenied();
   }

   @Override
   public void onStart() {
      super.onStart();

      overridePendingTransition(0, 0);
   }

   @Override
   public void onRestart() {
      super.onRestart();
      finishActivityIfPermissionDenied();
   }

   @Override
   public void onResume() {
      super.onResume();

      App.getBus().register(this);
      App.getBus().register(mBaseActivityListener);

      if (App.inDebug()) {
         ViewServer.get(this).setFocusedWindow(this);
      }
   }

   @Override
   protected void onPostResume() {
      super.onPostResume();

      /**
       * This covers missed events caused by dialogs or other views causing the
       * Activity's onPause method to be called which unregisters the Activity
       * as well as returning to an already running Activity via the back button.
       */
      Api.retryDeadEvents(this);
      if (mPendingGoogleSigninResult != null) {
         App.getBus().post(mPendingGoogleSigninResult);
         mPendingGoogleSigninResult = null;
      }
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      if (App.inDebug()) {
         ViewServer.get(this).removeWindow(this);
      }
   }

   @Override
   public void onPause() {
      super.onPause();

      App.getBus().unregister(this);
      App.getBus().unregister(mBaseActivityListener);
   }

   public void setDrawerContent(int layoutid) {
      LayoutInflater inflater = (LayoutInflater) this
       .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      View v = inflater.inflate(layoutid, mContentFrame, false);
      mContentFrame.addView(v);
   }

   public boolean openLoginDialogIfLoggedOut() {
      if (!App.get().isUserLoggedIn()) {
         LoginFragment.newInstance().show(getSupportFragmentManager(), "LoginFragment");
         return true;
      } else {
         return false;
      }
   }

   public void onLogin(LoginEvent.Login event) {
      setUserid();
   }

   public void onLogout(LoginEvent.Logout event) {
      /**
       * Check permissions before setting mUserid. Otherwise the Activity
       * will never be finished because mUserid matches the currently logged
       * in user.
       */
      finishActivityIfPermissionDenied();
      setUserid();
   }

   public void onCancelLogin(LoginEvent.Cancel event) {
      finishActivityIfPermissionDenied();
   }

   /**
    * Sets the userid to the currently logged in user's userid.
    */
   private void setUserid() {
      User user = App.get().getUser();
      mUserid = user == null ? LOGGED_OUT_USERID : user.getUserid();
   }

   /**
    * Called when the custom menu title is clicked. This is only applicable
    * for Dozuki because of how the custom logo is displayed in the action bar.
    */
   protected void onCustomMenuTitleClick(View v) {
      // Finish the Activity because this is the "up" action by default.
      finish();
   }

   /**
    * Finishes the Activity if the user should be logged in but isn't.
    */
   private void finishActivityIfPermissionDenied() {
      App app = App.get();
      User user = app.getUser();
      int currentUserid = user == null ? LOGGED_OUT_USERID : user.getUserid();

      // Never finish the activity if the user is logging in.
      if (neverFinishActivityOnLogout() || app.isLoggingIn()) {
         return;
      }

      // Finish if the site is private or activity requires authentication.
      if ((currentUserid == LOGGED_OUT_USERID || currentUserid != mUserid) &&
       (finishActivityIfLoggedOut() || !app.getSite().mPublic)) {
         Intent intent = getIntent();
         finish();
         startActivity(intent);
      }
   }

   /**
    * Returns true if the Activity should be finished if the user logs out or
    * cancels authentication.
    */
   public boolean finishActivityIfLoggedOut() {
      return false;
   }

   /**
    * Returns true if the Activity should never be finished despite meeting
    * other conditions.
    *
    * This exists because of a race condition of sorts involving logging out of
    * private Dozuki sites. SiteListActivity can't reset the current site to
    * one that is public so it is erroneously finished unless flagged
    * otherwise.
    */
   public boolean neverFinishActivityOnLogout() {
      return false;
   }

   public void showLoading(int container) {
      showLoading(container, getString(R.string.loading));
   }

   public void showLoading(int container, String message) {
      Bundle args = new Bundle();
      args.putString(LoadingFragment.TEXT_KEY, message);
      LoadingFragment frag = new LoadingFragment();
      frag.setArguments(args);
      getSupportFragmentManager().beginTransaction()
       .add(container, frag, LOADING)
       .commit();
   }

   public void hideLoading() {
      Fragment loadingFragment = getSupportFragmentManager().findFragmentByTag(LOADING);
      if (loadingFragment != null) {
         // Because this is only hiding the loading fragment, it's fine to
         // commit with possible state loss.
         getSupportFragmentManager().beginTransaction()
          .remove(loadingFragment)
          .commitAllowingStateLoss();
      }
   }

   public static Intent addSite(Intent intent, Site site) {
      intent.putExtra(SITE_ARGUMENT, site);
      return intent;
   }
}
