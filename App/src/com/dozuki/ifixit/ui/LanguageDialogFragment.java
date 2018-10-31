package com.dozuki.ifixit.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.dozuki.ifixit.R;
import com.dozuki.ifixit.util.LocaleManager;

public class LanguageDialogFragment extends DialogFragment {
   public static LanguageDialogFragment newInstance() {
      LanguageDialogFragment frag = new LanguageDialogFragment();
      return frag;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      Resources res = getResources();
      final String[] codes = res.getStringArray(R.array.languagesCodes);
      String[] languages = res.getStringArray(R.array.languages);

      for (int i = 0; i < codes.length; i++) {
         if (LocaleManager.getLanguage(getActivity()).equalsIgnoreCase(codes[i])) {
            languages[i] += " " + getResources().getString(R.string.current);
         }
      }

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.select_language)
       .setItems(languages, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
             String languageCode = codes[which];
             ((OnLanguageSelectListener)getActivity()).onLanguageSelect(languageCode);
          }
       });
      return builder.create();
   }

   public interface OnLanguageSelectListener {
      void onLanguageSelect(String languageCode);
   }
}
