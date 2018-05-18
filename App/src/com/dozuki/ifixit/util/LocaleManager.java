package com.dozuki.ifixit.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.model.dozuki.Site;

import java.util.Locale;

public class LocaleManager {
   private static final String LANGUAGE_KEY = "language_key";

   public static Context setLocale(Context c) {
      return updateResources(c, getLanguage(c));
   }

   public static Context setNewLocale(Context c, String language) {
      persistLanguage(c, language);
      return updateResources(c, language);
   }

   public static String getLanguage(Context c) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
      return prefs.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage()); // default to system language
   }

   @SuppressLint("ApplySharedPref")
   private static void persistLanguage(Context c, String language) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
      // use commit() instead of apply(), because sometimes we kill the application process immediately
      // which will prevent apply() to finish
      prefs.edit().putString(LANGUAGE_KEY, language).commit();
   }

   private static Context updateResources(Context context, String language) {
      Locale newLocale;

      // if the language code does also contain a region
      if (language.contains("-r") || language.contains("-")) {
         // split the language code into language and region
         final String[] language_region = language.split("\\-(r)?");
         // construct a new Locale object with the specified language and region
         newLocale = new Locale(language_region[0], language_region[1]);
      }
      // if the language code does not contain a region
      else {
         // simply construct a new Locale object from the given language code
         newLocale = new Locale(language);
      }

      Locale.setDefault(newLocale);

      Resources res = context.getResources();
      Configuration config = new Configuration(res.getConfiguration());
      if (Build.VERSION.SDK_INT >= 17) {
         config.setLocale(newLocale);
         config.setLayoutDirection(config.locale);
         context = context.createConfigurationContext(config);
      } else {
         config.locale = newLocale;
         res.updateConfiguration(config, res.getDisplayMetrics());
      }
      return context;
   }

   public static Locale getLocale(Resources res) {
      Configuration config = res.getConfiguration();
      return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
   }

   public static String[] getAvailableLanguages() {
      Site site = App.get().getSite();

      return site.getAvailableLanguages();
   }
}