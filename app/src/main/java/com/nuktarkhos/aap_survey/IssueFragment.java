package com.nuktarkhos.aap_survey;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nuktarkhos.aap_survey.data.AppDatabase;
import com.nuktarkhos.aap_survey.data.Issue;
import com.nuktarkhos.aap_survey.data.Survey;
import com.nuktarkhos.aap_survey.databinding.FragmentIssueBinding;
import com.nuktarkhos.aap_survey.task.TaskRunner;
import com.nuktarkhos.aap_survey.utility.Locative;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class IssueFragment extends Fragment {

    FragmentIssueBinding binding;
    private AppDatabase surveyDb;
    private MainActivity parent;

    boolean updating = false;
    int issueId;

    ActivityResultLauncher<Intent> photoActivityResultLauncher;

    boolean hasPicture = false;
    String photoUri;
    byte[] thumbnail;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        Bundle bundle = getArguments();

        if (bundle != null) {
            issueId = getArguments().getInt("issueId", -1);
            if (issueId != -1) updating = true;
        }

        final Bitmap[] issueImage = new Bitmap[1];

        photoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        TaskRunner taskRunner = new TaskRunner();
                        taskRunner.executeAsync(() -> {

                            issueImage[0] = BitmapFactory.decodeFile(photoUri);
                            Bitmap thumbnailImage = ThumbnailUtils.extractThumbnail(issueImage[0],
                                    240, 240);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            thumbnailImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            thumbnail = stream.toByteArray();



                            return Boolean.TRUE;
                        }, (result1 -> {

                            if (result1 != null) {
                                binding.issuePicture.setImageBitmap(issueImage[0]);
                                hasPicture = true;
                            }
                        }));

                    } else {
                        photoUri = null;
                        hasPicture = false;
                    }

                    if (photoUri == null)
                        Toast.makeText(getContext(), R.string.failed_picture, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parent = (MainActivity)getActivity();

        if (parent != null) {
            surveyDb = parent.getSurveyDb();
        }

        binding = FragmentIssueBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        if (!updating) initializeIssue();
        else loadIssue();

        binding.issuePicture.setOnClickListener(view1 -> {
            addIssuePhoto();
        });

        binding.saveIssueButton.setOnClickListener(view1 -> {
            saveIssue();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addIssuePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        Uri photoOutput;

        if (photoUri != null){
            photoFile = new File(photoUri);

            if (photoFile.exists()){
                photoFile.delete();
                photoUri = null;
            }
        }

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.w("AAP.Issue", ex.toString());
        }

        if (photoFile != null) {
            photoUri = photoFile.getAbsolutePath();

            photoOutput = FileProvider.getUriForFile(parent,
                    parent.getApplicationContext().getPackageName() + ".provider",
                    photoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutput);

            photoActivityResultLauncher.launch(intent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File path = parent.getFilesDir();
        File folder = new File(path, "issues");

        folder.mkdir();

        return new File(folder,timeStamp + "_issue.jpg");
    }

    private void initializeIssue(){
        Calendar calendar = Calendar.getInstance();
        String time = String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        String[] locs = Locative.gpsToString(parent.getLocation());

        binding.issueLatitudeTextEdit.setText(locs[0]);
        binding.issueLongitudeTextEdit.setText(locs[1]);
        binding.issueTimeTextEdit.setText(time);
    }

    private void loadIssue(){
        binding.issuePicture.setClickable(false);
        binding.saveIssueButton.setClickable(false);

        Survey[] surveys = surveyDb.surveyDao().getActiveSurvey();

        binding.progressBar.setVisibility(VISIBLE);

        if (surveys.length == 1){
            final Issue[] issue = new Issue[1];

            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(() -> {

                issue[0] = surveyDb.issueDao().getIssue(surveys[0].id, issueId);

                return Boolean.TRUE;
            }, (result1 -> {


                if (result1){
                    binding.issueLatitudeTextEdit.setText(issue[0].latitude);
                    binding.issueLongitudeTextEdit.setText(issue[0].longitude);
                    binding.typeSpinner.setSelection(issue[0].type);
                    binding.statusSpinner.setSelection(issue[0].severity);
                    binding.editTextNotes.setText(issue[0].notes);
                    binding.issueTimeTextEdit.setText(issue[0].time);

                    photoUri = issue[0].pictureURI;
                    thumbnail = issue[0].thumbnail;

                    if (photoUri != null)    {
                        Bitmap issueImage = BitmapFactory.decodeFile(photoUri);
                        binding.issuePicture.setImageBitmap(issueImage);
                        hasPicture = true;
                    }
                } else {
                    Toast.makeText(getContext(), R.string.failed_load, Toast.LENGTH_SHORT).show();
                    issueId = -1;
                    updating = false;
                }

                binding.progressBar.setVisibility(INVISIBLE);

                binding.issuePicture.setClickable(true);
                binding.saveIssueButton.setClickable(true);
            }));
        }
    }

    private void saveIssue(){
        if (!hasPicture) {
            Toast.makeText(getContext(), R.string.no_picture, Toast.LENGTH_SHORT).show();
        } else {
            binding.issuePicture.setClickable(false);
            binding.saveIssueButton.setClickable(false);

            Survey[] surveys = surveyDb.surveyDao().getActiveSurvey();

            binding.progressBar.setVisibility(VISIBLE);

             if (surveys.length == 1){

                Issue issue = new Issue();

                issue.latitude = binding.issueLatitudeTextEdit.getText().toString();
                issue.longitude = binding.issueLongitudeTextEdit.getText().toString();
                issue.type = binding.typeSpinner.getSelectedItemPosition();
                issue.severity = binding.statusSpinner.getSelectedItemPosition();
                issue.surveyId = surveys[0].id;
                issue.notes = binding.editTextNotes.getText().toString();
                issue.time = binding.issueTimeTextEdit.getText().toString();

                if (photoUri != null)
                    issue.pictureURI = photoUri;

                issue.thumbnail = thumbnail;

                TaskRunner taskRunner = new TaskRunner();
                taskRunner.executeAsync(() -> {

                    if (updating){
                        issue.id = issueId;
                        surveyDb.issueDao().updateIssue(issue);
                    } else {
                        issue.id = 0;
                        surveyDb.issueDao().addIssue(issue);
                    }

                    return Boolean.TRUE;
                }, (result1 -> {

                    binding.progressBar.setVisibility(INVISIBLE);

                    binding.issuePicture.setClickable(true);
                    binding.saveIssueButton.setClickable(true);

                    if (result1 == null){
                        Toast.makeText(getContext(), R.string.failed_save, Toast.LENGTH_SHORT).show();
                    } else {
                        NavHostFragment.findNavController(IssueFragment.this)
                                .navigate(R.id.action_saveIssue_pop);
                    }
                }));
            }
        }
    }
}