package com.nuktarkhos.aap_survey.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SurveyDao {

    @Query("SELECT * FROM surveys")
    Survey[] getAllSurveys();

    @Query("SELECT * FROM surveys where active = 1")
    Survey[] getActiveSurvey();

    @Insert()
    long startSurvey(Survey survey);

    @Delete()
    void deleteSurvey(Survey survey);
}
