package com.dozuki.ifixit.ui.topic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.topic.TopicLeaf;
import com.dozuki.ifixit.model.topic.TopicNode;
import com.dozuki.ifixit.ui.BaseActivity;
import com.dozuki.ifixit.ui.BaseFragment;
import com.dozuki.ifixit.ui.topic.adapters.TopicPageAdapter;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiCall;
import com.dozuki.ifixit.util.api.ApiEvent;
import com.google.android.material.tabs.TabLayout;
import com.squareup.otto.Subscribe;

import androidx.viewpager.widget.ViewPager;

public class TopicViewFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
   private static final String CURRENT_PAGE = "CURRENT_PAGE";
   private static final String CURRENT_TOPIC_LEAF = "CURRENT_TOPIC_LEAF";
   private static final String CURRENT_TOPIC_NODE = "CURRENT_TOPIC_NODE";

   private TopicNode mTopicNode;
   private TopicLeaf mTopicLeaf;
   private TopicPageAdapter mPageAdapter;
   private ViewPager mPager;

   private int mSelectedTab = -1;
   private TabLayout mTabs;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.topic_view_fragment, container, false);

      Bundle args = getArguments();

      mPager = (ViewPager) view.findViewById(R.id.topic_view_view_pager);
      mTabs = (TabLayout) view.findViewById(R.id.tab_layout);

      mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
      mTabs.setVisibility(View.VISIBLE);

      if (savedInstanceState != null) {
         mSelectedTab = savedInstanceState.getInt(CURRENT_PAGE, 0); // Default to Guide page
         mTopicNode = (TopicNode) savedInstanceState.getSerializable(CURRENT_TOPIC_NODE);
         TopicLeaf topicLeaf = (TopicLeaf) savedInstanceState.getSerializable(CURRENT_TOPIC_LEAF);

         if (topicLeaf != null) {
            setTopicLeaf(topicLeaf);
         } else if (mTopicNode != null) {
            getTopicLeaf(mTopicNode.getName());
         }
      } else if (args != null) {
         if (args.containsKey(TopicViewActivity.TOPIC_NAME_KEY)) {
            getTopicLeaf(args.getString(TopicViewActivity.TOPIC_NAME_KEY));
         }
      }
      return view;
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);

      outState.putInt(CURRENT_PAGE, mSelectedTab);
      outState.putSerializable(CURRENT_TOPIC_LEAF, mTopicLeaf);
      outState.putSerializable(CURRENT_TOPIC_NODE, mTopicNode);
   }

   @Subscribe
   public void onTopic(ApiEvent.Topic event) {
      if (!event.hasError()) {
         setTopicLeaf(event.getResult());
      } else {
         Api.getErrorDialog(getActivity(), event).show();
      }
   }

   public boolean isDisplayingTopic() {
      return mTopicLeaf != null;
   }

   public void setTopicNode(TopicNode topicNode) {
      if (topicNode == null) {
         mTopicNode = null;
         mTopicLeaf = null;
         return;
      }

      if (mTopicNode == null || !mTopicNode.equals(topicNode)) {
         getTopicLeaf(topicNode.getName());
      } else {
         selectDefaultTab();
      }

      mTopicNode = topicNode;
   }

   public void setTopicLeaf(TopicLeaf topicLeaf) {
      if (topicLeaf != null) {
         mTopicLeaf = topicLeaf;
         if (mTopicNode != null && !topicLeaf.getName().equals(mTopicNode.getName())) {
            // Not the most recently selected topic... wait for another.
            return;
         } else if (mTopicLeaf != null && mTopicLeaf.equals(topicLeaf)) {
            ((BaseActivity)getActivity()).getSupportActionBar().setTitle(mTopicNode.getName());

            mPageAdapter = new TopicPageAdapter(getFragmentManager(), getActivity(), mTopicLeaf);
            mPager.setAdapter(mPageAdapter);
            mTabs.setupWithViewPager(mPager);
            //selectDefaultTab();

            return;
         }
      }

      // display error message
      return;

   }

   @Override
   public void onPageScrolled(int i, float v, int i2) {

   }

   @Override
   public void onPageSelected(int position) {
      App.sendScreenView(mPageAdapter.getFragmentScreenLabel(position));
   }

   @Override
   public void onPageScrollStateChanged(int i) {

   }

   private void selectDefaultTab() {
      if (mTopicLeaf == null) {
         return;
      }

      ((BaseActivity) getActivity()).hideLoading();
   }

   private void getTopicLeaf(String topicName) {
      mTopicLeaf = null;
      mSelectedTab = -1;

      Api.call(getActivity(), ApiCall.topic(topicName));
   }

   public TopicLeaf getTopicLeaf() {
      return mTopicLeaf;
   }

   public TopicNode getTopicNode() {
      return mTopicNode;
   }

}
