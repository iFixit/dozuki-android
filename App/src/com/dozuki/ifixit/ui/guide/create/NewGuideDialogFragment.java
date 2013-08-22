package com.dozuki.ifixit.ui.guide.create;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dozuki.ifixit.MainApplication;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.model.guide.Guide;

import java.util.regex.Pattern;

public class NewGuideDialogFragment extends SherlockDialogFragment {
    private static final String INVALID_DEVICE_NAME_PATTERN = "[^#<>\\[\\]\\|\\{\\},\\+\\?&\\/\\\\\\%:;]+";

    private static final String GUIDE_KEY = "GUIDE_KEY";
    private Guide mGuide;
    private Spinner mType;
    private EditText mSubject;
    private AutoCompleteTextView mTopic;
    private TextView mSubjectLabel;

    public static NewGuideDialogFragment newInstance(Guide guide) {
       NewGuideDialogFragment frag = new NewGuideDialogFragment();

       Bundle bundle = new Bundle();
       bundle.putSerializable(GUIDE_KEY, guide);
       frag.setArguments(bundle);

       return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       if (savedInstanceState != null) {
          mGuide = (Guide) savedInstanceState.getSerializable(GUIDE_KEY);
       } else {
          mGuide = (Guide) getArguments().getSerializable(GUIDE_KEY);
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
     Bundle savedInstanceState) {

       getDialog().setTitle(getString(R.string.new_guide_details_title));
       View v = inflater.inflate(R.layout.new_guide_fragment_dialog, container, false);

       final int guideTypesArray = MainApplication.get().getSite().isIfixit() ? R.array.ifixit_guide_types : R.array
        .dozuki_guide_types;

       mTopic = (AutoCompleteTextView) v.findViewById(R.id.topic_name_field);
       mTopic.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

          @Override
          public void afterTextChanged(Editable editable) { }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             if (!Pattern.matches(INVALID_DEVICE_NAME_PATTERN, charSequence)) {
                mTopic.setError(getString(R.string.device_invalid_characters_error_message));
             }
          }
       });

       mSubject = (EditText) v.findViewById(R.id.subject_field);
       mSubjectLabel = (TextView) v.findViewById(R.id.subject_label);
       mType = (Spinner) v.findViewById(R.id.guide_types_spinner);

       // Create an ArrayAdapter using the string array and a default spinner layout
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
        guideTypesArray, android.R.layout.simple_spinner_item);

       // Specify the layout to use when the list of choices appears
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       // Apply the adapter to the spinner
       mType.setAdapter(adapter);

       mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             String[] guideTypes = getResources().getStringArray(guideTypesArray);
             int visibility;

             if (!MainApplication.get().getSite().hasSubject(guideTypes[position])) {
                visibility = View.GONE;
                mTopic.setImeOptions(EditorInfo.IME_ACTION_DONE);
             } else {
                visibility = View.VISIBLE;
                mTopic.setImeOptions(EditorInfo.IME_ACTION_NEXT);
             }

             mSubject.setVisibility(visibility);
             mSubjectLabel.setVisibility(visibility);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
             // No need to do anything
          }
       });

       ((TextView) v.findViewById(R.id.topic_name_label)).setText(
        getString(R.string.guide_intro_wizard_guide_topic_title, MainApplication.get().getTopicName()));

       v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             boolean error = false;
             String type = (String) mType.getItemAtPosition(mType.getSelectedItemPosition());
             Editable topic = mTopic.getText();

             if (MainApplication.get().getSite().hasSubject(type)) {
                Editable subject = mSubject.getText();

                if (subject.length() == 0) {
                   error = true;
                   mSubject.setError(getString(R.string.guide_subject_required_error));
                } else {
                   mGuide.setSubject(subject.toString());
                }
             }

             if (topic.length() == 0) {
                error = true;
                mTopic.setError(getString(R.string.topic_name_required_error, MainApplication.get().getTopicName()));
             }

             if (!error) {
                mGuide.setType(type);
                mGuide.setTopic(topic.toString());

                MainApplication.getBus().post(new GuideDetailsChangedEvent(mGuide));
                getDialog().dismiss();
             }
          }
       });

       return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);

       outState.putSerializable(GUIDE_KEY, mGuide);
    }

    @Override
    public void onResume() {
       super.onResume();
       MainApplication.getBus().register(this);
    }

    @Override
    public void onPause() {
       super.onPause();

       MainApplication.getBus().unregister(this);
    }
 }