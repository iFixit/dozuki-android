package com.dozuki.ifixit.ui.guide.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dozuki.ifixit.R;

public class EmbedViewActivity extends AppCompatActivity {
   public static final String HTML = "HTML";
   private String mHTML;
   private WebView mWebView;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      requestWindowFeature((int) Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

      setContentView(R.layout.embed_view);
      Bundle extras = getIntent().getExtras();
      mHTML = (String) extras.get(HTML);
      mWebView = (WebView) findViewById(R.id.full_screen_web_view);
      WebSettings settings = mWebView.getSettings();
      settings.setUseWideViewPort(true);
      settings.setJavaScriptEnabled(true);
      settings.setLoadWithOverviewMode(true);
      settings.setBuiltInZoomControls(true);
      settings.setAppCacheEnabled(true);
      settings.setCacheMode(WebSettings.LOAD_DEFAULT);
      mWebView.setWebChromeClient(new WebChromeClient() {});
      if (savedInstanceState != null) {
         mWebView.restoreState(savedInstanceState);
      }
      mWebView.loadUrl(mHTML);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (mWebView != null) {
         mWebView.saveState(outState);
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      final WebView webview = (WebView) findViewById(R.id.full_screen_web_view);
      webview.loadData("", "text/html", "utf-8");
   }
}
