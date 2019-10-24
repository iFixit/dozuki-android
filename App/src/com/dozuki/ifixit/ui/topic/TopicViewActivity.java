package com.dozuki.ifixit.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.dozuki.Site;
import com.dozuki.ifixit.model.topic.TopicLeaf;
import com.dozuki.ifixit.model.topic.TopicNode;
import com.dozuki.ifixit.ui.guide.view.FullImageViewActivity;
import com.dozuki.ifixit.ui.topic.adapters.TopicPageAdapter;
import com.dozuki.ifixit.util.ImageSizes;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiCall;
import com.dozuki.ifixit.util.api.ApiEvent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.viewpager.widget.ViewPager;

public class TopicViewActivity extends BaseTopicActivity {
   public static final String TOPIC_KEY = "TOPIC";
   public static final String TOPIC_NAME_KEY = "TOPIC_NAME_KEY";
   private static final String TOPIC_VIEW_TAG = "TOPIC_VIEW_TAG";

   private AppBarLayout mAppBar;
   private TopicNode mTopicNode;
   private TopicLeaf mTopic;
   private ImageView mBackdropView;
   private ViewPager mPager;
   private TabLayout mTabs;
   private TopicPageAdapter mPageAdapter;
   private CollapsingToolbarLayout mCollapsingToolbar;
   private Site mSite;
   private String mTopicName;

   public static Intent viewTopic(Context context, String topicName) {
      Intent intent = new Intent(context, TopicViewActivity.class);
      intent.putExtra(TOPIC_NAME_KEY, topicName);
      return intent;
   }

   @Override
   public void onCreate(Bundle savedState) {
      super.onCreate(savedState);

      setTheme(App.get().getTransparentSiteTheme());

      mSite = App.get().getSite();

      setContentView(R.layout.topic_view);

      mAppBar = (AppBarLayout) findViewById(R.id.appbar);
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
      mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

      setSupportActionBar(mToolbar);

      if (!mSite.mTheme.equals("white")) {
         mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
      } else {
         mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
      }

      mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
         @Override
         public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            final Drawable upArrow, searchIcon, languageIcon;

            // Appbar is collapsed
            if (verticalOffset == -mCollapsingToolbar.getHeight() + mToolbar.getHeight()) {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp, getTheme());
                  searchIcon = getResources().getDrawable(R.drawable.ic_search_24dp, getTheme());
                  languageIcon = getResources().getDrawable(R.drawable.ic_language_white_24dp, getTheme());
               } else {
                  upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
                  searchIcon = getResources().getDrawable(R.drawable.ic_search_24dp);
                  languageIcon = getResources().getDrawable(R.drawable.ic_language_white_24dp);
               }
            } else {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp, getTheme());
                  searchIcon = getResources().getDrawable(R.drawable.ic_search_black_24dp, getTheme());
                  languageIcon = getResources().getDrawable(R.drawable.ic_language_black_24dp, getTheme());
               } else {
                  upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
                  searchIcon = getResources().getDrawable(R.drawable.ic_search_black_24dp);
                  languageIcon = getResources().getDrawable(R.drawable.ic_language_black_24dp);
               }
            }

            mAppBar.post(new Runnable() {
               @Override
               public void run() {

                  getSupportActionBar().setHomeAsUpIndicator(upArrow);

                  if (mMenu != null) {
                     mMenu.findItem(R.id.action_search).setIcon(searchIcon);
                     mMenu.findItem(R.id.language_change).setIcon(languageIcon);
                  }
               }
            });
         }
      });

      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);

      showLoading(R.id.loading_container);

      mBackdropView = (ImageView) findViewById(R.id.backdrop);
      mPager = (ViewPager) findViewById(R.id.topic_viewpager);
      mTabs = (TabLayout) findViewById(R.id.tab_layout);
      mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
      mTabs.setVisibility(View.VISIBLE);
      mTopicNode = (TopicNode) getIntent().getSerializableExtra(TOPIC_KEY);

      Bundle bundle;
      mTopicName = "";
      String topicTitle = "";

      if (mTopicNode != null) {
         mTopicName = mTopicNode.getDisplayName();
         topicTitle = mTopicNode.getName();
      } else if ((bundle = getIntent().getExtras()) != null) {
         mTopicName = bundle.getString(TOPIC_NAME_KEY);
         topicTitle = bundle.getString(TOPIC_NAME_KEY);
      }

      if (!mTopicName.equals("")) {
         mCollapsingToolbar.setTitle(mTopicName);
         mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_AppCompat_Title);

         if (mSite.mTheme.equals("white")) {
            mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
         } else {
            mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
         }

         if (mSite.mHasTitlePictures) {
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
         }

         App.sendScreenView("/category/" + topicTitle);
         Api.call(this, ApiCall.topic(topicTitle));
      }
   }

   private void loadTopicImage() {
      String url = mTopic.getImage().getPath(ImageSizes.topicMain);
      Picasso
       .with(this)
       .load(url)
       .error(R.drawable.no_image)
       .into(mBackdropView);

      if (mSite.mHasTitlePictures && (url == null || (url.equals("") || url.startsWith(".")))) {
         mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.black));
      } else if (mSite.mHasTitlePictures) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBackdropView.setForeground(null);
         }
      }

      mBackdropView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String url1 = (String) v.getTag();

            if (url1 == null || (url1.equals("") || url1.startsWith("."))) {
               return;
            }

            startActivity(FullImageViewActivity.viewImage(getBaseContext(), url1, false));
         }
      });
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         // Respond to the action bar's Up/Home button
         case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Subscribe
   public void onTopic(ApiEvent.Topic event) {
      hideLoading();
      if (!event.hasError()) {
         mTopic = event.getResult();
         mPageAdapter = new TopicPageAdapter(getSupportFragmentManager(), this, mTopic);
         mPager.setAdapter(mPageAdapter);
         mTabs.setupWithViewPager(mPager);

         loadTopicImage();
      } else {
         Api.getErrorDialog(this, event).show();
      }
   }

   @Override
   public void showLoading(int container) {
      findViewById(container).setVisibility(View.VISIBLE);
      super.showLoading(container);
   }

   @Override
   public void hideLoading() {
      super.hideLoading();
      findViewById(R.id.loading_container).setVisibility(View.GONE);
   }

   @Override
   public Intent getLanguageIntent() {
      Intent i = new Intent(TopicViewActivity.this, TopicViewActivity.class);
      i.putExtra(TOPIC_NAME_KEY, mTopicName);
      return i;
   }
}
