package com.nuktarkhos.aap_survey;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.nuktarkhos.aap_survey.data.AppDatabase;
import com.nuktarkhos.aap_survey.data.Issue;
import com.nuktarkhos.aap_survey.data.Survey;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nuktarkhos.aap_survey.databinding.ActivityMainBinding;
import com.nuktarkhos.aap_survey.task.TaskRunner;
import com.nuktarkhos.aap_survey.utility.IssueWriter;

import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences preferences;

    private final int ACCESS_FINE_LOCATION_REQ = 10;
    private final int WRITE_EXTERNAL_REQ = 11;

    boolean permitLocation = false;
    boolean permitWrite = false;

    private String todaysDate;
    private String username;
    private String pathname;
    private Location location;
    private boolean completing = false;

    private boolean reportComplete = false;

    AppDatabase surveyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getPreferences(MODE_PRIVATE);

        surveyDb = AppDatabase.getInstance(getApplicationContext());

        //check if we have an active survey we can restart
        Survey[] activeSurveys = surveyDb.surveyDao().getActiveSurvey();

        if (activeSurveys.length > 1){
            clearSurveys();
        } else if (activeSurveys.length == 1){
            username = activeSurveys[0].surveyor;
            pathname = activeSurveys[0].path;
            todaysDate = activeSurveys[0].date;

            completing = true;
        } else {
            // get today's date
            todaysDate = DateFormat.getDateInstance().format(new Date());

            // check if we have persisted a user name and path name
            username = preferences.getString("username", null);
            pathname = preferences.getString("pathname", null);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    protected void onStart(){
        super.onStart();

        // check we have location permission before going to the next stage
        permitLocation = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                ACCESS_FINE_LOCATION_REQ);

        if (permitLocation){
            permitWrite = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_REQ);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_finish_survey) {
            Survey[] activeSurveys = surveyDb.surveyDao().getActiveSurvey();

            if (activeSurveys.length != 1){
                Toast.makeText(this, R.string.no_survey, Toast.LENGTH_SHORT).show();
            } else {
                Survey survey = activeSurveys[0];

                finishSurvey(survey);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permitLocation = true;
                }

                permitWrite = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_REQ);
            }
            case WRITE_EXTERNAL_REQ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permitWrite = true;
                }
            }
        }
    }

    protected String getUsername(){
        return username;
    }

    protected void persistUsername(String newUsername){
        preferences.edit().putString("username", newUsername).apply();
        username = newUsername;
    }

    protected String getPathname(){
        return pathname;
    }

    protected void persistPathname(String newPathname){
        preferences.edit().putString("pathname", newPathname).apply();
        pathname = newPathname;
    }

    protected void setLocation(Location newLocation){
        location = newLocation;
    }

    protected Location getLocation() {
        return location;
    }

    protected String getSurveyDate(){
        return todaysDate;
    }

    protected boolean isCompletingSurvey(){
        return completing;
    }

    protected AppDatabase getSurveyDb(){
        return surveyDb;
    }

    protected boolean permitLocation(){
        return permitLocation;
    }

    private void finishSurvey(Survey survey){

        binding.saveProgressBar.setVisibility(VISIBLE);

        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(() -> {

            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

            File downloads = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );

            String reportName = pathname.toLowerCase(Locale.ROOT)
                    .replaceAll("\\s+", "_") + date + ".html";

            File report = new File(downloads, reportName);

            FileWriter write = new FileWriter(report);
            IssueWriter issueWriter = new IssueWriter(write, survey,
                    surveyDb.issueDao().getAllSurveyIssues(survey.id));

            issueWriter.writeIssues();

            clearSurveyIssues(survey);
            surveyDb.surveyDao().deleteSurvey(survey);

            binding.saveProgressBar.setVisibility(INVISIBLE);

            return Boolean.TRUE;
        }, (result1 -> {

            if (result1 == Boolean.TRUE) {
                Toast.makeText(this, R.string.report_generated, Toast.LENGTH_SHORT).show();

                Handler h = new Handler();
                Runnable r = this::finish;

                h.postDelayed(r, 2000);
            }
        }));
    }

    private void clearSurveys(){
        Survey[] surveys = surveyDb.surveyDao().getAllSurveys();

        for (Survey survey : surveys) {
            clearSurveyIssues(survey);

            surveyDb.surveyDao().deleteSurvey(survey);
        }
    }

    private void clearSurveyIssues(Survey survey){
        List<Issue> issues = surveyDb.issueDao().getAllSurveyIssues(survey.id);

        for (Issue issue : issues){
            File photo = new File(issue.pictureURI);

            if (photo.exists()) photo.delete();

            surveyDb.issueDao().deleteIssue(issue);
        }
    }

    private boolean checkPermission(String requested, int response){

        if (ContextCompat.checkSelfPermission(this,
                requested) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{requested},
                    response);

            return false;
        }

        return true;
    }
}