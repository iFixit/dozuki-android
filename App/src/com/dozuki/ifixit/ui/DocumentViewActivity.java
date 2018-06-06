package com.dozuki.ifixit.ui;

import android.os.Bundle;
import android.widget.FrameLayout;

public class DocumentViewActivity extends BaseActivity {
   private static final int CONTENT_VIEW_ID = 10101010;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      FrameLayout frame = new FrameLayout(this);
      frame.setId(CONTENT_VIEW_ID);
      setContentView(frame, new FrameLayout.LayoutParams(
       FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

      if (savedInstanceState == null) {
         // During initial setup, plug in the details fragment.
         DocumentWebViewFragment details = new DocumentWebViewFragment();
         details.setArguments(getIntent().getExtras());
         getSupportFragmentManager().beginTransaction().add(CONTENT_VIEW_ID, details).commit();
      }
   }
}
