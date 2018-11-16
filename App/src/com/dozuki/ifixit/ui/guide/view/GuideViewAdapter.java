package com.dozuki.ifixit.ui.guide.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.Guide;
import com.dozuki.ifixit.ui.DocumentWebViewFragment;
import com.dozuki.ifixit.ui.topic.DocumentListFragment;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class GuideViewAdapter extends FragmentStatePagerAdapter {
   private static final int GUIDE_INTRO_POSITION = 0;
   private static final int GUIDE_CONCLUSION_OFFSET = 1;
   private Context mContext;
   private Map<Integer, String> mPageLabelMap;

   private int mStepOffset = 1;

   // Default these to a page number that doesn't exist. They will be updated
   // if the guide has tools and parts.
   private int mToolsPosition = -1;
   private int mPartsPosition = -1;
   private int mConclusionPosition = -1;
   private int mFeaturedDocumentPosition = -1;
   private int mDocumentPosition = -1;

   private Guide mGuide;
   private boolean mIsOfflineGuide;

   public GuideViewAdapter(FragmentManager fm, Context context, Guide guide, boolean isOfflineGuide) {
      super(fm);
      mGuide = guide;
      mIsOfflineGuide = isOfflineGuide;
      mContext = context;

      mPageLabelMap = new HashMap<Integer, String>();

      if (mGuide.getFeaturedDocument() != null) {
         mFeaturedDocumentPosition = mStepOffset;
         mStepOffset++;
      }

      if (guideHasDocuments()) {
         mDocumentPosition = mStepOffset;
         mStepOffset++;
      }

      if (guideHasTools()) {
         mToolsPosition = mStepOffset;
         mStepOffset++;
      }

      if (guideHasParts()) {
         mPartsPosition = mStepOffset;
         mStepOffset++;
      }

      if (!mGuide.isTeardown()) {
         mConclusionPosition = getCount() - 1;
      }
   }

   @Override
   public int getCount() {
      if (mGuide != null) {
         int count = mGuide.getNumSteps() + mStepOffset;
         if (!mGuide.isTeardown()) {
            count +=  GUIDE_CONCLUSION_OFFSET;
         }
         return count;
      } else {
         return 0;
      }
   }

   @Override
   public Fragment getItem(int position) {
      Fragment fragment;
      String label = "/guide/view/" + mGuide.getGuideid();

      if (position == GUIDE_INTRO_POSITION) {
         label += "/intro";
         fragment = new GuideIntroViewFragment();
         Bundle args = new Bundle();
         args.putSerializable(GuideIntroViewFragment.GUIDE_KEY, mGuide);
         fragment.setArguments(args);
      } else if (position == mDocumentPosition) {
         label += "/guide-documents";

         fragment = new DocumentListFragment();
         Bundle args = new Bundle();
         args.putSerializable(DocumentListFragment.ITEMS_KEY, mGuide.getDocuments());
         fragment.setArguments(args);
      } else if (position == mFeaturedDocumentPosition) {
         label += "/featured-document";

         fragment = new DocumentWebViewFragment();
         Bundle args = new Bundle();
         args.putString(DocumentWebViewFragment.URL_KEY, mGuide.getFeaturedDocument().getFullUrl().replace(".pdf", ""));
         fragment.setArguments(args);
      } else if (position == mToolsPosition) {
         label += "/tools";
         fragment = GuidePartsToolsViewFragment.newInstance(mGuide.getTools());
      } else if (position == mPartsPosition) {
         label += "/parts";
         fragment = GuidePartsToolsViewFragment.newInstance(mGuide.getParts());
      } else if (position == mConclusionPosition) {
         label += "/conclusion";
         fragment = GuideConclusionFragment.newInstance(mGuide);
      } else {
         int stepNumber = (position - mStepOffset);
         label += "/" + (stepNumber + 1); // Step title # should be 1 indexed.

         Bundle args = new Bundle();
         args.putSerializable("STEP_KEY", mGuide.getStep(stepNumber));
         args.putBoolean("OFFLINE_KEY", mIsOfflineGuide);

         fragment = new GuideStepViewFragment();
         fragment.setArguments(args);
      }

      mPageLabelMap.put(position, label);
      return fragment;
   }

   public String getFragmentScreenLabel(int key) {
      return mPageLabelMap.get(key);
   }

   @Override
   public void destroyItem(View container, int position, Object object) {
      super.destroyItem(container, position, object);

      mPageLabelMap.remove(position);
   }

   @Override
   public CharSequence getPageTitle(int position) {
      if (position == GUIDE_INTRO_POSITION) {
         return mContext.getResources().getString(R.string.introduction);
      } else if (position == mFeaturedDocumentPosition) {
         return mContext.getResources().getString(R.string.featured_document);
      } else if (position == mDocumentPosition) {
         return mContext.getResources().getString(R.string.attached_documents);
      } else if (position == mToolsPosition) {
         return mContext.getResources().getString(R.string.requiredTools);
      } else if (position == mPartsPosition) {
         return mContext.getResources().getString(R.string.requiredParts);
      } else if (position == mConclusionPosition) {
         return mContext.getResources().getString(R.string.conclusion);
      } else {
         return mContext.getResources().getString(R.string.step_number, (position - mStepOffset) + 1);
      }
   }

   public int getStepOffset() {
      return mStepOffset;
   }

   private boolean guideHasDocuments() {
      return mGuide.getDocuments().size() != 0;
   }

   private boolean guideHasTools() {
      return mGuide.getNumTools() != 0;
   }

   private boolean guideHasParts() {
      return mGuide.getNumParts() != 0;
   }
}
