package com.dozuki.ifixit.ui.guide.create;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class LockableViewPager extends ViewPager {

   private boolean isPagingEnabled;

   public LockableViewPager(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.isPagingEnabled = true;
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      return this.isPagingEnabled && super.onTouchEvent(event);
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent event) {
      return this.isPagingEnabled && super.onInterceptTouchEvent(event);
   }

   public void setPagingEnabled(boolean enabled) {
      this.isPagingEnabled = enabled;
   }

   public boolean isEnabled() {
      return this.isPagingEnabled;
   }
}
