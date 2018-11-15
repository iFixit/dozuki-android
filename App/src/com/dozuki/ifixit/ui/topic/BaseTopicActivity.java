package com.dozuki.ifixit.ui.topic;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.ui.BaseMenuDrawerActivity;
import com.dozuki.ifixit.ui.LanguageDialogFragment;
import com.dozuki.ifixit.ui.search.SearchActivity;
import com.dozuki.ifixit.util.LocaleManager;

public abstract class BaseTopicActivity extends BaseMenuDrawerActivity
 implements SearchView.OnQueryTextListener, LanguageDialogFragment.OnLanguageSelectListener {

   protected Menu mMenu;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      mMenu = menu;

      getMenuInflater().inflate(R.menu.topic_list_menu, menu);
      // Retrieve the SearchView and plug it into SearchManager
      final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
      SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
      String hint = getString(R.string.search_site_hint, App.get().getSite().mTitle);

      searchView.setQueryHint(hint);
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

      MenuItem item = menu.findItem(R.id.language_change);
      String[] languages = LocaleManager.getAvailableLanguages();

      item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            showCountryDialog();
            return false;
         }
      });

      if (languages.length > 1) {
         item.setVisible(true);
      }

      return true;
   }

   @Override
   public boolean onQueryTextSubmit(String query) {
      Intent searchIntent = new Intent(this, SearchActivity.class);
      searchIntent.putExtra(SearchManager.QUERY, query);
      searchIntent.setAction(Intent.ACTION_SEARCH);

      startActivity(searchIntent);

      return true; // we start the search activity manually
   }

   @Override
   public boolean onQueryTextChange(String newText) {
      return false;
   }

   public abstract Intent getLanguageIntent();

   @Override
   public void onLanguageSelect(String languageCode) {
      LocaleManager.setNewLocale(this, languageCode);

      Intent i = this.getLanguageIntent();
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);

      Toast.makeText(this, R.string.language_changed_toast, Toast.LENGTH_SHORT).show();
   }

   private void showCountryDialog() {
      LanguageDialogFragment newFragment = LanguageDialogFragment.newInstance();
      newFragment.show(getFragmentManager(), "dialog");
   }
}
