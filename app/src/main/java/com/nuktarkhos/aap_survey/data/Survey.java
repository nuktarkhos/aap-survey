package com.nuktarkhos.aap_survey.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "surveys")
public class Survey {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String path;
    public String surveyor;
    public String date;
    public String weather;
    public Boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return id == survey.id && path.equals(survey.path) && surveyor.equals(survey.surveyor) && date.equals(survey.date) && weather.equals(survey.weather) && active.equals(survey.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, surveyor, date, weather, active);
    }
}
