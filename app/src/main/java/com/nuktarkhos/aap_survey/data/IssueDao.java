package com.nuktarkhos.aap_survey.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IssueDao {

    @Query("SELECT * FROM issues where survey_id = :surveyId")
    LiveData<List<Issue>> getAllSurveyIssuesLive(long surveyId);

    @Query("SELECT * FROM issues where survey_id = :surveyId")
    List<Issue> getAllSurveyIssues(long surveyId);

    @Query("SELECT * FROM issues where survey_id = :surveyId and id = :issueId LIMIT 1")
    Issue getIssue(long surveyId, long issueId);

    @Insert()
    void addIssue(Issue issue);

    @Update()
    void updateIssue(Issue issue);

    @Delete()
    void deleteIssue(Issue issue);

    @Delete()
    void deleteAllIssues(Issue[] issues);
}
