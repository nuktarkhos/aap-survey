package com.nuktarkhos.aap_survey.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nuktarkhos.aap_survey.data.Issue;
import com.nuktarkhos.aap_survey.data.Survey;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class IssueWriter {

    private final FileWriter fileWriter;
    private final Survey survey;
    private final List<Issue> issues;

    public IssueWriter(FileWriter newFileWriter, Survey newSurvey, List<Issue> newIssues){
        fileWriter = newFileWriter;
        survey = newSurvey;
        issues = newIssues;
    }

    public void writeIssues(){
        int issueNumber = 1;

        writeHeader(survey);

        for (Issue issue : issues){
            String title = "Issue " + issueNumber++;
            writeIssue(issue, title);
        }

        writeTail();

        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ioe){
            Log.e("AAP.html", ioe.toString());
        }
    }

   private void writeHeader(Survey survey){
        writeLine("<!DOCTYPE html>");
        writeLine("<html>");
        writeLine("<body>");
        writeLine("<h2>" + survey.path + " survey " + survey.date + "</h2>");
    }

    private void writeIssue(Issue issue, String title){

        String imageB64 = bitmapToBase64(issue.pictureURI);

        writeLine("<hr/>");
        writeLine("<h3>" + title + "</h3>");
        writeLine("<p><b>Time:</b> " + issue.time + "</p>");
        writeLine("<p><b>Latitude:</b> " + issue.latitude + "</p>");
        writeLine("<p><b>Longitude:</b> " + issue.longitude + "</p>");
        writeLine("<p><b>Issue:</b> " + IssueType.toString(issue.type) + "</p>");
        writeLine("<p><b>Status:</b> " + IssueSeverity.toString(issue.severity) + "</p>");
        writeLine("<p><b>Notes:</b> " + issue.notes + "</p>");
        writeLine("<br>");
        writeLine("<img style=\"max-width:100%;\" src=\"data:image/jpeg;base64," + imageB64 + "\">");
        writeLine("<br>");
    }

    private void writeTail(){
        writeLine("</body>");
        writeLine("</html>");

    }

    private void writeLine(String string){

        try {
            fileWriter.write(string);
            fileWriter.write("\r\n");
        } catch (IOException e){
            Log.e("AAP.html", e.toString());
        }
    }

    private static String bitmapToBase64(String uri){
        Bitmap photo = BitmapFactory.decodeFile(uri);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }
}
