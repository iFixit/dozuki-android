package com.dozuki.ifixit.ui.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dozuki.ifixit.App;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.user.User;
import com.dozuki.ifixit.ui.BaseDialogFragment;
import com.dozuki.ifixit.util.api.Api;
import com.dozuki.ifixit.util.api.ApiCall;
import com.dozuki.ifixit.util.api.ApiError;
import com.dozuki.ifixit.util.api.ApiEvent;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.otto.Subscribe;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends BaseDialogFragment implements OnClickListener {
   private AppCompatButton mRegister;
   private AppCompatButton mCancelRegister;
   private TextInputEditText mEmail;
   private TextInputEditText mPassword;
   private TextInputEditText mConfirmPassword;
   private TextInputEditText mName;
   private TextView mErrorText;
   private TextView mTermsAgreeText;
   private AppCompatCheckBox mTermsAgreeCheckBox;
   private ProgressBar mLoadingSpinner;

   @Subscribe
   public void onRegister(ApiEvent.Register event) {
      if (!event.hasError()) {
         User user = event.getResult();
         ((App)getActivity().getApplication()).login(user, getEmail(),
          getPassword(), true);

         dismiss();
      } else {
         enable(true);
         ApiError error = event.getError();
         if (error.mType == ApiError.Type.CONNECTION ||
          error.mType == ApiError.Type.PARSE) {
            Api.getErrorDialog(getActivity(), event).show();
         }
         mLoadingSpinner.setVisibility(View.GONE);

         mErrorText.setVisibility(View.VISIBLE);
         mErrorText.setText(error.mMessage);
      }
   }

   public static Fragment newInstance() {
      RegisterFragment frag = new RegisterFragment();
      return frag;
   }

   /**
    * Required for restoring fragments
    */
   public RegisterFragment() {}

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.register_fragment, container, false);

      mEmail = (TextInputEditText) view.findViewById(R.id.edit_login_id);
      mPassword = (TextInputEditText)view.findViewById(R.id.edit_password);
      mConfirmPassword = (TextInputEditText)view.findViewById(R.id.edit_confirm_password);

      // Password fields default to a courier typeface (very annoying) and
      // setting the font-family in xml does nothing, so we have to set it
      // explicitly here
      mPassword.setTypeface(Typeface.DEFAULT);
      mConfirmPassword.setTypeface(Typeface.DEFAULT);
      mName = (TextInputEditText)view.findViewById(R.id.edit_login_username);

      mRegister = (AppCompatButton)view.findViewById(R.id.register_button);
      mCancelRegister = (AppCompatButton)view.findViewById(R.id.cancel_register_button);

      mErrorText = (TextView)view.findViewById(R.id.login_error_text);
      mErrorText.setVisibility(View.GONE);

      mTermsAgreeCheckBox = (AppCompatCheckBox) view.findViewById(R.id.login_agreement_terms_checkbox);
      mTermsAgreeText = (TextView)view.findViewById(R.id.login_agreement_terms_textview);
      if (App.get().getSite().isIfixit()) {
         mTermsAgreeText.setText(R.string.register_agreement);
         mTermsAgreeText.setMovementMethod(LinkMovementMethod.getInstance());
      } else {
         mTermsAgreeCheckBox.setVisibility(View.GONE);
         mTermsAgreeText.setVisibility(View.GONE);
      }

      mLoadingSpinner = (ProgressBar)view.findViewById(R.id.login_loading_bar);
      mLoadingSpinner.setVisibility(View.GONE);

      mRegister.setOnClickListener(this);
      mCancelRegister.setOnClickListener(this);

      return view;
   }

   @Override
   public void onStart() {
      super.onStart();

      App.sendScreenView("/register");
   }

   private void showKeyboard() {
      InputMethodManager in = (InputMethodManager)getActivity()
       .getSystemService(Context.INPUT_METHOD_SERVICE);

      in.toggleSoftInput(InputMethodManager.SHOW_FORCED,
       InputMethodManager.HIDE_IMPLICIT_ONLY);
   }

   private void enable(boolean enabled) {
      mEmail.setEnabled(enabled);
      mPassword.setEnabled(enabled);
      mConfirmPassword.setEnabled(enabled);
      mRegister.setEnabled(enabled);
      mCancelRegister.setEnabled(enabled);
      mName.setEnabled(enabled);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.register_button:
            String email = getEmail();
            String name = mName.getText().toString();
            String password = getPassword();
            String confirmPassword = mConfirmPassword.getText().toString();

            if (password.equals(confirmPassword) && email.length() > 0 &&
             name.length() > 0 && (!App.get().getSite().isIfixit() || mTermsAgreeCheckBox.isChecked())) {
               enable(false);

               mErrorText.setVisibility(View.GONE);
               mLoadingSpinner.setVisibility(View.VISIBLE);
               Api.call(getActivity(),
                ApiCall.register(email, password, name));
            } else {
               if (email.length() <= 0) {
                  mEmail.setError("Email is required to register.");
                  mEmail.requestFocus();
                  //showKeyboard();
               } else if (password.length() <= 0) {
                  mPassword.setError("Password required");
                  mPassword.requestFocus();
                  //showKeyboard();
               } else if (name.length() <= 0) {
                  mName.setError("Username required");
                  mName.requestFocus();
                  //showKeyboard();
               } else if (!password.equals(confirmPassword)) {
                  mPassword.setError(getString(R.string.passwords_do_not_match_error));
                  mPassword.requestFocus();
               } else if (!mTermsAgreeCheckBox.isChecked()) {
                  mErrorText.setText(R.string.terms_unchecked_error);
                  mErrorText.setVisibility(View.VISIBLE);
               }
            }
            break;

          case R.id.cancel_register_button:
              // Go back to login.
             getActivity().getSupportFragmentManager()
              .beginTransaction()
              .remove(this)
              .add(LoginFragment.newInstance(), null)
              .commit();

              break;
       }
   }

   private String getEmail() {
      return mEmail.getText().toString();
   }

   private String getPassword() {
      return mPassword.getText().toString();
   }

   @Override
   public void onCancel(DialogInterface dialog) {
      App.get().cancelLogin();
   }
}
