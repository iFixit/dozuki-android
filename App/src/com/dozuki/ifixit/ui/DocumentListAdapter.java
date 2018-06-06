package com.dozuki.ifixit.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.Document;

import java.util.ArrayList;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.ViewHolder> {
   private final Context mContext;
   private ArrayList<Document> mDocuments = new ArrayList<>();

   public DocumentListAdapter(Context context, ArrayList<Document> documents) {
      mDocuments = documents;
      mContext = context;
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
       .inflate(R.layout.document_list_item, parent, false);

      return new ViewHolder(v);
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      final Document doc = mDocuments.get(position);
      final Context context = holder.title.getContext();
      holder.title.setText(doc.getTitle());
      holder.title.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            // Remove the /.pdf from Dozuki pdf document links.
            // PDF viewers on android error on them.
            String docUrl = doc.getFullUrl().replaceAll("/.pdf", "");

            Intent intent = new Intent(mContext, DocumentViewActivity.class);
            intent.putExtra(DocumentWebViewFragment.URL_KEY, docUrl);
            mContext.startActivity(intent);
         }
      });
   }

   @Override
   public int getItemCount() {
      return mDocuments.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView title;

      public ViewHolder(View itemView) {
         super(itemView);
         title = itemView.findViewById(R.id.document_title);
      }
   }
}
