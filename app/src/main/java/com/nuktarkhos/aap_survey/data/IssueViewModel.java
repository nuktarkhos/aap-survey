package com.nuktarkhos.aap_survey.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class IssueViewModel extends AndroidViewModel {

    private AppDatabase surveyDb;

    private final LiveData<List<Issue>> issues;

    public IssueViewModel(Application application) {
        super(application);

        surveyDb = AppDatabase.getInstance(application.getApplicationContext());

        Survey[] currentSurvey = surveyDb.surveyDao().getActiveSurvey();

        if (currentSurvey.length == 1) {
            issues = surveyDb.issueDao().getAllSurveyIssuesLive(currentSurvey[0].id);
        } else {
            issues = new LiveData<List<Issue>>() {};
        }
    }

    public LiveData<List<Issue>> getAllIssues() {
        return issues;
    }

    public Issue getIssueForPosition(int position){
        return issues.getValue().get(position);
    }
}
