package com.nuktarkhos.aap_survey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.nuktarkhos.aap_survey.data.Survey;
import com.nuktarkhos.aap_survey.databinding.FragmentSurveyBinding;
import com.nuktarkhos.aap_survey.utility.WeatherType;

public class SurveyFragment extends Fragment {

    private FragmentSurveyBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSurveyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setScreenText();

        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                persistUserDetails();
                startSurvey();

                NavHostFragment.findNavController(SurveyFragment.this)
                        .navigate(R.id.action_startOrContinueSurvey);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setScreenText(){
        MainActivity parentActivity = (MainActivity)getActivity();

        if (parentActivity != null) {
            if (parentActivity.getUsername() != null) {
                binding.userNameEditText.setText(parentActivity.getUsername(),
                        TextView.BufferType.EDITABLE);
            }

            if (parentActivity.getPathname() != null){
                binding.pathNameEditText.setText(parentActivity.getPathname(),
                        TextView.BufferType.EDITABLE);
            }

            if (parentActivity.getSurveyDate() != null){
                binding.dateTextbox.setText(parentActivity.getSurveyDate());
            }

            if (parentActivity.isCompletingSurvey()){
                binding.startButton.setText(R.string.complete);
            }
        }
    }

    private void persistUserDetails(){
        MainActivity parentActivity = (MainActivity)getActivity();

        if (parentActivity != null) {
            parentActivity.persistUsername(binding.userNameEditText.getText().toString());
            parentActivity.persistPathname(binding.pathNameEditText.getText().toString());
        }
    }

    private void startSurvey(){
        MainActivity parentActivity = (MainActivity)getActivity();
        Survey survey = new Survey();

        if ((parentActivity != null) && !parentActivity.isCompletingSurvey()){
            survey.id = 0;
            survey.surveyor = parentActivity.getUsername();
            survey.path = parentActivity.getPathname();
            survey.date = parentActivity.getSurveyDate();
            survey.weather = WeatherType.toString(binding.weatherSpinner.getSelectedItemPosition());
            survey.active = Boolean.TRUE;

            parentActivity.getSurveyDb().surveyDao().startSurvey(survey);
        }
    }
}