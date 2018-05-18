package com.dozuki.ifixit.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.dozuki.ifixit.R;

public class LanguageDialogFragment extends DialogFragment {
   public static LanguageDialogFragment newInstance() {
      LanguageDialogFragment frag = new LanguageDialogFragment();
      return frag;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.select_language)
       .setItems(R.array.languages, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
             String languageCode = getResources().getStringArray(R.array.languagesCodes)[which];
             ((OnLanguageSelectListener)getActivity()).onLanguageSelect(languageCode);
          }
       });
      return builder.create();
   }

   public interface OnLanguageSelectListener {
      void onLanguageSelect(String languageCode);
   }
}
