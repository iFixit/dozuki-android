/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dozuki.ifixit.model.guide.wizard;

import android.text.TextUtils;

import com.dozuki.ifixit.ui.guide.create.wizard.EditTextFragment;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class EditTextPage extends Page {
   public static final String TEXT_DATA_KEY = "name";

   protected String mDescription = "";
   private String mHint = "";
   private boolean mStripNewlines = true;

   public EditTextPage(ModelCallbacks callbacks) {
      super(callbacks);
   }

   @Override
   public Fragment createFragment() {
      return EditTextFragment.create(getKey());
   }

   @Override
   public void getReviewItems(ArrayList<ReviewItem> dest) {
      dest.add(new ReviewItem(super.getTitle(), mData.getString(TEXT_DATA_KEY), getKey(), -1));
   }

   @Override
   public boolean isCompleted() {
      return !TextUtils.isEmpty(mData.getString(TEXT_DATA_KEY));
   }

   public EditTextPage stripNewlines(boolean shouldStrip) {
      mStripNewlines = shouldStrip;
      return this;
   }

   public boolean shouldStripNewlines() {
      return mStripNewlines;
   }

   public EditTextPage setDescription(String description) {
      mDescription = description;
      return this;
   }

   public EditTextPage setHint(String hint) {
      mHint = hint;
      return this;
   }

   public String getHint() {
      return mHint;
   }

   public String getDescription() {
      return mDescription;
   }
}
