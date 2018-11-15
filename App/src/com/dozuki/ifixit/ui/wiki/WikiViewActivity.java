package com.dozuki.ifixit.ui.wiki;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.Wiki;
import com.dozuki.ifixit.ui.BaseMenuDrawerActivity;
import com.dozuki.ifixit.util.PicassoImageGetter;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiCall;
import com.dozuki.ifixit.util.api.ApiEvent;
import com.squareup.otto.Subscribe;

public class WikiViewActivity extends BaseMenuDrawerActivity {
   private static final String WIKI_TITLE_KEY = "WIKI_TITLE_KEY";
   private static final String WIKI_KEY = "WIKI_KEY";

   private static final float HEADER_SIZE = 1.3f;
   private Wiki mWiki;

   public static Intent viewByTitle(Context context, String title) {
      Intent intent = new Intent(context, WikiViewActivity.class);
      intent.putExtra(WIKI_TITLE_KEY, title);
      return intent;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      super.setDrawerContent(R.layout.wiki_main);

      String wikiTitle = "";

      if (savedInstanceState != null) {
         mWiki = (Wiki) savedInstanceState.getSerializable(WIKI_KEY);
      } else {
         Bundle extras = getIntent().getExtras();
         wikiTitle = extras.getString(WIKI_TITLE_KEY);
      }

      if (mWiki != null) {
         initializeUI(mWiki);
      } else {
         fetchWiki(wikiTitle);
      }
   }
   @Override
   public void onSaveInstanceState(Bundle state) {
      super.onSaveInstanceState(state);

      state.putSerializable(WIKI_KEY, mWiki);
   }

   private void initializeUI(Wiki wiki) {
      hideLoading();

      mWiki = wiki;

      App.sendScreenView("/wiki/view/" + mWiki.title);
      getSupportActionBar().setTitle(mWiki.displayTitle);

      TextView wikiTitle = (TextView) findViewById(R.id.wiki_title);

      TextView wikiText = (TextView) findViewById(R.id.wiki_content);
      wikiTitle.setText(mWiki.displayTitle);
      Html.ImageGetter imgGetter = new PicassoImageGetter(wikiText, getResources());

      Spanned htmlParsed = mWiki.getContentSpanned(imgGetter);

      wikiText.setText(htmlParsed);
      wikiText.setMovementMethod(LinkMovementMethod.getInstance());
   }

   @Subscribe
   public void onWiki(ApiEvent.ViewWiki event) {
      hideLoading();

      if (!event.hasError()) {
         if (mWiki == null) {
            Wiki wiki = event.getResult();
            initializeUI(wiki);
         }
      } else {
         Api.getErrorDialog(this, event).show();
      }
   }

   private void fetchWiki(String title) {
      showLoading(R.id.loading_container);
      Api.call(this, ApiCall.wiki(title));
   }
}
