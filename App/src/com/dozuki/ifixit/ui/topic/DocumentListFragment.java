package com.dozuki.ifixit.ui.topic;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.Document;
import com.dozuki.ifixit.ui.BaseFragment;
import com.dozuki.ifixit.ui.DocumentListAdapter;

import java.util.ArrayList;

public class DocumentListFragment extends BaseFragment {
   public static final String ITEMS_KEY = "ITEMS_KEY";
   private ArrayList<Document> mItems;

   public DocumentListFragment() { }

   @Override
   public void onCreate(Bundle savedState) {
      super.onCreate(savedState);

      mItems = (ArrayList<Document>) this.getArguments().getSerializable(ITEMS_KEY);

      if (savedState != null && mItems == null) {
         mItems = (ArrayList<Document>) savedState.getSerializable(ITEMS_KEY);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.recycler_list_view, container, false);

      RecyclerView mRecycleView = (RecyclerView) view.findViewById(R.id.recycler_view);
      mRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

      ViewCompat.setNestedScrollingEnabled(mRecycleView, false);
      LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
      mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

      mRecycleView.setLayoutManager(mLayoutManager);

      RecyclerView.Adapter mRecycleAdapter = new DocumentListAdapter(mItems);
      mRecycleView.setAdapter(mRecycleAdapter);

      return view;
   }

   @Override
   public void onSaveInstanceState(Bundle state) {
      super.onSaveInstanceState(state);

      state.putSerializable(ITEMS_KEY, mItems);
   }

}
