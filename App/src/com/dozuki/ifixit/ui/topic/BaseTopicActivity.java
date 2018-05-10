package com.dozuki.ifixit.ui.topic;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.ui.BaseMenuDrawerActivity;
import com.dozuki.ifixit.ui.search.SearchActivity;
import com.dozuki.ifixit.util.LocaleManager;

public abstract class BaseTopicActivity extends BaseMenuDrawerActivity
 implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

   protected Menu mMenu;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      mMenu = menu;

      getMenuInflater().inflate(R.menu.topic_list_menu, menu);
      // Retrieve the SearchView and plug it into SearchManager
      final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
      SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

      MenuItem item = menu.findItem(R.id.spinner);
      Spinner spinner = (Spinner) item.getActionView();
      String[] languages = LocaleManager.getAvailableLanguages();

      if (languages.length > 1) {
         // Create an ArrayAdapter using the string array and a default spinner layout
         ArrayAdapter<String> adapter = new ArrayAdapter<String>
          (this, R.layout.language_spinner_item, languages);

         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner.setAdapter(adapter);
         spinner.setSelection(adapter.getPosition(LocaleManager.getLanguage(this)));
         spinner.setOnItemSelectedListener(this);
      } else {
         spinner.setVisibility(View.GONE);
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

   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if (!mUserInteracting || view.getId() != R.id.language_spinner_item) {
         mUserInteracting = true;
         return;
      }

      LocaleManager.setNewLocale(this, LocaleManager.getAvailableLanguages()[position]);

      Intent i = this.getIntent();
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);

      Toast.makeText(this, R.string.language_changed_toast, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onNothingSelected(AdapterView<?> parent) {

   }

   public abstract Intent getLanguageIntent();

}
