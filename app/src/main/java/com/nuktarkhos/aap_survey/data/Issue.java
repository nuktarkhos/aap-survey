package com.nuktarkhos.aap_survey.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.Objects;

@Entity (tableName = "issues")
public class Issue {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "survey_id")
    public long surveyId;
    @ColumnInfo(name = "issue_number")
    public String latitude;
    public String longitude;
    public int type;
    public int severity;
    public String notes;
    public String time;
    public byte[] thumbnail;
    public String pictureURI;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return id == issue.id && surveyId == issue.surveyId && type == issue.type && severity == issue.severity && latitude.equals(issue.latitude) && longitude.equals(issue.longitude) && notes.equals(issue.notes) && time.equals(issue.time) && Arrays.equals(thumbnail, issue.thumbnail) && pictureURI.equals(issue.pictureURI);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, surveyId, latitude, longitude, type, severity, notes, time, pictureURI);
        result = 31 * result + Arrays.hashCode(thumbnail);
        return result;
    }
}
