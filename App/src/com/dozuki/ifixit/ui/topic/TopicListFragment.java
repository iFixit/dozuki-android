package com.dozuki.ifixit.ui.topic;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.topic.TopicNode;
import com.dozuki.ifixit.ui.BaseActivity;
import com.dozuki.ifixit.ui.BaseFragment;
import com.dozuki.ifixit.ui.topic.adapters.TopicListAdapter;
import com.marczych.androidsectionheaders.SectionHeadersAdapter;
import com.marczych.androidsectionheaders.SectionListView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class TopicListFragment extends BaseFragment
 implements TopicSelectedListener, OnItemClickListener {
   public static final String CURRENT_TOPIC = "CURRENT_TOPIC";

   private TopicSelectedListener topicSelectedListener;
   private TopicNode mTopic;
   private SectionHeadersAdapter mTopicAdapter;
   private Context mContext;
   private SectionListView mListView;

   public static Fragment newInstance(TopicNode topic) {
      TopicListFragment frag = new TopicListFragment();
      Bundle args = new Bundle();
      args.putSerializable(CURRENT_TOPIC, topic);
      frag.setArguments(args);
      return frag;
   }

   /**
    * Required for restoring fragments
    */
   public TopicListFragment() {}

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Bundle b = getArguments();

      if (savedInstanceState != null) {
         mTopic = (TopicNode)savedInstanceState.getSerializable(CURRENT_TOPIC);
      } else if (b != null) {
         mTopic = (TopicNode)b.getSerializable(CURRENT_TOPIC);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.topic_list_fragment, container, false);

      mListView = (SectionListView)view.findViewById(R.id.topicList);
      mListView.getListView().setOnItemClickListener(this);

      setTopic(mTopic);

      return view;
   }

   @Override
   public void onStart() {
      super.onStart();

      App.sendScreenView("/category/" + mTopic.getName());
   }

   private void setupTopicAdapter() {
      // TODO: A lot of of this is specific to iFixit so it should probably
      // be moved to the iFixit block.
      mTopicAdapter = new SectionHeadersAdapter();
      ArrayList<TopicNode> generalInfo = new ArrayList<TopicNode>();
      ArrayList<TopicNode> nonLeaves = new ArrayList<TopicNode>();
      ArrayList<TopicNode> leaves = new ArrayList<TopicNode>();
      TopicListAdapter adapter;

      for (TopicNode topic : mTopic.getChildren()) {
         if (topic.isLeaf()) {
            leaves.add(topic);
         } else {
            nonLeaves.add(topic);
         }
      }

      if (!mTopic.isRoot() && !((TopicActivity)getActivity()).isDualPane()) {
         TopicNode generalTopicNode = new TopicNode(mTopic.getName());
         generalTopicNode.setDisplayName(mTopic.getDisplayName());

         generalInfo.add(generalTopicNode);
         adapter = new TopicListAdapter(mContext, mContext.getString(
          R.string.generalInformation), generalInfo);
         adapter.setTopicSelectedListener(this);
         mTopicAdapter.addSection(adapter);
      }

      if (App.get().getSite().isIfixit()) {
         if (nonLeaves.size() > 0) {
            adapter = new TopicListAdapter(mContext, mContext.getString(R.string.categories),
             nonLeaves);
            adapter.setTopicSelectedListener(this);
            mTopicAdapter.addSection(adapter);
         }

         if (leaves.size() > 0) {
            App app = (App)getActivity().getApplication();

            adapter = new TopicListAdapter(mContext, mContext.getString(R.string.category), leaves);
            adapter.setTopicSelectedListener(this);
            mTopicAdapter.addSection(adapter);
         }
      } else {
         adapter = new TopicListAdapter(mContext, mContext.getString(R.string.categories),
          mTopic.getChildren());
         adapter.setTopicSelectedListener(this);
         mTopicAdapter.addSection(adapter);
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);

      outState.putSerializable(CURRENT_TOPIC, mTopic);
   }

   @Override
   public void onItemClick(AdapterView<?> adapterView, View view, int position,
    long id) {
      mTopicAdapter.onItemClick(null, view, position, id);
   }

   public void onTopicSelected(TopicNode topic) {
      topicSelectedListener.onTopicSelected(topic);
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);

      try {
         topicSelectedListener = (TopicSelectedListener)context;
         mContext = context;
      } catch (ClassCastException e) {
         throw new ClassCastException(context.toString() + " must implement TopicSelectedListener");
      }
   }

   @SuppressWarnings("deprecation")
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
         try {
            topicSelectedListener = (TopicSelectedListener)activity;
            mContext = activity;
         } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TopicSelectedListener");
         }
      }
   }

   private void setTopic(TopicNode topic) {
      mTopic = topic;

      String pageTitle = mTopic.getName().equals("ROOT") ?
       App.get().getSite().mTitle :
       mTopic.getDisplayName();

      ((BaseActivity)getActivity()).getSupportActionBar().setTitle(pageTitle);

      setupTopicAdapter();
      mListView.setAdapter(mTopicAdapter);
   }
}
